package com.famtwen.notification.services;

import com.famtwen.notification.event.NotificationEvent;
import com.famtwen.notification.entity.NotificationHistory;
import com.famtwen.notification.entity.NotificationTemplate;
import com.famtwen.notification.dto.request.EmailRequest;
import com.famtwen.notification.dto.request.Recipient;
import com.famtwen.notification.dto.request.SendEmailRequest;
import com.famtwen.notification.dto.request.Sender;
import com.famtwen.notification.dto.response.EmailResponse;
import com.famtwen.notification.exception.NotificationStatus;
import com.famtwen.notification.exception.AppException;
import com.famtwen.notification.exception.ErrorCode;
import com.famtwen.notification.repository.NotificationHistoryRepository;
import com.famtwen.notification.repository.httpclient.EmailClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 7. Enhanced Email Service
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    EmailClient emailClient;
    NotificationTemplateService templateService;
    NotificationHistoryRepository historyRepository;

    @Value("${notification.brevo-apikey}")
    @NonFinal
    String apiKey;

    @Value("${notification.name}")
    @NonFinal
    String nameNotify;

    @Value("${notification.email}")
    @NonFinal
    String emailNotify;

    public EmailResponse sendEmail(SendEmailRequest request) {
        EmailRequest emailRequest = EmailRequest.builder()
                                                .sender(Sender.builder()
                                                              .name(nameNotify)
                                                              .email(emailNotify)
                                                              .build())
                                                .to(List.of(request.getTo()))
                                                .subject(request.getSubject())
                                                .htmlContent(request.getHtmlContent())
                                                .build();

        try {
            EmailResponse response = emailClient.sendEmail(apiKey, emailRequest);
            log.info("Email sent successfully with messageId: {}", response.getMessageId());
            return response;
        } catch (FeignException e){
            log.error("Failed to send email: {}", e.getMessage());
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }

    public EmailResponse sendTemplatedEmail(NotificationEvent event) {
        // Get template
        NotificationTemplate template = templateService.getTemplate(event.getTemplateCode());

        // Validate required parameters
        templateService.validateRequiredParams(template, event.getParams());

        // Merge default params with provided params
        Map<String, Object> allParams = new HashMap<>(template.getDefaultParams());
        allParams.putAll(event.getParams());

        // Process template
        String subject = StringUtils.hasText(event.getCustomSubject())
                ? event.getCustomSubject()
                : templateService.processTemplate(template.getSubject(), allParams);

        String htmlContent = StringUtils.hasText(event.getCustomBody())
                ? event.getCustomBody()
                : templateService.processTemplate(template.getHtmlContent(), allParams);

        // Create history record
        NotificationHistory history = NotificationHistory.builder()
                                                         .channel(event.getChannel())
                                                         .recipient(event.getRecipient())
                                                         .templateCode(event.getTemplateCode())
                                                         .subject(subject)
                                                         .content(htmlContent)
                                                         .status(NotificationStatus.PENDING)
                                                         .metadata(event.getMetadata())
                                                         .build();

        history = historyRepository.save(history);

        try {
            // Send email
            SendEmailRequest emailRequest = SendEmailRequest.builder()
                                                            .to(Recipient.builder()
                                                                         .email(event.getRecipient())
                                                                         .build())
                                                            .subject(subject)
                                                            .htmlContent(htmlContent)
                                                            .build();

            EmailResponse response = sendEmail(emailRequest);

            // Update history
            history.setStatus(NotificationStatus.SENT);
            history.setExternalMessageId(response.getMessageId());
            historyRepository.save(history);

            return response;

        } catch (Exception e) {
            // Update history with error
            history.setStatus(NotificationStatus.FAILED);
            history.setErrorMessage(e.getMessage());
            historyRepository.save(history);
            throw e;
        }
    }
}
