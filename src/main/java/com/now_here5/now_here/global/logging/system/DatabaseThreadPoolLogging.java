package com.now_here5.now_here.global.logging.system;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseThreadPoolLogging {

    private final DataSource dataSource;

    public void logHikariCPStatus() {
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        log.info("HikariCP Pool - Active: {}, Idle: {}, Total: {}, Max: {}",
                hikariDataSource.getHikariPoolMXBean().getActiveConnections(),
                hikariDataSource.getHikariPoolMXBean().getIdleConnections(),
                hikariDataSource.getHikariPoolMXBean().getTotalConnections(),
                hikariDataSource.getHikariConfigMXBean().getMaximumPoolSize());
    }
}
