package com.now_here5.now_here.admin.role.repository;

import com.now_here5.now_here.domain.member.entity.role.Role;
import com.now_here5.now_here.domain.member.entity.role.RoleName;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
@Slf4j
public class RoleAdminRepository {
    private final EntityManager em;

    public List<Role> findRolesByNames(List<RoleName> roleNames) {
        try {
            return em.createQuery("select r from Role r where r.roleName in :roleNames", Role.class)
                    .setParameter("roleNames", roleNames)
                    .getResultList();
        } catch (Exception e) {
            log.error("Failed to find roles by names: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}