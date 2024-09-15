package com.now_here5.now_here.infra.notification.repository;


public interface MemoryRepository {

    String findCheckCodeBy(String target);

    Boolean checkStatusBy(String target);

    void saveCheckCode(String target, String checkCode);

    void delete(String target);
}



