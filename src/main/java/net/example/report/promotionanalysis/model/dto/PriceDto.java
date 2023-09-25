package net.example.report.promotionanalysis.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PriceDto(@NotBlank String chainName,
                       @NotNull Long materialCode,
                       @NotNull Double regularPricePerUnit) {
}
