package com.now_here5.now_here.domain.member.entity.role;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "privilege")
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "privilege_id")
    private Long id;

    @Column(name = "privilege_name", unique = true, nullable = false)
    private String privilegeName;

    @OneToMany(mappedBy = "privilege", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolePrivilege> rolePrivileges;

    public Privilege(String privilegeName) {
        this.privilegeName = privilegeName;
    }
}
