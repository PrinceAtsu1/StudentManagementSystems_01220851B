package com.template.service;

import java.nio.file.Path;

public class ImportResult {

    private final int totalRows;
    private final int importedCount;
    private final int skippedCount;
    private final int duplicateCount;
    private final Path errorReportPath;

    public ImportResult(int totalRows, int importedCount, int skippedCount, int duplicateCount, Path errorReportPath) {
        this.totalRows = totalRows;
        this.importedCount = importedCount;
        this.skippedCount = skippedCount;
        this.duplicateCount = duplicateCount;
        this.errorReportPath = errorReportPath;
    }

    public int getTotalRows() { return totalRows; }
    public int getImportedCount() { return importedCount; }
    public int getSkippedCount() { return skippedCount; }
    public int getDuplicateCount() { return duplicateCount; }
    public Path getErrorReportPath() { return errorReportPath; }
}