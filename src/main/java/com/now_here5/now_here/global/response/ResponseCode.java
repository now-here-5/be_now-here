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
    AUTHENTICATION_FAIL(401, "A001", "사용자 인증에 실패했습니다."),
    AUTHORIZATION_FAIL(403, "A002", "사용자 권한이 없습니다."),

    // Member
    LOGIN_SUCCESS(200, "M001", "로그인에 성공했습니다."),
    LOGIN_FAIL(200, "M002", "로그인에 실패했습니다."),
    LOGOUT_SUCCESS(200, "M003", "로그아웃에 성공했습니다."),
    LOGOUT_FAIL(200, "M004", "로그아웃에 실패했습니다."),
    INACTIVATE_SUCCESS(200, "M005", "회원탈퇴에 성공했습니다."),
    INACTIVATE_FAIL(400, "M006", "회원탈퇴에 실패했습니다."),

    // Sign-up
    SIGNUP_SUCCESS(200, "S001", "회원가입에 성공했습니다."),
    SIGNUP_FAIL(400, "S002", "회원가입에 실패했습니다."),
    SIGNUP_DUPLICATED(400, "S003", "이미 가입된 회원입니다."),

    // nickname
    NICKNAME_DUPLICATED(200, "A004", "중복된 닉네임입니다."),
    NICKNAME_QUALIFIED(200, "A004", "사용 가능한 닉네임입니다."),

    // Phone Verify
    PHONE_DUPLICATED(200, "A004", "현재 이벤트로 이미 가입된 번호입니다."),
    PHONE_VERIFY_REQUEST(200, "A004", "휴대폰 인증을 요청했습니다."),
    PHONE_VERIFY_SUCCESS(200, "A004", "휴대폰 인증에 성공했습니다."),
    PHONE_VERIFY_FAIL(400, "A005", "휴대폰 인증에 실패했습니다."),

    // Event
    EVENT_QUERY_SUCCESS(200, "E004", "이벤트 조회에 성공했습니다."),
    EVENT_QUERY_FAIL(200, "E004", "이벤트 조회에 실패했습니다.");
    // field
    private final int status;
    private final String code;
    private final String message;
}
