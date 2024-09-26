package com.now_here5.now_here.domain.member.entity.role;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name", unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberRole> memberRoleList = new ArrayList<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolePrivilege> rolePrivilegeList = new ArrayList<>();

    protected Role() {
        this.roleName = RoleName.USER;
    }

    public Role(RoleName roleName) {
        this.roleName = roleName;
    }

    public void addPrivilege(Privilege privilege) {
        RolePrivilege rolePrivilege = new RolePrivilege(this, privilege);
        rolePrivilegeList.add(rolePrivilege);
    }

    public void addPrivileges(List<Privilege> privileges) {
        for (Privilege privilege : privileges) {
            addPrivilege(privilege);
        }
    }

    public void removePrivilege(Privilege privilege) {
        rolePrivilegeList.removeIf(rolePrivilege -> rolePrivilege.getPrivilege().equals(privilege));
    }

    public void removePrivileges(List<Privilege> privileges) {
        for (Privilege privilege : privileges) {
            removePrivilege(privilege);
        }
    }
}
