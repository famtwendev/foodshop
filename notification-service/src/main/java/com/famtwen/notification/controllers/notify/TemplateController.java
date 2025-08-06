package com.famtwen.notification.controllers.notify;
import com.famtwen.notification.dto.ApiResponse;
import com.famtwen.notification.entity.NotificationTemplate;
import com.famtwen.notification.dto.notify.CreateTemplateRequest;
import com.famtwen.notification.services.NotificationTemplateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

// 9. Template Management Controller
@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TemplateController {
    NotificationTemplateService templateService;

    @PostMapping
    public ApiResponse<NotificationTemplate> createTemplate(@RequestBody CreateTemplateRequest request) {
        return ApiResponse.<NotificationTemplate>builder()
                          .result(templateService.createTemplate(request))
                          .build();
    }

    @GetMapping("/{templateCode}")
    public ApiResponse<NotificationTemplate> getTemplate(@PathVariable String templateCode) {
        return ApiResponse.<NotificationTemplate>builder()
                          .result(templateService.getTemplate(templateCode))
                          .build();
    }
}