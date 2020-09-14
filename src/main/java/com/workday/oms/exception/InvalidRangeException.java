package com.workday.oms.exception;

/**
 * DataThresholdException for Invalid Range Values
 */
public class InvalidRangeException extends IllegalArgumentException {

    private static final long serialVersionUID = 8867399396957792034L;

    public InvalidRangeException(String message) {
        super(message);
    }

}
