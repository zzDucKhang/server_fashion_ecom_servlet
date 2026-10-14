package com.fashionshop.dataaccess.dao.loyalty;

import com.fashionshop.business.entity.Voucher;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.RedisUtil;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import jakarta.persistence.EntityManager;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VoucherDAO extends AbstractJpaDAO<Voucher, Integer> {

    private static final Logger logger = Logger.getLogger(VoucherDAO.class.getName());

    public static final String VOUCHER_TOTAL_KEY_PREFIX = "voucher:total:";
    public static final String VOUCHER_USAGE_KEY_PREFIX = "voucher:usage:";
    public static final String VOUCHER_META_KEY_PREFIX = "voucher:meta:";
    private static final long COLD_START_LOCK_TTL_MS = 3000;
    private static final int VOUCHER_META_CACHE_TTL_SECONDS = 30;

    public enum VoucherFailureReason {
        VOUCHER_NOT_FOUND,
        VOUCHER_INACTIVE,
        VOUCHER_NOT_STARTED,
        VOUCHER_EXPIRED,
        VOUCHER_USAGE_LIMIT_REACHED,
        USER_USAGE_LIMIT_REACHED
    }

    public record AtomicApplyResult(boolean success, Integer voucherId, String code, VoucherFailureReason reason) {
        public static AtomicApplyResult ok(Integer id, String code) {
            return new AtomicApplyResult(true, id, code, null);
        }
        public static AtomicApplyResult fail(VoucherFailureReason reason) {
            return new AtomicApplyResult(false, null, null, reason);
        }
    }

    public VoucherDAO() {
        super(Voucher.class);
    }

    public List<Voucher> findAllPaginated(int page, int limit, Boolean isActive) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT v FROM Voucher v WHERE v.deletedAt IS NULL");
            if (isActive != null) jpql.append(" AND v.isActive = :isActive");
            jpql.append(" ORDER BY v.createdAt DESC");

            var q = em.createQuery(jpql.toString(), Voucher.class);
            if (isActive != null) q.setParameter("isActive", isActive);
            q.setFirstResult((page - 1) * limit);
            q.setMaxResults(limit);
            return q.getResultList();
        });
    }

    public long countAll(Boolean isActive) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(v) FROM Voucher v WHERE v.deletedAt IS NULL");
            if (isActive != null) jpql.append(" AND v.isActive = :isActive");

            var q = em.createQuery(jpql.toString(), Long.class);
            if (isActive != null) q.setParameter("isActive", isActive);
            return q.getSingleResult();
        });
    }

    public Optional<Voucher> findByCode(String code) {
        if (code == null) return Optional.empty();
        String normalized = code.trim().toUpperCase();
        return execute(em -> {
            List<Voucher> list = em.createQuery(
                    "SELECT v FROM Voucher v WHERE UPPER(v.code) = :code AND v.deletedAt IS NULL", Voucher.class)
                    .setParameter("code", normalized)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public int countUserVoucherUsageFromDb(int userId, String normalizedCode) {
        return execute(em -> {
            Long count = em.createQuery(
                    "SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND UPPER(o.voucherCode) = :code AND o.deletedAt IS NULL", Long.class)
                    .setParameter("userId", userId)
                    .setParameter("code", normalizedCode)
                    .getSingleResult();
            return count != null ? count.intValue() : 0;
        });
    }

    public int countUserVoucherUsage(int userId, String voucherCode) {
        String normalized = voucherCode.trim().toUpperCase();
        String usageKey = VOUCHER_USAGE_KEY_PREFIX + normalized;

        RedissonClient redisson = RedisUtil.getClient();
        if (redisson != null) {
            try {
                RMap<String, String> map = redisson.getMap(usageKey, org.redisson.client.codec.StringCodec.INSTANCE);
                String countStr = map.get(String.valueOf(userId));
                if (countStr != null) {
                    return (int) Double.parseDouble(countStr.trim());
                }
            } catch (Exception e) {
                logger.log(Level.FINE, "Redis getMap failed, falling back to database", e);
            }
        }

        return countUserVoucherUsageFromDb(userId, normalized);
    }

  

    public Voucher getVoucherMetadata(String code) {
        String normalized = code.trim().toUpperCase();
        String metaKey = VOUCHER_META_KEY_PREFIX + normalized;
        RedissonClient redisson = RedisUtil.getClient();

        if (redisson != null) {
            try {
                RBucket<String> bucket = redisson.getBucket(metaKey, StringCodec.INSTANCE);
                String cachedJson = bucket.get();
                if (cachedJson != null) {
                    return JsonUtil.getMapper().readValue(cachedJson, Voucher.class);
                }
            } catch (Exception e) {
                logger.log(Level.FINE, "Redis getBucket failed, evicting corrupted key and falling back to DB", e);
                try {
                    redisson.getBucket(metaKey, StringCodec.INSTANCE).delete();
                } catch (Exception ignored) {}
            }
        }

        Optional<Voucher> opt = findByCode(normalized);
        if (opt.isPresent()) {
            Voucher v = opt.get();
            if (redisson != null) {
                try {
                    redisson.getBucket(metaKey, StringCodec.INSTANCE).set(
                            JsonUtil.getMapper().writeValueAsString(v),
                            Duration.ofSeconds(VOUCHER_META_CACHE_TTL_SECONDS)
                    );
                } catch (Exception e) {
                    logger.log(Level.FINE, "Redis cache voucher metadata failed", e);
                }
            }
            return v;
        }
        return null;
    }

    public void invalidateVoucherCache(String code) {
        RedissonClient redisson = RedisUtil.getClient();
        if (code != null && redisson != null) {
            try {
                redisson.getBucket(VOUCHER_META_KEY_PREFIX + code.trim().toUpperCase(), StringCodec.INSTANCE).delete();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Hàm 2 tham số dùng cho API POST /vouchers/apply độc lập
     */
    public AtomicApplyResult atomicApplyVoucher(String code, int userId) {
        return executeTransaction(em -> atomicApplyVoucher(code, userId, em));
    }

    /**
     * Hàm 3 tham số chạy chung Transaction với OrderService
     * Sử dụng StringCodec.INSTANCE để tương thích 100% với HINCRBY / HINCRBYFLOAT và ioredis của Node.js
     */
    public AtomicApplyResult atomicApplyVoucher(String code, int userId, EntityManager txEm) {
        String normalized = code.trim().toUpperCase();
        Voucher voucher = getVoucherMetadata(normalized);

        if (voucher == null) return AtomicApplyResult.fail(VoucherFailureReason.VOUCHER_NOT_FOUND);
        if (Boolean.FALSE.equals(voucher.getIsActive())) return AtomicApplyResult.fail(VoucherFailureReason.VOUCHER_INACTIVE);

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getStartDate())) return AtomicApplyResult.fail(VoucherFailureReason.VOUCHER_NOT_STARTED);
        if (now.isAfter(voucher.getEndDate())) return AtomicApplyResult.fail(VoucherFailureReason.VOUCHER_EXPIRED);

        RedissonClient redisson = RedisUtil.getClient();
        long ttlSeconds = Math.max(Duration.between(now, voucher.getEndDate()).getSeconds() + 86400, 3600);

        String totalKey = VOUCHER_TOTAL_KEY_PREFIX + normalized;
        String usageKey = VOUCHER_USAGE_KEY_PREFIX + normalized;
        String userStr = String.valueOf(userId);

        RAtomicLong totalCounter = null;
        RMap<String, Integer> usageCounterMap = null;
        boolean redisIncremented = false;

        if (redisson != null) {
            try {
                totalCounter = redisson.getAtomicLong(totalKey);
                // Map kiểu String để put() với StringCodec không bị ClassCastException
                RMap<String, String> usageStringMap = redisson.getMap(usageKey, org.redisson.client.codec.StringCodec.INSTANCE);
                // Map kiểu Integer để gọi addAndGet() trả về đúng kiểu Integer
                usageCounterMap = redisson.getMap(usageKey, org.redisson.client.codec.StringCodec.INSTANCE);

                if (!totalCounter.isExists()) {
                    totalCounter.set(voucher.getUsedCount());
                    totalCounter.expire(Duration.ofSeconds(ttlSeconds));
                }

                if (voucher.getUserUsageLimit() > 0 && !usageStringMap.containsKey(userStr)) {
                    RLock lock = redisson.getLock("lock:voucher:seed:" + normalized + ":" + userStr);
                    try {
                        if (lock.tryLock(COLD_START_LOCK_TTL_MS, TimeUnit.MILLISECONDS)) {
                            if (!usageStringMap.containsKey(userStr)) {
                                int priorUsage = countUserVoucherUsageFromDb(userId, normalized);
                                usageStringMap.put(userStr, String.valueOf(priorUsage));
                                usageStringMap.expire(Duration.ofSeconds(ttlSeconds));
                            }
                        }
                    } catch (Exception ignored) {
                    } finally {
                        if (lock.isHeldByCurrentThread()) lock.unlock();
                    }
                }

                long newTotal = totalCounter.incrementAndGet();
                int newUserUsage;
                try {
                    Integer rawUserUsage = usageCounterMap.addAndGet(userStr, 1);
                    newUserUsage = rawUserUsage != null ? rawUserUsage : 1;
                } catch (Exception codecEx) {
                    logger.warning("Làm sạch key Redis cũ bị sai định dạng: " + usageKey);
                    usageStringMap.delete();
                    int priorUsage = countUserVoucherUsageFromDb(userId, normalized);
                    newUserUsage = priorUsage + 1;
                    usageStringMap.put(userStr, String.valueOf(newUserUsage));
                    usageStringMap.expire(Duration.ofSeconds(ttlSeconds));
                }
                redisIncremented = true;

                if (voucher.getTotalUsageLimit() > 0 && newTotal > voucher.getTotalUsageLimit()) {
                    totalCounter.decrementAndGet();
                    usageCounterMap.addAndGet(userStr, -1);
                    return AtomicApplyResult.fail(VoucherFailureReason.VOUCHER_USAGE_LIMIT_REACHED);
                }

                if (voucher.getUserUsageLimit() > 0 && newUserUsage > voucher.getUserUsageLimit()) {
                    totalCounter.decrementAndGet();
                    usageCounterMap.addAndGet(userStr, -1);
                    return AtomicApplyResult.fail(VoucherFailureReason.USER_USAGE_LIMIT_REACHED);
                }
            } catch (Exception redisEx) {
                logger.log(Level.WARNING, "Redis error during voucher application, falling back to DB validation", redisEx);
                if (voucher.getTotalUsageLimit() > 0 && voucher.getUsedCount() >= voucher.getTotalUsageLimit()) {
                    return AtomicApplyResult.fail(VoucherFailureReason.VOUCHER_USAGE_LIMIT_REACHED);
                }
                if (voucher.getUserUsageLimit() > 0 && countUserVoucherUsageFromDb(userId, normalized) >= voucher.getUserUsageLimit()) {
                    return AtomicApplyResult.fail(VoucherFailureReason.USER_USAGE_LIMIT_REACHED);
                }
            }
        } else {
            if (voucher.getTotalUsageLimit() > 0 && voucher.getUsedCount() >= voucher.getTotalUsageLimit()) {
                return AtomicApplyResult.fail(VoucherFailureReason.VOUCHER_USAGE_LIMIT_REACHED);
            }
            if (voucher.getUserUsageLimit() > 0 && countUserVoucherUsageFromDb(userId, normalized) >= voucher.getUserUsageLimit()) {
                return AtomicApplyResult.fail(VoucherFailureReason.USER_USAGE_LIMIT_REACHED);
            }
        }

        try {
            txEm.createQuery("UPDATE Voucher v SET v.usedCount = v.usedCount + 1 WHERE v.id = :id")
                .setParameter("id", voucher.getId())
                .executeUpdate();
            return AtomicApplyResult.ok(voucher.getId(), voucher.getCode());
        } catch (Exception ex) {
            if (redisIncremented && totalCounter != null && usageCounterMap != null) {
                try {
                    totalCounter.decrementAndGet();
                    usageCounterMap.addAndGet(userStr, -1);
                } catch (Exception ignored) {}
            }
            throw ex;
        }
    }

    public void releaseVoucher(String code, int userId) {
        executeTransaction(em -> {
            releaseVoucherSafely(code, userId, em);
            return null;
        });
    }

    public Voucher applyVoucherSafely(String voucherCode, int userId, EntityManager em) {
        String normalized = voucherCode.trim().toUpperCase();
        Voucher voucher = getVoucherMetadata(normalized);
        if (voucher == null) throw new NotFoundException("Mã voucher không tồn tại");
        if (Boolean.FALSE.equals(voucher.getIsActive())) throw new BadRequestException("Voucher đã bị vô hiệu hóa");

        AtomicApplyResult res = atomicApplyVoucher(normalized, userId, em);
        if (!res.success()) {
            if (res.reason() == VoucherFailureReason.VOUCHER_USAGE_LIMIT_REACHED)
                throw new BadRequestException("Mã giảm giá đã hết lượt sử dụng toàn hệ thống");
            if (res.reason() == VoucherFailureReason.USER_USAGE_LIMIT_REACHED)
                throw new BadRequestException("Bạn đã dùng hết lượt sử dụng mã voucher này");
            if (res.reason() == VoucherFailureReason.VOUCHER_NOT_STARTED)
                throw new BadRequestException("Voucher chưa đến thời gian áp dụng");
            if (res.reason() == VoucherFailureReason.VOUCHER_EXPIRED)
                throw new BadRequestException("Voucher đã hết hạn sử dụng");
            throw new BadRequestException("Voucher không thể áp dụng: " + res.reason());
        }

        return em.find(Voucher.class, voucher.getId());
    }

    public void releaseVoucherSafely(String voucherCode, int userId, EntityManager txEm) {
        String normalized = voucherCode.trim().toUpperCase();
        RedissonClient redisson = RedisUtil.getClient();

        if (redisson != null) {
            try {
                RAtomicLong totalCounter = redisson.getAtomicLong(VOUCHER_TOTAL_KEY_PREFIX + normalized);
                RMap<String, Integer> usageCounterMap = redisson.getMap(VOUCHER_USAGE_KEY_PREFIX + normalized, org.redisson.client.codec.StringCodec.INSTANCE);

                if (totalCounter.isExists()) totalCounter.decrementAndGet();
                usageCounterMap.addAndGet(String.valueOf(userId), -1);
            } catch (Exception e) {
                logger.log(Level.WARNING, "releaseVoucherSafely: Redis decrement error", e);
            }
        }

        txEm.createQuery("UPDATE Voucher v SET v.usedCount = v.usedCount - 1 WHERE UPPER(v.code) = :code AND v.usedCount > 0")
                .setParameter("code", normalized)
                .executeUpdate();
    }
}