package com.capnex_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PositionRequest {
    @NotBlank
    private String ticker;

    @NotNull
    private Integer lots;

    @NotNull
    private Double prixEntree;

    private String dateEntree;

    private Long portefeuilleId;
}
