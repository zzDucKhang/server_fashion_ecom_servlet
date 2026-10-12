package com.fashionshop.common.util;

import com.fashionshop.config.AppConfig;
import com.fashionshop.common.exception.AppException; // Bổ sung import này
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class MailUtil {
    public static void sendVerificationEmail(String toEmail, String otpCode) {
        String mailUser = AppConfig.get("MAIL_USER", "your-email@gmail.com");
        String mailPass = AppConfig.get("MAIL_PASS", "your-app-password");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailUser, mailPass);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailUser, "Fashion Store"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Mã xác thực OTP – Fashion Store");

            String htmlContent = String.format("""
                <div style="font-family:Arial,sans-serif;max-width:480px;margin:auto;padding:24px;border:1px solid #e5e7eb;border-radius:8px">
                    <h2 style="color:#111827">Mã xác thực của bạn</h2>
                    <p style="color:#374151">Sử dụng mã OTP dưới đây để xác thực tài khoản:</p>
                    <div style="font-size:36px;font-weight:bold;letter-spacing:8px;text-align:center;color:#4f46e5;padding:16px 0">%s</div>
                    <p style="color:#6b7280;font-size:14px">⏱ Mã có hiệu lực trong <strong>10 phút</strong> kể từ khi nhận được email này.</p>
                    <p style="color:#6b7280;font-size:13px">⚠️ Không chia sẻ mã này với bất kỳ ai.</p>
                </div>
                """, otpCode);

            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (Exception e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
            // THÊM DÒNG NÀY ĐỂ BÁO LỖI VỀ CLIENT VÀ CHẶN THÔNG BÁO THÀNH CÔNG
            throw new AppException("Hệ thống không thể gửi email xác thực (Lỗi SMTP). Vui lòng kiểm tra lại.", 500);
        }
    }
}