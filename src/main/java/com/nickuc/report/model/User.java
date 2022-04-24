/*
 * This file is part of a NickUC project
 *
 * Copyright (c) NickUC <nickuc.com>
 * https://github.com/nickuc
 */

package com.nickuc.report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public class User {

    private final UUID uniqueId;
    private final List<Report> reportList;

    public synchronized Stream<Map.Entry<String, Integer>> getOrderedReports(int limit) {
        Map<String, Integer> reasonAndCount = new HashMap<>();
        reportList.forEach(report ->
                reasonAndCount.merge(report.getReason(), 1, Integer::sum));

        return reasonAndCount.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(limit);
    }

    @Nullable
    public synchronized Report lastReport() {
        return reportList.size() > 0 ? reportList.get(reportList.size() - 1) : null;
    }

    public synchronized void addReport(String author, String reason) {
        reportList.add(new Report(author, reason, System.currentTimeMillis()));
    }

}
