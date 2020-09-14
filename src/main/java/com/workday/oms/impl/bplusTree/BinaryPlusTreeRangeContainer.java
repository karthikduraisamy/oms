package com.workday.oms.impl.bplusTree;


import com.workday.oms.entity.BinaryPlusTree;
import com.workday.oms.entity.PayrollResult;
import com.workday.oms.entity.PayrollResultIds;
import com.workday.oms.exception.DataThresholdException;
import com.workday.oms.exception.InvalidRangeException;
import com.workday.oms.interfaces.Ids;
import com.workday.oms.interfaces.RangeContainer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class BinaryPlusTreeRangeContainer implements RangeContainer {

    public final BinaryPlusTree<Long, List<PayrollResult>> binaryPlusTree;
    private static final int DATA_THRESHOLD = 32000;
    private static final Logger log = Logger.getLogger(BinaryPlusTreeRangeContainer.class);

    public BinaryPlusTreeRangeContainer(long[] data) throws DataThresholdException {
        if (data == null) {
            this.binaryPlusTree = new BinaryPlusTree<>();
        } else {
            if (data.length > DATA_THRESHOLD) {
                log.error("The maximum threshold (32000) of data size exceeded");
                throw new DataThresholdException("Maximum Threshold data size exceeded:" + data.length);
            }
            this.binaryPlusTree = new BinaryPlusTree<>();
            for (short i = 0; i < data.length; i++) {
                List<PayrollResult> val = binaryPlusTree.search(data[i]);
                if (val == null) {
                    List<PayrollResult> lp = new ArrayList<>();
                    lp.add(new PayrollResult(i, data[i]));
                    binaryPlusTree.insert(data[i], lp);
                } else {
                    val.add(new PayrollResult(i, data[i]));
                    binaryPlusTree.insert(data[i], val);
                }
            }
        }
    }

    public Ids findIdsInRange(long fromValue, long toValue, boolean fromInclusive, boolean toInclusive) throws InvalidRangeException {
        if (fromValue < 0 || toValue < 0 || toValue < fromValue || fromValue > toValue) {
            log.error("Invalid Salary Range :: From Value:: " + fromValue + " toValue::" + toValue);
            throw new InvalidRangeException("Invalid Salary Range :: From Value:: " + fromValue + " toValue::" + toValue);
        }
        List<List<PayrollResult>> results = binaryPlusTree.searchRange(fromValue, toValue, fromInclusive, toInclusive);
        List<PayrollResult> res = results.stream().flatMap(Collection::stream).collect(Collectors.toList());
        Collections.sort(res);
        return new PayrollResultIds(res);
    }


}
