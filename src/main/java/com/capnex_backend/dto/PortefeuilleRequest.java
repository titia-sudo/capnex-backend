package com.capnex_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class PortefeuilleRequest {
    @NotBlank
    private String nom;

    @NotNull
    private Double capitalInitial;

    private List<PositionRequest> positions;
}