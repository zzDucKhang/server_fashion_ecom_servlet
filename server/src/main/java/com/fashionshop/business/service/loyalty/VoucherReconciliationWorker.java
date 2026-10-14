package com.fashionshop.business.service.loyalty;

import com.fashionshop.business.entity.Voucher;
import com.fashionshop.common.util.JPAUtil;
import com.fashionshop.common.util.RedisUtil;
import com.fashionshop.dataaccess.dao.loyalty.VoucherDAO;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VoucherReconciliationWorker {
    private static final Logger logger = Logger.getLogger(VoucherReconciliationWorker.class.getName());
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void start() {
        scheduler.scheduleAtFixedRate(VoucherReconciliationWorker::reconcileVouchers, 1, 24, TimeUnit.HOURS);
    }

    public static void stop() {
        scheduler.shutdown();
    }

    public static void reconcileVouchers() {
        logger.info("[CRON] Bắt đầu tiến trình định kỳ đồng bộ số lượt dùng Voucher...");
        RedissonClient redisson = RedisUtil.getClient();
        var em = JPAUtil.getEntityManager();

        try {
            List<Voucher> activeVouchers = em.createQuery(
                    "SELECT v FROM Voucher v WHERE v.deletedAt IS NULL AND v.isActive = true", Voucher.class)
                    .getResultList();

            for (Voucher v : activeVouchers) {
                String totalKey = VoucherDAO.VOUCHER_TOTAL_KEY_PREFIX + v.getCode().trim().toUpperCase();
                RAtomicLong redisCounter = redisson.getAtomicLong(totalKey);

                if (!redisCounter.isExists()) {
                    long ttlSeconds = Math.max(Duration.between(LocalDateTime.now(), v.getEndDate()).getSeconds() + 86400, 3600);
                    redisCounter.set(v.getUsedCount());
                    redisCounter.expire(Duration.ofSeconds(ttlSeconds));
                    continue;
                }

                long redisCount = redisCounter.get();
                int dbCount = v.getUsedCount();

                if (redisCount != dbCount) {
                    logger.warning(String.format("[CRON] Phát hiện chênh lệch Voucher %s. Redis: %d, DB: %d. Đang đồng bộ...",
                            v.getCode(), redisCount, dbCount));
                    em.getTransaction().begin();
                    em.createQuery("UPDATE Voucher v SET v.usedCount = :used WHERE v.id = :id")
                            .setParameter("used", (int) redisCount)
                            .setParameter("id", v.getId())
                            .executeUpdate();
                    em.getTransaction().commit();
                }
            }
            logger.info("[CRON] Tiến trình đồng bộ Voucher hoàn tất thành công.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.log(Level.SEVERE, "[CRON] Lỗi trong quá trình đồng bộ voucher", e);
        } finally {
            em.close();
        }
    }
}