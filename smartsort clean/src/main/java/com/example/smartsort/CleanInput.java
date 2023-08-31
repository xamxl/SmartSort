package com.example.smartsort;

public class CleanInput {
    private String toClean;
    private String clean;
    private String toCleanColumn;

    public String getToClean() {
        return toClean;
    }

    public void setToClean(String toClean) {
        this.toClean = toClean;
    }

    public String getClean() {
        return clean;
    }

    public void setClean(String clean) {
        this.clean = clean;
    }

    public int getToCleanColumn() {
        return (toCleanColumn == null || toCleanColumn.equals("")) ? 0 : Integer.parseInt(toCleanColumn);
    }

    public void setToCleanColumn(String toCleanColumn) {
        this.toCleanColumn = toCleanColumn;
    }

}
