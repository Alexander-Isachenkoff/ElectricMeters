package electricMeters.service;

import electricMeters.core.SqlFileSelect;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CostService {

    public static CompletableFuture<Double> calcTotalCost(int year, int month, int rateTypeID, int voltageLevelID) {
        return SqlFileSelect.builder()
                .sqlFile("TotalCost.sql")
                .params(List.of(month, year, rateTypeID, voltageLevelID))
                .build()
                .runAsync()
                .thenApply(jsonObjects -> jsonObjects.get(0).getDouble("SUMMARY_COST"));
    }

    public static CompletableFuture<Double> calcPeakPowerCost(int year, int month, int rateTypeID) {
        return SqlFileSelect.builder()
                .sqlFile("PeakPowerCost.sql")
                .params(List.of(rateTypeID, year, month))
                .build()
                .runAsync()
                .thenApply(jsonObjects -> jsonObjects.get(0).getDouble("POWER_COST"));
    }

}
