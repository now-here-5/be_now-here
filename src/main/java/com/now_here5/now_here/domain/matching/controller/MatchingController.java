package com.now_here5.now_here.domain.matching.controller;

import com.now_here5.now_here.domain.matching.dto.BannerListResponse;
import com.now_here5.now_here.domain.matching.service.MatchingService;
import com.now_here5.now_here.global.response.ResponseForm;
import com.now_here5.now_here.global.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/matching")
@Tag(name = "Matching API", description = "매칭 API")
public class MatchingController {
    private final MatchingService matchingService;

    @Operation(summary = "배너용 멤버 조회", description = "배너에 표시할 멤버 목록을 조회합니다.",  security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "M001 - 배너용 멤버 목록 조회에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "M001 - 배너용 멤버 목록 조회에 실패했습니다.")
    })
    @GetMapping("/banner")
    public ResponseEntity<ResponseForm> getMemberForBanner() {
        List<BannerListResponse> bannerList = matchingService.getBannerList();

        return bannerList != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.BannerList_QUERY_SUCCESS, bannerList)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.BannerList_QUERY_FAIL));
    }
}