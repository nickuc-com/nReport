package com.nickuc.report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class Settings {

    private final List<String> loadedReports;
    private final int delayReports;
    private final boolean allowOtherReports;

}
