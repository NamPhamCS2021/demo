package com.example.demoSQL.dto;

import com.example.demoSQL.enums.EResponseCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private T data;
    private String resultCode;
    @JsonProperty("resultDesc")
    private String resultMessage;

    public ApiResponse(String reesultCode, String resultMessage) {
        this.resultCode = reesultCode;
        this.resultMessage = resultMessage;
    }
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .resultCode(EResponseCode.SUCCESS.getCode())
                .resultMessage(EResponseCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }
    public static <T> ApiResponse<T> fail(EResponseCode code) {
        return ApiResponse.<T>builder()
                .resultCode(code.getCode())
                .resultMessage(code.getMessage())
                .build();
    }
}
