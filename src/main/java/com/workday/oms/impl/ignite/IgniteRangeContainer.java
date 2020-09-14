package com.workday.oms.impl.ignite;

import com.workday.oms.entity.PayrollResult;
import com.workday.oms.entity.PayrollResultIds;
import com.workday.oms.exception.DataThresholdException;
import com.workday.oms.exception.InvalidRangeException;
import com.workday.oms.interfaces.Ids;
import com.workday.oms.interfaces.RangeContainer;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.log4j.Logger;

import javax.cache.Cache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class IgniteRangeContainer implements RangeContainer {
    long[] data;
    private static final String CACHE_NAME = "payrollDataCache";
    private static final int DATA_THRESHOLD = 32000;
    private static final String queryField = "netSalary";
    private static final Logger log = Logger.getLogger(IgniteRangeContainer.class);

    public IgniteRangeContainer(long[] data) throws DataThresholdException {
        this.data = data;
        if (Objects.nonNull(data)) {
            if (data.length > DATA_THRESHOLD) {
                log.error("The maximum threshold (32000) of data size exceeded");
                throw new DataThresholdException("Maximum Threshold data size exceeded:" + data.length);
            }
        }
        Ignite ignite = Ignition.start();
        IgniteCache<Short, PayrollResult> cache = ignite.getOrCreateCache(CACHE_NAME);
        IntStream.range(0, data.length).forEach(i -> cache.put((short) i, new PayrollResult((short) i, data[i])));
    }

    /**
     * Range Query based on a predicate using binary objects.
     *
     * @return
     */
    private static PayrollResultIds scanQuery(long fromValue, long toValue, boolean fromInclusive, boolean toInclusive) {
        IgniteCache<Short, PayrollResult> cache = Ignition.ignite().cache(CACHE_NAME).withKeepBinary();
        ScanQuery<Short, BinaryObject> scan = new ScanQuery<>(
                new IgniteBiPredicate<Short, BinaryObject>() {
                    @Override
                    public boolean apply(Short key, BinaryObject payrollResult) {
                        if (fromInclusive && toInclusive)
                            return payrollResult.<Long>field(queryField) >= fromValue && payrollResult.<Long>field(queryField) <= toValue;
                        else if (fromInclusive && !toInclusive)
                            return payrollResult.<Long>field(queryField) >= fromValue && payrollResult.<Long>field(queryField) < toValue;
                        else if (!fromInclusive && toInclusive)
                            return payrollResult.<Long>field(queryField) > fromValue && payrollResult.<Long>field(queryField) <= toValue;
                        return payrollResult.<Long>field(queryField) > fromValue && payrollResult.<Long>field(queryField) < toValue;
                    }
                }
        );
        List<Cache.Entry<Short, BinaryObject>> cacheResults = cache.query(scan).getAll();
        List<PayrollResult> payrollResultList = new ArrayList<>();
        for (Cache.Entry<Short, BinaryObject> cacheResult : cacheResults) {
            payrollResultList.add(new PayrollResult(cacheResult.getKey(), cacheResult.getValue().field("netSalary")));
        }
        Collections.sort(payrollResultList);
        return new PayrollResultIds(payrollResultList);
    }

    @Override
    public Ids findIdsInRange(long fromValue, long toValue, boolean fromInclusive, boolean toInclusive) throws InvalidRangeException {
        if (fromValue < 0 || toValue < 0 || toValue < fromValue || fromValue > toValue) {
            log.error("Invalid Salary Range :: From Value:: " + fromValue + " toValue::" + toValue);
            throw new InvalidRangeException("Invalid Salary Range :: From Value:: " + fromValue + " toValue::" + toValue);
        }
        return scanQuery(fromValue, toValue, fromInclusive, toInclusive);
    }
}
