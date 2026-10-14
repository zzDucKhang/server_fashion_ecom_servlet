package com.fashionshop.common.util;

import com.fashionshop.common.exception.AppException;
import com.fashionshop.config.AppConfig;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MailUtil - Tuong thich 100% voi Brevo REST API v3 cua Node.js server,
 * kem co che fallback sang SMTP Gmail va log DEV_OTP.
 */
public class MailUtil {
    private static final Logger logger = Logger.getLogger(MailUtil.class.getName());
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void sendVerificationEmail(String toEmail, String otpCode) {
        String brevoApiKey = AppConfig.BREVO_API_KEY;
        String senderEmail = (AppConfig.BREVO_SENDER_EMAIL != null && !AppConfig.BREVO_SENDER_EMAIL.isBlank())
                ? AppConfig.BREVO_SENDER_EMAIL
                : AppConfig.MAIL_USER;
        String senderName = (AppConfig.BREVO_SENDER_NAME != null && !AppConfig.BREVO_SENDER_NAME.isBlank())
                ? AppConfig.BREVO_SENDER_NAME
                : "Fashion Store";

        // 1. Uu tien gui qua Brevo REST API v3 (giong het Node.js mail.ts)
        if (brevoApiKey != null && !brevoApiKey.isBlank()) {
            try {
                if (senderEmail == null || senderEmail.isBlank()) {
                    senderEmail = "noreply@fashionstore.com";
                }

                String htmlContent = String.format("""
                    <div style="font-family:Arial,sans-serif;max-width:480px;margin:auto;padding:24px;border:1px solid #e5e7eb;border-radius:8px">
                      <h2 style="color:#111827">Mã xác thực của bạn</h2>
                      <p style="color:#374151">Sử dụng mã OTP dưới đây để xác thực tài khoản:</p>
                      <div style="font-size:36px;font-weight:bold;letter-spacing:8px;text-align:center;color:#4f46e5;padding:16px 0">%s</div>
                      <p style="color:#6b7280;font-size:14px">⏱ Mã có hiệu lực trong <strong>15 phút</strong> kể từ khi nhận được email này.</p>
                      <p style="color:#6b7280;font-size:13px">⚠️ Không chia sẻ mã này với bất kỳ ai. Fashion Store sẽ không bao giờ hỏi mã OTP của bạn.</p>
                    </div>
                    """, otpCode);

                String payloadJson = JsonUtil.toJson(java.util.Map.of(
                        "sender", java.util.Map.of("name", senderName, "email", senderEmail),
                        "to", java.util.List.of(java.util.Map.of("email", toEmail)),
                        "subject", "Mã xác thực OTP – Fashion Store",
                        "htmlContent", htmlContent
                ));

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                        .timeout(Duration.ofSeconds(15))
                        .header("accept", "application/json")
                        .header("content-type", "application/json")
                        .header("api-key", brevoApiKey)
                        .POST(HttpRequest.BodyPublishers.ofString(payloadJson, StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    logger.info("[MailUtil] Gửi email OTP thành công qua Brevo API tới " + toEmail);
                    return;
                } else {
                    logger.warning("[MailUtil] Brevo send failed (" + response.statusCode() + "): " + response.body());
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "[MailUtil] Lỗi gửi qua Brevo API tới " + toEmail, e);
            }
        }

        // 2. Fallback sang Jakarta Mail SMTP Gmail (neu cau hinh MAIL_USER & MAIL_PASS)
        String mailUser = AppConfig.MAIL_USER;
        String mailPass = AppConfig.MAIL_PASS;

        if (mailUser != null && !mailUser.isBlank() && mailPass != null && !mailPass.isBlank()) {
            try {
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

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(mailUser, senderName));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Mã xác thực OTP – Fashion Store");

                String htmlContent = String.format("""
                    <div style="font-family:Arial,sans-serif;max-width:480px;margin:auto;padding:24px;border:1px solid #e5e7eb;border-radius:8px">
                      <h2 style="color:#111827">Mã xác thực của bạn</h2>
                      <p style="color:#374151">Sử dụng mã OTP dưới đây để xác thực tài khoản:</p>
                      <div style="font-size:36px;font-weight:bold;letter-spacing:8px;text-align:center;color:#4f46e5;padding:16px 0">%s</div>
                      <p style="color:#6b7280;font-size:14px">⏱ Mã có hiệu lực trong <strong>15 phút</strong> kể từ khi nhận được email này.</p>
                      <p style="color:#6b7280;font-size:13px">⚠️ Không chia sẻ mã này với bất kỳ ai. Fashion Store sẽ không bao giờ hỏi mã OTP của bạn.</p>
                    </div>
                    """, otpCode);

                message.setContent(htmlContent, "text/html; charset=utf-8");
                Transport.send(message);
                logger.info("[MailUtil] Gửi email OTP thành công qua SMTP tới " + toEmail);
                return;
            } catch (Exception e) {
                logger.log(Level.WARNING, "[MailUtil] Lỗi gửi qua SMTP tới " + toEmail, e);
            }
        }

        // 3. Fallback in log DEV_OTP (giong dong 165 cua auth.service.ts ben Node.js)
        logger.info(String.format("[DEV_OTP] Mã OTP xác thực cho %s là: %s", toEmail, otpCode));
    }
}