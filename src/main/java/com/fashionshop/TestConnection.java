package com.fashionshop;

import com.fashionshop.business.entity.User;

import com.fashionshop.common.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class TestConnection {
    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            System.out.println("Dang kiem tra ket noi Database qua JPA...");

            // Query thử danh sách User
            List<User> users = em.createQuery("SELECT u FROM User u", User.class)
                    .setMaxResults(5)
                    .getResultList();

            System.out.println("Ket noi thanh cong! So luong User tim thay: " + users.size());
            for (User u : users) {
                System.out.println("- Email: " + u.getEmail() + " | Ten: " + u.getFullName());
            }
        } catch (Exception e) {
            System.err.println("Ket noi that bai:");
            e.printStackTrace();
        } finally {
            em.close();
            JPAUtil.close();
        }
    }
}