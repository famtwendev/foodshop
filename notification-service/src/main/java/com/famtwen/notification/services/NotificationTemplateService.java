package com.famtwen.notification.services;
import com.famtwen.notification.entity.NotificationTemplate;
import com.famtwen.notification.dto.notify.CreateTemplateRequest;
import com.famtwen.notification.exception.AppException;
import com.famtwen.notification.exception.ErrorCode;
import com.famtwen.notification.repository.NotificationTemplateRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 5. Template Service
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationTemplateService {
    NotificationTemplateRepository templateRepository;

    public NotificationTemplate getTemplate(String templateCode) {
        return templateRepository.findByTemplateCodeAndActiveTrue(templateCode)
                                 .orElseThrow(() -> new AppException(ErrorCode.TEMPLATE_NOT_FOUND));
    }

    public String processTemplate(String template, Map<String, Object> params) {
        if (template == null || params == null) {
            return template;
        }

        String result = template;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }

        return result;
    }

    public void validateRequiredParams(NotificationTemplate template, Map<String, Object> params) {
        if (template.getRequiredParams() == null) return;

        List<String> missingParams = template.getRequiredParams().stream()
                                             .filter(param -> !params.containsKey(param) || params.get(param) == null)
                                             .collect(Collectors.toList());

        if (!missingParams.isEmpty()) {
            throw new AppException(ErrorCode.MISSING_REQUIRED_PARAMS);
        }
    }

    public NotificationTemplate createTemplate(CreateTemplateRequest request) {
        // Check if template code already exists
        if (templateRepository.findByTemplateCodeAndActiveTrue(request.getTemplateCode()).isPresent()) {
            throw new AppException(ErrorCode.TEMPLATE_CODE_EXISTS);
        }

        NotificationTemplate template = NotificationTemplate.builder()
                                                            .templateCode(request.getTemplateCode())
                                                            .channel(request.getChannel())
                                                            .subject(request.getSubject())
                                                            .htmlContent(request.getHtmlContent())
                                                            .textContent(request.getTextContent())
                                                            .requiredParams(request.getRequiredParams())
                                                            .defaultParams(request.getDefaultParams())
                                                            .description(request.getDescription())
                                                            .active(true)
                                                            .build();

        return templateRepository.save(template);
    }
}