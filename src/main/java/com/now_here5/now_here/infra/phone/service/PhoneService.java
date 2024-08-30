package com.now_here5.now_here.infra.phone.service;


public interface PhoneService {
    boolean sendVerificationCode(String phone);

    boolean verifyCode(String phone, String code);

    boolean isVerifiedPhone( String phone) ;

    String getPhoneCode(String phone);

    void sendSms(String phone, Object message);
}
