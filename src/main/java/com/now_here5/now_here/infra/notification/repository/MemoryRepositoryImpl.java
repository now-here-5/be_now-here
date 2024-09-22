package com.now_here5.now_here.infra.notification.repository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentSkipListMap;

@Slf4j
@Repository
public class MemoryRepositoryImpl implements MemoryRepository {
    private static final ConcurrentSkipListMap<String, String> checkCodeMap = new ConcurrentSkipListMap<>();

    private final int codeMaxMemorySize; // 최대 크기
    private final int codeStandardMemorySize;  // 최소 크기

    public MemoryRepositoryImpl(
            @Value("${checkCodeMap.codeMaxMemorySize}") int codeMaxMemorySize,
            @Value("${checkCodeMap.codeStandardMemorySize}") int codeStandardMemorySize) {
        this.codeMaxMemorySize = codeMaxMemorySize;
        this.codeStandardMemorySize = codeStandardMemorySize;
    }

    public String findCheckCodeBy(String target) {
        log.info("found code = {}", checkCodeMap.get(target));
        return checkCodeMap.get(target);
    }

    public Boolean checkStatusBy(String target) {
        return Boolean.valueOf(findCheckCodeBy(target));
    }

    @Transactional
    public void saveCheckCode(String target, String checkCode) { // CREATE
        checkCodeMap.remove(target);
        checkIfOverflow(); // 오버플로우 방지

        log.info("saved phone_number & code = {}&{}", target, checkCode);
        checkCodeMap.put(target, checkCode);
    }

    public void delete(String target) {
        checkCodeMap.remove(target);
    }

    private void checkIfOverflow() {
        int currentSize = checkCodeMap.size();

        log.info("current phone memory size = {}", currentSize);
        if (currentSize > codeMaxMemorySize) { // 최대 크기를 넘으면 초과된 모든 요소 제거
            for (int i = 0; i < currentSize - codeMaxMemorySize; i++) {
                checkCodeMap.pollFirstEntry(); // 가장 오래된 항목 제거
            }
        } else if (currentSize > codeStandardMemorySize) { // 기준 크기보다 크면 기준 크기 초과분의 1.2배 제거
            int numberOfElementsToRemove = (int) ((currentSize - codeStandardMemorySize) * 1.2);
            for (int i = 0; i < numberOfElementsToRemove; i++) {
                checkCodeMap.pollFirstEntry();
            }
        }
    }

    public int getCheckCodeMapSize() {
        return codeMaxMemorySize - checkCodeMap.size();
    }
}