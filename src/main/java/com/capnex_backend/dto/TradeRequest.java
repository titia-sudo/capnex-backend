package com.capnex_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TradeRequest {
    @NotBlank
    private String ticker;

    @NotNull
    private Double prixIn;

    @NotNull
    private Double capital;

    @NotBlank
    private String sizing;

    private String dateIn;
    private Double prixOut;
}
