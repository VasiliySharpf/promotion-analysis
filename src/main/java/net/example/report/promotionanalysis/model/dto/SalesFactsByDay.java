package net.example.report.promotionanalysis.model.dto;

import java.time.LocalDate;
import java.util.List;

public record SalesFactsByDay(LocalDate day,
                              List<ActualDto> salesFacts) {
}
