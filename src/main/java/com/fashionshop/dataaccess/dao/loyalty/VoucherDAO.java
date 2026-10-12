package com.fashionshop.dataaccess.dao.loyalty;

import com.fashionshop.business.entity.Voucher;
import com.fashionshop.common.exception.BadRequestException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.util.JsonUtil;
import com.fashionshop.common.util.RedisUtil;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import jakarta.persistence.EntityManager;
import org.redisson.api.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class VoucherDAO extends AbstractJpaDAO<Voucher, Integer> {

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

    public int countUserVoucherUsage(int userId, String voucherCode) {
        String normalized = voucherCode.trim().toUpperCase();
        String usageKey = VOUCHER_USAGE_KEY_PREFIX + normalized;

        try {
            RMap<String, Integer> map = RedisUtil.getClient().getMap(usageKey);
            Integer count = map.get(String.valueOf(userId));
            if (count != null) return count;
        } catch (Exception ignored) {}

        return execute(em -> {
            Long count = em.createQuery(
                    "SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND UPPER(o.voucherCode) = :code AND o.deletedAt IS NULL", Long.class)
                    .setParameter("userId", userId)
                    .setParameter("code", normalized)
                    .getSingleResult();
            return count.intValue();
        });
    }

    public Voucher getVoucherMetadata(String code) {
        String normalized = code.trim().toUpperCase();
        String metaKey = VOUCHER_META_KEY_PREFIX + normalized;
        RedissonClient redisson = RedisUtil.getClient();

        try {
            RBucket<String> bucket = redisson.getBucket(metaKey);
            String cachedJson = bucket.get();
            if (cachedJson != null) {
                return JsonUtil.getMapper().readValue(cachedJson, Voucher.class);
            }
        } catch (Exception ignored) {}

        Optional<Voucher> opt = findByCode(normalized);
        if (opt.isPresent()) {
            Voucher v = opt.get();
            try {
                redisson.getBucket(metaKey).set(
                JsonUtil.getMapper().writeValueAsString(v), 
                Duration.ofSeconds(VOUCHER_META_CACHE_TTL_SECONDS)
);
            } catch (Exception ignored) {}
            return v;
        }
        return null;
    }

    public void invalidateVoucherCache(String code) {
        if (code != null) {
            RedisUtil.getClient().getBucket(VOUCHER_META_KEY_PREFIX + code.trim().toUpperCase()).delete();
        }
    }

    /**
     * Áp dụng Voucher bảo đảm Atomic & Concurrency Safe qua Redisson
     */
    public AtomicApplyResult atomicApplyVoucher(String code, int userId) {
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

        RAtomicLong totalCounter = redisson.getAtomicLong(totalKey);
        RMap<String, Integer> usageMap = redisson.getMap(usageKey);

        // Cold-start seed Redis nếu chưa tồn tại
        if (!totalCounter.isExists()) {
            totalCounter.set(voucher.getUsedCount());
            totalCounter.expire(Duration.ofSeconds(ttlSeconds));
        }

        if (voucher.getUserUsageLimit() > 0 && !usageMap.containsKey(userStr)) {
            RLock lock = redisson.getLock("lock:voucher:seed:" + normalized + ":" + userStr);
            try {
                if (lock.tryLock(COLD_START_LOCK_TTL_MS, TimeUnit.MILLISECONDS)) {
                    if (!usageMap.containsKey(userStr)) {
                        int priorUsage = countUserVoucherUsage(userId, normalized);
                        usageMap.put(userStr, priorUsage);
                        usageMap.expire(Duration.ofSeconds(ttlSeconds));
                    }
                }
            } catch (Exception ignored) {
            } finally {
                if (lock.isHeldByCurrentThread()) lock.unlock();
            }
        }

        // Tăng Atomic Counter
        long newTotal = totalCounter.incrementAndGet();
        Integer rawUserUsage = usageMap.addAndGet(userStr, 1);
        int newUserUsage = rawUserUsage != null ? rawUserUsage : 1;

        // Rollback nếu vượt giới hạn tổng
        if (voucher.getTotalUsageLimit() > 0 && newTotal > voucher.getTotalUsageLimit()) {
            totalCounter.decrementAndGet();
            usageMap.addAndGet(userStr, -1);
            return AtomicApplyResult.fail(VoucherFailureReason.VOUCHER_USAGE_LIMIT_REACHED);
        }

        // Rollback nếu vượt giới hạn lượt dùng của User
        if (voucher.getUserUsageLimit() > 0 && newUserUsage > voucher.getUserUsageLimit()) {
            totalCounter.decrementAndGet();
            usageMap.addAndGet(userStr, -1);
            return AtomicApplyResult.fail(VoucherFailureReason.USER_USAGE_LIMIT_REACHED);
        }

        // Cập nhật DB
        try {
            executeTransaction(em -> {
                em.createQuery("UPDATE Voucher v SET v.usedCount = v.usedCount + 1 WHERE v.id = :id")
                        .setParameter("id", voucher.getId())
                        .executeUpdate();
                return null;
            });
            return AtomicApplyResult.ok(voucher.getId(), voucher.getCode());
        } catch (Exception ex) {
            // Two-Phase Rollback
            totalCounter.decrementAndGet();
            usageMap.addAndGet(userStr, -1);
            throw ex;
        }
    }

    public void releaseVoucher(String code, int userId) {
        String normalized = code.trim().toUpperCase();
        RedissonClient redisson = RedisUtil.getClient();

        RAtomicLong totalCounter = redisson.getAtomicLong(VOUCHER_TOTAL_KEY_PREFIX + normalized);
        RMap<String, Integer> usageMap = redisson.getMap(VOUCHER_USAGE_KEY_PREFIX + normalized);

        if (totalCounter.isExists()) totalCounter.decrementAndGet();
        usageMap.addAndGet(String.valueOf(userId), -1);

        executeTransaction(em -> {
            em.createQuery("UPDATE Voucher v SET v.usedCount = v.usedCount - 1 WHERE UPPER(v.code) = :code AND v.usedCount > 0")
                    .setParameter("code", normalized)
                    .executeUpdate();
            return null;
        });
    }

    public Voucher applyVoucherSafely(String voucherCode, int userId, EntityManager em) {
        String normalized = voucherCode.trim().toUpperCase();
        Voucher voucher = getVoucherMetadata(normalized);
        if (voucher == null) throw new NotFoundException("Mã voucher không tồn tại");
        if (Boolean.FALSE.equals(voucher.getIsActive())) throw new BadRequestException("Voucher đã bị vô hiệu hóa");

        AtomicApplyResult res = atomicApplyVoucher(normalized, userId);
        if (!res.success()) {
            if (res.reason() == VoucherFailureReason.VOUCHER_USAGE_LIMIT_REACHED)
                throw new BadRequestException("Mã giảm giá đã hết lượt sử dụng toàn hệ thống");
            if (res.reason() == VoucherFailureReason.USER_USAGE_LIMIT_REACHED)
                throw new BadRequestException("Bạn đã dùng hết lượt sử dụng mã voucher này");
            throw new BadRequestException("Voucher không thể áp dụng: " + res.reason());
        }

        return em.find(Voucher.class, voucher.getId());
    }

    public void releaseVoucherSafely(String voucherCode, int userId, EntityManager em) {
        releaseVoucher(voucherCode, userId);
    }
}