package com.skill.platform.stats.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for trend data response.
 * <p>
 * Contains a list of daily data points for the requested metric
 * over a specified date range.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendResponse {

    /** The metric being queried (e.g. "skills", "downloads", "users"). */
    private String metric;

    /** The start date of the trend range (inclusive, ISO-8601). */
    private String startDate;

    /** The end date of the trend range (inclusive, ISO-8601). */
    private String endDate;

    /** Daily aggregated data points. */
    private List<DailyDataPoint> dataPoints;

    /**
     * A single daily data point in a trend series.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyDataPoint {

        /** The date for this data point (ISO-8601, e.g. "2026-05-17"). */
        private String date;

        /** The aggregated value for the metric on this date. */
        private long value;
    }
}
