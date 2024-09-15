package com.now_here5.now_here.global.config.database;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {
                "com.now_here5.now_here.domain.event.repository",
                "com.now_here5.now_here.domain.interaction.repository",
                "com.now_here5.now_here.domain.matching.repository",
                "com.now_here5.now_here.domain.member.repository",
                "com.now_here5.now_here.infra.notification.repository"
        },
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class JpaConfig {

    private final DataSource dataSource;
    private final JpaProperties jpaProperties;

    public JpaConfig(@Qualifier("dataSource") DataSource dataSource, JpaProperties jpaProperties) {
        this.dataSource = dataSource;
        this.jpaProperties = jpaProperties;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        // HibernateJpaVendorAdapter 설정
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");

        // EntityManagerFactoryBean 생성 및 설정
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setJpaVendorAdapter(vendorAdapter);

        // 엔티티 패키지 스캔 설정
        factoryBean.setPackagesToScan(
                "com.now_here5.now_here.domain.event.entity",
                "com.now_here5.now_here.domain.interaction.entity",
                "com.now_here5.now_here.domain.matching.entity",
                "com.now_here5.now_here.domain.member.entity",
                "com.now_here5.now_here.infra.notification.entity"
        );

        // JPA 프로퍼티 설정
        factoryBean.setJpaPropertyMap(jpaProperties.getProperties());

        factoryBean.setPersistenceUnitName("default");

        return factoryBean;
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
