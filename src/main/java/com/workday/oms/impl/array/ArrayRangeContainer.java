package com.workday.oms.impl.array;

import com.workday.oms.entity.*;
import com.workday.oms.exception.DataThresholdException;
import com.workday.oms.exception.InvalidRangeException;
import com.workday.oms.interfaces.Ids;
import com.workday.oms.interfaces.RangeContainer;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ArrayRangeContainer implements RangeContainer {

    public static final Ids EMPTY_IDS = new EmptyIds();
    private static final int DATA_THRESHOLD = 32000;
    public final PayrollResult[] payrollResults;
    private static final Logger log = Logger.getLogger(ArrayRangeContainer.class);
    public final long[] data;

    public ArrayRangeContainer(long[] data) throws DataThresholdException {
        if (data != null) {
            if (data.length > DATA_THRESHOLD) {
                log.error("The maximum threshold (32000) of data size exceeded");
                throw new DataThresholdException("Maximum Threshold data size exceeded:" + data.length);
            }
            this.data = new long[data.length];
            payrollResults = new PayrollResult[data.length];
            for (short i = 0; i < data.length; i++) {
                payrollResults[i] = new PayrollResult(i, data[i]);
            }
            Arrays.sort(payrollResults, new PayrollResultComparator());
        } else {
            this.data = new long[]{};
            payrollResults = new PayrollResult[]{};
        }

    }

    @Override
    public Ids findIdsInRange(long fromValue, long toValue, boolean fromInclusive, boolean toInclusive) throws InvalidRangeException {
        if (fromValue < 0 || toValue < 0 || toValue < fromValue || fromValue > toValue) {
            log.error("Invalid Salary Range :: From Value:: " + fromValue + " toValue::" + toValue);
            throw new InvalidRangeException("Invalid Salary Range :: From Value:: " + fromValue + " toValue::" + toValue);
        } else if (toValue < fromValue || payrollResults.length == 0) {
            return EMPTY_IDS;
        } else {
            if (!OutOfRange(fromValue, toValue, fromInclusive, toInclusive)) {
                Range range = findRange(0, payrollResults.length - 1, fromValue, toValue, fromInclusive, toInclusive);
                int size = range.endId - range.startId;
                List<PayrollResult> payrollResultsList = IntStream.rangeClosed(0, size).mapToObj(i -> new PayrollResult(
                        payrollResults[i + range.startId].workerId,
                        payrollResults[i + range.startId].netSalary)).sorted().collect(Collectors.toList());
                return new PayrollResultIds(payrollResultsList);
            } else return EMPTY_IDS;
        }

    }

    private boolean OutOfRange(long fromValue, long toValue, boolean fromInclusive, boolean toInclusive) {
        return fromValue > payrollResults[payrollResults.length - 1].netSalary ||
                (fromValue == payrollResults[payrollResults.length - 1].netSalary && !fromInclusive) ||
                toValue < payrollResults[0].netSalary ||
                (toValue == payrollResults[0].netSalary && !toInclusive);
    }

    private Range findRange(int startId, int endId, long fromValue, long toValue, boolean fromInclusive, boolean toInclusive) {
        if ((payrollResults[startId].netSalary > fromValue || (payrollResults[startId].netSalary == fromValue && fromInclusive)) &&
                (payrollResults[endId].netSalary < toValue || (payrollResults[endId].netSalary == toValue && toInclusive))) {
            return new Range(startId, endId);
        }

        if (startId + 1 == endId) {
            if ((payrollResults[startId].netSalary > fromValue || payrollResults[startId].netSalary == fromValue && fromInclusive) &&
                    (payrollResults[startId].netSalary < toValue || payrollResults[startId].netSalary == toValue && toInclusive)) {
                return new Range(startId, startId);
            } else {
                return new Range(endId, endId);
            }
        }

        int mid = (startId + endId) >>> 1;

        if (payrollResults[mid].netSalary < fromValue || (payrollResults[mid].netSalary == fromValue && !fromInclusive)) {
            return findRange(mid, endId, fromValue, toValue, fromInclusive, toInclusive);
        } else if (payrollResults[mid].netSalary > toValue || (payrollResults[mid].netSalary == toValue && !toInclusive)) {
            return findRange(startId, mid, fromValue, toValue, fromInclusive, toInclusive);
        } else {
            Range r1 = findRange(startId, mid, fromValue, toValue, fromInclusive, toInclusive);
            Range r2 = findRange(mid, endId, fromValue, toValue, fromInclusive, toInclusive);
            r1.endId = r2.endId;
            return r1;
        }
    }
}
