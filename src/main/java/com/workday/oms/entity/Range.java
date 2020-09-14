package com.workday.oms.entity;

/**
 * Entity for Range Values
 */
public class Range {
    public int startId;
    public int endId;

    public Range(int startId, int endId) {
        super();
        this.startId = startId;
        this.endId = endId;
    }
}
