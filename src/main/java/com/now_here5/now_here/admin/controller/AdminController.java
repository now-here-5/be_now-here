package com.now_here5.now_here.admin.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Page API", description = "관리자 페이지 API")
public class AdminController {

}