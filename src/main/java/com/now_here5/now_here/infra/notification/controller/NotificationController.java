//package com.now_here5.now_here.infra.notification.controller;
//
//import com.now_here5.now_here.infra.notification.dto.NotificationResponse;
//import com.now_here5.now_here.global.response.ResponseCode;
//import com.now_here5.now_here.global.response.ResponseForm;
//import com.now_here5.now_here.infra.notification.service.NotificationService;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.List;
//
//@Controller
//@Slf4j
//@RequiredArgsConstructor
//@Tag(name = "Notification API", description = "알림 API")
//@RequestMapping("/notification")
//public class NotificationController {
//
//    private final NotificationService notificationService;
//
//    // 알림 확인 페이지
//    @GetMapping("/list")
//    public ResponseEntity<ResponseForm> getNotification() {
//        List<NotificationResponse> notificationResponseList = notificationService.getNotificationList();
//
//        return notificationResponseList != null ?
//                ResponseEntity.ok(ResponseForm.of(ResponseCode.NOTIFICATION_LIST_QUERY_SUCCESS, notificationResponseList)) :
//                ResponseEntity.ok(ResponseForm.of(ResponseCode.NOTIFICATION_LIST_QUERY_FAIL));
//    }
//}
