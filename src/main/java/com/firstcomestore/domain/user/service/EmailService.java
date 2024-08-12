package com.firstcomestore.domain.user.service;

import com.firstcomestore.domain.user.exception.EmailSendingException;
import com.firstcomestore.domain.user.exception.EmailTemplateLoadException;
import com.firstcomestore.domain.user.exception.VerificationCodeExpiredException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String TEST_ID_EMAIL = "fasttime123@naver.com";

    public String sendVerificationEmail(String to)
        throws MessagingException, UnsupportedEncodingException {
        String authCode = generateAuthCode();
        MimeMessage message = createMessage(to, authCode);
        try {
            javaMailSender.send(message);
            saveVerificationCode(to, authCode, 5);
            return authCode;
        } catch (MailException ex) {
            throw new EmailSendingException();
        }
    }

    private void saveVerificationCode(String email, String code, long timeout) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(email, code, timeout, TimeUnit.MINUTES);
    }

    public boolean verifyEmailCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(email);
        if (storedCode == null) {
            throw new VerificationCodeExpiredException();
        }
        return storedCode.equals(code);
    }


    private String generateAuthCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(3);
            switch (index) {
                case 0:
                    key.append((char) (random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) (random.nextInt(26) + 65));
                    break;
                case 2:
                    key.append(random.nextInt(10));
                    break;
            }
        }
        return key.toString();
    }

    private MimeMessage createMessage(String to, String authCode)
        throws MessagingException, UnsupportedEncodingException {
        String setFrom = TEST_ID_EMAIL;
        String title = "회원가입 인증 번호";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true,
            StandardCharsets.UTF_8.name());

        String emailTemplate = loadEmailTemplate("email_template.html");

        emailTemplate = emailTemplate.replace("{{authCode}}", authCode);

        helper.setSubject(title);
        helper.setFrom(
            new InternetAddress(setFrom, "FirstComeStore", StandardCharsets.UTF_8.name()));
        helper.setTo(to);
        helper.setText(emailTemplate, true);

        return message;
    }

    private String loadEmailTemplate(String templateName) {
        try {
            Resource resource = new ClassPathResource("templates/" + templateName);
            InputStream inputStream = resource.getInputStream();
            byte[] templateBytes = inputStream.readAllBytes();
            return new String(templateBytes, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new EmailTemplateLoadException();
        }
    }
}
