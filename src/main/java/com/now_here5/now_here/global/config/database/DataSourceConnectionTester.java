//package com.now_here5.now_here.global.config.database;
//
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//
//@Slf4j
//@Component
//public class DataSourceConnectionTester {
//    private final DataSource masterDataSource;
//
//    private final DataSource slaveDataSource;
//
//    public DataSourceConnectionTester(@Qualifier("masterDataSource") DataSource masterDataSource, @Qualifier("slaveDataSource") DataSource slaveDataSource) {
//        this.masterDataSource = masterDataSource;
//        this.slaveDataSource = slaveDataSource;
//    }
//
//    @PostConstruct
//    public void testConnections() {
//        testConnection(masterDataSource, "Master");
//        testConnection(slaveDataSource, "Slave");
//    }
//
//    private void testConnection(DataSource dataSource, String name) {
//        try (Connection conn = dataSource.getConnection()) {
//            log.info("{} DataSource connection successful", name);
//        } catch (SQLException e) {
//            log.error("{} DataSource connection failed: {}", name, e.getMessage());
//        }
//    }
//}
