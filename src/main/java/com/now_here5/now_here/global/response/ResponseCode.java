package com.now_here5.now_here.global.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * Response Convention
 * - 도메인 별로 나누어 관리
 * - [동사_목적어_SUCCESS] 형태로 생성
 * - 코드는 도메인명 앞에서부터 1~2글자로 사용
 * - 메시지는 "~니다."로 마무리
 */

/*
 * Business Response Code
 * A  : Authentication or Authorization
 * M  : Member
 * S  : Sign-up
 * E  : Event
 */


@Getter
@AllArgsConstructor
public enum ResponseCode {

    // example
    EXAMPLE_SUCCESS(200, "U001", "응답에 성공했습니다.."),

    // Authentication
    AUTHENTICATION_FAIL(401, "A002", "사용자 인증에 실패했습니다."),
    AUTHORIZATION_FAIL(403, "A002", "사용자 권한이 없습니다."),

    // Member
    LOGIN_SUCCESS(200, "M001", "로그인에 성공했습니다."),
    LOGIN_FAIL(200, "M001", "로그인에 실패했습니다."),
    LOGOUT_SUCCESS(200, "M002", "로그아웃에 성공했습니다."),
    LOGOUT_FAIL(200, "M002", "로그아웃에 실패했습니다."),
    DEACTIVATE_SUCCESS(200, "M003", "회원탈퇴에 성공했습니다."),
    DEACTIVATE_FAIL(400, "M003", "회원탈퇴에 실패했습니다."),
    MEMBER_RECOMMEND_SUCCESS(200, "M004", "회원 추천에 성공했습니다."),
    MEMBER_RECOMMEND_FAIL(400, "M004", "회원 추천에 실패했습니다."),
    PROFILE_QUERY_SUCCESS(200, "M005", "프로필 조회에 성공했습니다."),
    PROFILE_QUERY_FAIL(400, "M005", "프로필 조회에 실패했습니다."),
    PERSONAL_INFO_QUERY_SUCCESS(200, "M006", "개인정보 조회에 성공했습니다."),
    PERSONAL_INFO_QUERY_FAIL(400, "M006", "개인정보 조회에 실패했습니다."),
    DESCRIPTION_UPDATE_SUCCESS(200, "M007", "자기소개 수정에 성공했습니다."),
    DESCRIPTION_UPDATE_FAIL(400, "M007", "자기소개 수정에 실패했습니다."),
    NICKNAME_UPDATE_SUCCESS(200, "M008", "닉네임 수정에 성공했습니다."),
    NICKNAME_UPDATE_FAIL(400, "M008", "닉네임 수정에 실패했습니다."),
    MBTI_UPDATE_SUCCESS(200, "M009", "MBTI 수정에 성공했습니다."),
    MBTI_UPDATE_FAIL(400, "M009", "MBTI 수정에 실패했습니다."),
    TOGGLE_NOTIFICATION_SUCCESS(200, "M010", "알림 설정 변경에 성공했습니다."),
    TOGGLE_NOTIFICATION_FAIL(400, "M010", "알림 설정 변경에 실패했습니다."),
//    SNS_ID_UPDATE_SUCCESS(200, "M011", "SNS ID 수정에 성공했습니다."),
//    SNS_ID_UPDATE_FAIL(400, "M011", "SNS ID 수정에 실패했습니다."),
    NOTIFICATION_READ_SUCCESS(200, "M012", "알림 읽음 처리에 성공했습니다."),
    NOTIFICATION_READ_FAIL(400, "M012", "알림 읽음 처리에 실패했습니다."),
    BIRTHDAY_UPDATE_SUCCESS(200, "M013", "생일 수정에 성공했습니다."),
    BIRTHDAY_UPDATE_FAIL(400, "M013", "생일 수정에 실패했습니다."),
    // Sign-up
    SIGNUP_SUCCESS(200, "S001", "회원가입에 성공했습니다."),
    SIGNUP_FAIL(400, "S001", "회원가입에 실패했습니다."),
    SIGNUP_DUPLICATED(400, "S002", "이미 가입된 회원입니다."),

    // nickname
    NICKNAME_DUPLICATED(200, "A001", "중복된 닉네임입니다."),
    NICKNAME_QUALIFIED(200, "A001", "사용 가능한 닉네임입니다."),

    // Phone Verify
    PHONE_DUPLICATED(200, "A003", "현재 이벤트로 이미 가입된 번호입니다."),
    PHONE_VERIFY_REQUEST(200, "A003", "휴대폰 인증을 요청했습니다."),
    PHONE_VERIFY_SUCCESS(200, "A003", "휴대폰 인증에 성공했습니다."),
    PHONE_VERIFY_FAIL(400, "A003", "휴대폰 인증에 실패했습니다."),


    PHONE_QUALIFIED(200, "A004", "사용 가능한 번호입니다."),
    // development - only
    PHONE_GET_SUCCESS(200, "A001-D", "개발용 인증 번호 조회에 성공했습니다."),
    PHONE_GET_FAIL(400, "A001-D", "개발용 인증 번호 조회에 실패했습니다."),

    // Event
    EVENTLIST_QUERY_SUCCESS(200, "E001", "이벤트 목록 조회에 성공했습니다."),
    EVENTLIST_QUERY_FAIL(400, "E001", "이벤트 목록 조회에 실패했습니다."),
    EVENT_QUERY_SUCCESS(200, "E002", "이벤트 조회에 성공했습니다."),
    EVENT_QUERY_FAIL(400, "E002", "이벤트 조회에 실패했습니다."),
    EVENT_TIME_SUCCESS(200, "E003", "이벤트 시간 조회에 성공했습니다."),
    EVENT_TIME_FAIL(400, "E003", "이벤트 시간 조회에 실패했습니다."),
    MY_EVENTS_QUERY_SUCCESS(200, "E004", "내 이벤트 목록 조회에 성공했습니다."),
    MY_EVENTS_QUERY_FAIL(400, "E004", "내 이벤트 목록 조회에 실패했습니다."),
    MEMBER_REACTIVATE_SUCCESS(200, "E005", "회원 재활성화에 성공했습니다."),
    MEMBER_REACTIVATE_FAIL(400, "E005", "회원 재활성화에 실패했습니다."),
    //admin
    EVENT_CLOSE_SUCCESS(200, "E005", "이벤트 종료에 성공했습니다."),
    EVENT_CLOSE_FAIL(400, "E005", "이벤트 종료에 실패했습니다."),
    EVENT_OPEN_SUCCESS(200, "E006", "이벤트 생성에 성공했습니다."),
    EVENT_OPEN_FAIL(400, "E006", "이벤트 생성에 실패했습니다."),
    EVENT_ADD_LOCATION_SUCCESS(200, "E007", "이벤트 장소 추가에 성공했습니다."),
    EVENT_ADD_LOCATION_FAIL(400, "E007", "이벤트 장소 추가에 실패했습니다."),
    EVENT_DELETE_LOCATION_SUCCESS(200, "E008", "이벤트 장소 삭제에 성공했습니다."),
    EVENT_DELETE_LOCATION_FAIL(400, "E008", "이벤트 장소 삭제에 실패했습니다."),
    EVENT_DELETE_SUCCESS(200, "E009", "이벤트 삭제에 성공했습니다."),
    EVENT_DELETE_FAIL(400, "E009", "이벤트 삭제에 실패했습니다."),

    LOCATION_CREATE_SUCCESS(200, "E10", "장소 생성에 성공했습니다."),
    LOCATION_CREATE_FAIL(400, "E10", "장소 생성에 실패했습니다."),
    LOCATION_DELETE_SUCCESS(200, "E11", "장소 삭제에 성공했습니다."),
    LOCATION_DELETE_FAIL(400, "E11", "장소 삭제에 실패했습니다."),
    LOCATION_QUERY_SUCCESS(200, "E12", "장소 조회에 성공했습니다."),
    LOCATION_QUERY_FAIL(400, "E12", "장소 조회에 실패했습니다."),
    LOCATION_LIST_QUERY_SUCCESS(200, "E13", "장소 목록 조회에 성공했습니다."),
    LOCATION_LIST_QUERY_FAIL(400, "E13", "장소 목록 조회에 실패했습니다."),
    EVENTSCHEDULER_QUERY_SUCCESS(200, "E14", "이벤트 스케줄러 조회에 성공했습니다."),
    EVENTSCHEDULER_QUERY_FAIL(400, "E14", "이벤트 스케줄러 조회에 실패했습니다."),
    EVENT_UPDATE_SUCCESS(200, "E15", "이벤트 업데이트에 성공했습니다."),
    EVENT_UPDATE_FAIL(400, "E15", "이벤트 업데이트에 실패했습니다."),
    EVENT_QUERY_BY_TOKEN_SUCCESS(200, "E16", "이벤트 토큰으로 조회에 성공했습니다."),
    EVENT_QUERY_BY_TOKEN_FAIL(400, "E16", "이벤트 토큰으로 조회에 실패했습니다."),

    // Matching
    BANNER_LIST_QUERY_SUCCESS(200, "M001", "배너 목록 조회에 성공했습니다."),
    BANNER_LIST_QUERY_FAIL(400, "M001", "배너 목록 조회에 실패했습니다."),
    LOVE_SEND_SUCCESS(200, "M002", "하트 전송에 성공했습니다."),
    LOVE_SEND_FAIL(400, "M002", "하트 전송에 실패했습니다."),
    LOVE_RECEIVE_SUCCESS(200, "M003", "하트 수신에 성공했습니다."),
    LOVE_RECEIVE_FAIL(400, "M003", "하트 수신에 실패했습니다."),
    LOVE_REJECT_SUCCESS(200, "M004", "하트 거절에 성공했습니다."),
    LOVE_REJECT_FAIL(400, "M004", "하트 거절에 실패했습니다."),
    SUMMARY_GET_SUCCESS(200, "M005", "매칭 현황 조회에 성공했습니다."),
    SUMMARY_GET_FAIL(400, "M005", "매칭 현황 조회에 실패했습니다."),
    SENDER_LIST_QUERY_SUCCESS(200, "M006", "받은 하트 페이지 조회에 성공했습니다."),
    SENDER_LIST_QUERY_FAIL(400, "M006", "받은 하트 페이지 조회에 실패했습니다."),
    RECEIVER_LIST_QUERY_SUCCESS(200, "M007", "보낸 하트 페이지 조회에 성공했습니다."),
    RECEIVER_LIST_QUERY_FAIL(400, "M007", "보낸 하트 페이지 조회에 실패했습니다."),
    SUMMARY_DETAIL_GET_SUCCESS(200, "M008", "매칭 현황 페이지 조회에 성공했습니다."),
    SUMMARY_DETAIL_GET_FAIL(400, "M008", "매칭 현황 페이지 조회에 실패했습니다."),
    NOTIFICATION_LIST_QUERY_SUCCESS(200, "M009", "알림 목록 조회에 성공했습니다."),
    NOTIFICATION_LIST_QUERY_FAIL(400, "M009", "알림 목록 조회에 실패했습니다."),
    NOTIFICATION_COUNT_QUERY_SUCCESS(200, "M010", "알림 개수 조회에 성공했습니다."),
    NOTIFICATION_COUNT_QUERY_FAIL(400, "M010", "알림 개수 조회에 실패했습니다."),

    // Interaction
    FEEDBACK_CREATE_SUCCESS(200, "I001", "FEEDBACK 생성에 성공했습니다."),
    FEEDBACK_CREATE_FAIL(400, "I001", "FEEDBACK 생성에 실패했습니다."),
    INQUIRY_CREATE_SUCCESS(200, "I002", "INQUIRY 생성에 성공했습니다."),
    INQUIRY_CREATE_FAIL(400, "I002", "INQUIRY 생성에 실패했습니다."),
    WITHDRAWAL_REASON_CREATE_SUCCESS(200, "I003", "WITHDRAWAL_REASON 생성에 성공했습니다."),
    WITHDRAWAL_REASON_CREATE_FAIL(400, "I003", "WITHDRAWAL_REASON 생성에 실패했습니다."),
    FEEDBACK_QUERY_SUCCESS(200, "I004", "FEEDBACK 조회에 성공했습니다."),
    FEEDBACK_QUERY_FAIL(400, "I004", "FEEDBACK 조회에 실패했습니다."),
    INQUIRY_QUERY_SUCCESS(200, "I005", "INQUIRY 조회에 성공했습니다."),
    INQUIRY_QUERY_FAIL(400, "I005", "INQUIRY 조회에 실패했습니다."),
    WITHDRAWAL_REASON_QUERY_SUCCESS(200, "I006", "WITHDRAWAL_REASON 조회에 성공했습니다."),
    WITHDRAWAL_REASON_QUERY_FAIL(400, "I006", "WITHDRAWAL_REASON 조회에 실패했습니다."),
    FEEDBACK_STATUS_FAIL(400, "I007", "피드백 상태 조회에 실패했습니다."),
    FEEDBACK_STATUS_SUCCESS(200, "I007", "피드백 상태 조회에 성공했습니다."),

    // Notification
    FCM_TOKEN_SAVE_SUCCESS(200, "N001", "FCM 토큰 저장에 성공했습니다."),
    FCM_TOKEN_SAVE_FAIL(400, "N001", "FCM 토큰 저장에 실패했습니다."),
    ;


    // field
    private final int status;
    private final String code;
    private final String message;
}
