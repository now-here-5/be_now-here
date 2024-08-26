package com.now_here5.now_here.infra.phone.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
@Repository
public class MemoryRepository {
    private static final ConcurrentHashMap<String, String> checkCodeMap = new ConcurrentHashMap<>();
    private static final ConcurrentLinkedDeque<String> checkCodeQueue = new ConcurrentLinkedDeque<>();


    public String findCheckCodeBy(String target) {
        log.info("found code = {}", checkCodeMap.get(target));
        return checkCodeMap.get(target);
    }

    public Boolean checkStatusBy(String target) {
        return Boolean.valueOf(findCheckCodeBy(target));
    }

    public void saveCheckCode(String target, String checkCode) { // CREATE

        checkCodeMap.remove(target);
        checkIFOverflow(); // 오버플로우 방지

        log.info("saved email&code = {}&{}",target,checkCode);

        checkCodeMap.put(target,checkCode);
        checkCodeQueue.addLast(target);
    }

    public void delete(String target) {
        checkCodeMap.remove(target);
    }

    private void checkIFOverflow(){

        if( checkCodeMap.size() > 50){ // 50개 넘으면 비움
            int numberOfElementsToRemove = checkCodeQueue.size() / 4; // 반의 반

            for (int i = 0; i < numberOfElementsToRemove && !checkCodeQueue.isEmpty(); i++) {
                String phone = checkCodeQueue.pollFirst(); // 큐에서 빼고
                checkCodeMap.remove(phone); // 맵에서도 삭제
            }
            
        }
    }


}
