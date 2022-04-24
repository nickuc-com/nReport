package com.nickuc.report.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Report {

    private final String author;
    private final String reason;
    private final long time;

}
