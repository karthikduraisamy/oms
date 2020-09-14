package com.workday.oms.impl.treemap;


import com.workday.oms.entity.PayrollResult;
import com.workday.oms.entity.PayrollResultIds;
import com.workday.oms.exception.DataThresholdException;
import com.workday.oms.exception.InvalidRangeException;
import com.workday.oms.interfaces.Ids;
import com.workday.oms.interfaces.RangeContainer;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TreemapRangeContainer uses Treemap to store values and returns a sub-map of sorted results within query range
 */
public class TreeMapRangeContainer implements RangeContainer {
    private final TreeMap<Long, Short> dataMap;
    private static final int DATA_THRESHOLD = 32000;
    private static final Logger log = Logger.getLogger(TreeMapRangeContainer.class);

    public TreeMapRangeContainer(long[] data) throws DataThresholdException {
        if (Objects.nonNull(data)) {
            if (data.length > DATA_THRESHOLD) {
                log.error("The maximum threshold (32000) of data size exceeded");
                throw new DataThresholdException("Maximum Threshold data size exceeded:" + data.length);
            }
        }
        this.dataMap = new TreeMap<>();
        for (short i = 0; i < data.length; i++) {
            dataMap.put(data[i], i);
        }
    }

    public Ids findIdsInRange(long fromValue, long toValue, boolean fromInclusive, boolean toInclusive) {
        if (fromValue < 0 || toValue < 0 || toValue < fromValue || fromValue > toValue) {
            log.error("Invalid Salary Range :: From Value:: " + fromValue + " toValue::" + toValue);
            throw new InvalidRangeException("Invalid Salary Range :: From Value:: " + fromValue + " toValue::" + toValue);
        }
        NavigableMap<Long, Short> m = dataMap.subMap(fromValue, fromInclusive, toValue, toInclusive);
        List<PayrollResult> list = convertToPayrollResultList(m);
        Collections.sort(list);
        return new PayrollResultIds(list);
    }

    private List<PayrollResult> convertToPayrollResultList(NavigableMap<Long, Short> m) {
        List<PayrollResult> payrollResultList = m.entrySet().stream().map(entry -> new PayrollResult(entry.getValue(), entry.getKey())).collect(Collectors.toList());
        return payrollResultList;
    }
}
