package com.now_here5.now_here.global.response;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class ExceptionResponse implements ResponseInterface {
    private final String exceptionMessage;
    private final String exceptionCode;
}
