package com.now_here5.now_here.global.security.dto;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RoleNamesDto {
    private final List<String> roleNames;
}
