package com.now_here5.now_here.global.response;

import com.now_here5.now_here.global.response.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;



@Getter
public class ResponseForm {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY,name = "Http 상태 코드")
   final private int status;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY,name = "Business 상태 코드")
    final private String code;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY,name = "응답 메세지")
    final private String message;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY,name = "응답 데이터")
    final  private Object data;

    private ResponseForm(ResponseCode resultCode, Object data) {
        this.status = resultCode.getStatus();
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public static ResponseForm of(ResponseCode resultCode, Object data) {
        return new ResponseForm(resultCode, data);
    }

    public static ResponseForm of(ResponseCode resultCode) {
        return new ResponseForm(resultCode, "");
    }
}