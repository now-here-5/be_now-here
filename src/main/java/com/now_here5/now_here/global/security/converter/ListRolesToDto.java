package com.now_here5.now_here.global.security.converter;


import com.now_here5.now_here.domain.member.entity.role.MemberRole;
import com.now_here5.now_here.global.security.dto.RoleNamesDto;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class ListRolesToDto {
    public RoleNamesDto converter(List<MemberRole> memberRoleList) {

        return RoleNamesDto.builder()
                .roleNames(memberRoleList.stream()
                        .map(memberRole -> memberRole.getRole().getRoleName().name())
                        .toList()
                )
                .build();
    }
}
