package com.now_here5.now_here.domain.member.entity;


import com.now_here5.now_here.admin.role.repository.RoleAdminRepository;
import com.now_here5.now_here.domain.member.entity.role.MemberRole;
import com.now_here5.now_here.domain.member.entity.role.Role;
import com.now_here5.now_here.domain.member.entity.role.RoleName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleAdminService {

    private final RoleAdminRepository roleAdminRepository;

    public List<Role> findRoleByName(List<RoleName> roleNames) {
        return roleAdminRepository.findRolesByNames(roleNames);
    }

}
