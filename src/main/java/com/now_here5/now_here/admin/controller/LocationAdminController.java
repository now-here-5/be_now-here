package com.now_here5.now_here.admin.controller;

import com.now_here5.now_here.domain.event.dto.LocationResponse;
import com.now_here5.now_here.domain.event.dto.NewLocationRequest;
import com.now_here5.now_here.domain.event.service.EventService;
import com.now_here5.now_here.global.response.ResponseCode;
import com.now_here5.now_here.global.response.ResponseForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/location")
@Tag(name = "Admin location management API", description = "관리자 위치 관리 API")
public class LocationAdminController {
    private final EventService eventService;

    @Operation(summary = "위치 추가", description = "새로운 위치를 추가합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "새로운 위치 요청",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = NewLocationRequest.class),
                    examples = @ExampleObject(
                            name = "NewLocationRequest",
                            value = "{ \"locationName\": \"Location A\" }"
                    )
            ))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "E011 - 위치 추가 성공"),
            @ApiResponse(responseCode = "400", description = "E012 - 위치 추가 실패")
    })
    @PostMapping("/add")
    public ResponseEntity<ResponseForm> addLocation(@RequestBody NewLocationRequest newLocationRequest) {
        boolean result = eventService.createLocation(newLocationRequest);

        return result ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_ADD_LOCATION_SUCCESS)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_ADD_LOCATION_FAIL));
    }

    @Operation(summary = "위치 조회", description = "위치 ID를 사용하여 위치의 세부 정보를 조회합니다.")
    @Parameters({
            @Parameter(name = "location_id", description = "위치 ID", required = true, schema = @Schema(example = "1"))
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "E012 - 위치 조회 성공", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "E012 - 위치 조회 실패")
    })
    @GetMapping("/detail/{location_id}")
    public ResponseEntity<ResponseForm> getLocation(
            @PathVariable(name = "location_id") Long locationId) {
        String location = eventService.getLocation(locationId);
        return location != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.LOCATION_QUERY_SUCCESS, location)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.LOCATION_QUERY_FAIL));
    }

    @Operation(summary = "위치 목록 조회", description = "모든 위치의 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "E013 - 위치 목록 조회 성공", content = @Content(schema = @Schema(implementation = LocationResponse.class))),
            @ApiResponse(responseCode = "400", description = "E013 - 위치 목록 조회 실패")
    })
    @GetMapping("/list")
    public ResponseEntity<ResponseForm> getLocationList() {
        List<LocationResponse> locationList = eventService.getLocationList();

        return locationList != null ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.LOCATION_LIST_QUERY_SUCCESS, locationList)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.LOCATION_LIST_QUERY_FAIL));
    }

    @Operation(summary = "위치 삭제", description = "위치 ID를 사용하여 위치를 삭제합니다.")
    @Parameters({
            @Parameter(name = "location_id", description = "위치 ID", required = true, schema = @Schema(example = "1"))
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "E011 - 위치 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "E011 - 위치 삭제 실패")
    })
    @DeleteMapping("/delete/{location_id}")
    public ResponseEntity<ResponseForm> deleteLocation(
            @PathVariable(name = "location_id") Long locationId) {
        boolean result = eventService.deleteLocation(locationId);

        return result ?
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_DELETE_LOCATION_SUCCESS)) :
                ResponseEntity.ok(ResponseForm.of(ResponseCode.EVENT_DELETE_LOCATION_FAIL));
    }
}
