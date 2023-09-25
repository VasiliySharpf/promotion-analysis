package net.example.report.promotionanalysis.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.example.report.promotionanalysis.model.enums.ShipmentType;

import java.time.LocalDate;

public record ActualDto(@JsonIgnore LocalDate date,
                        Long productCode,
                        Long customerCode,
                        String chainName,
                        Integer quantity,
                        Double actualSalesValue,
                        ShipmentType shipmentType) {
}
