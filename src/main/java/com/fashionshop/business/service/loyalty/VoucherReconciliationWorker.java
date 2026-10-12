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

public class VoucherReconciliationWorker {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void start() {
        scheduler.scheduleAtFixedRate(VoucherReconciliationWorker::reconcileVouchers, 1, 24, TimeUnit.HOURS);
    }

    public static void stop() {
        scheduler.shutdown();
    }

    public static void reconcileVouchers() {
        System.out.println(">>> [CRON] Starting nightly voucher reconciliation job...");
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
                    System.out.printf(">>> [CRON] Discrepancy detected for Voucher %s. Redis: %d, DB: %d. Syncing DB...%n",
                            v.getCode(), redisCount, dbCount);
                    em.getTransaction().begin();
                    em.createQuery("UPDATE Voucher v SET v.usedCount = :used WHERE v.id = :id")
                            .setParameter("used", (int) redisCount)
                            .setParameter("id", v.getId())
                            .executeUpdate();
                    em.getTransaction().commit();
                }
            }
            System.out.println(">>> [CRON] Voucher reconciliation job finished successfully.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}