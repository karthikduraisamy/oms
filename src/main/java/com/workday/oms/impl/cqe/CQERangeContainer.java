package com.workday.oms.impl.cqe;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.resultset.ResultSet;
import com.workday.oms.entity.PayrollResult;
import com.workday.oms.entity.PayrollResultIds;
import com.workday.oms.exception.DataThresholdException;
import com.workday.oms.exception.InvalidRangeException;
import com.workday.oms.interfaces.Ids;
import com.workday.oms.interfaces.PayrollResultSetter;
import com.workday.oms.interfaces.RangeContainer;
import org.apache.log4j.Logger;

import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.googlecode.cqengine.query.QueryFactory.*;

public class CQERangeContainer implements RangeContainer {

    private static final Logger log = Logger.getLogger(CQERangeContainer.class);
    private static final int DATA_THRESHOLD = 32000;
    private final IndexedCollection<PayrollResult> indexedPayrollData = new ConcurrentIndexedCollection<>();

    public CQERangeContainer(long[] data) throws DataThresholdException {
        if (Objects.nonNull(data)) {
            if (data.length > DATA_THRESHOLD) {
                log.error("The maximum threshold (32000) of data size exceeded");
                throw new DataThresholdException("Maximum Threshold data size exceeded:" + data.length);
            }
            populateData(data);
        }
    }

    /**
     * Build PayrollResultCQE
     *
     * @param resultSetters
     * @return
     */
    public static PayrollResult build(PayrollResultSetter... resultSetters) {
        final PayrollResult payrollResultCQE = new PayrollResult();
        Stream.of(resultSetters).forEach(results -> results.set(payrollResultCQE));
        return payrollResultCQE;
    }

    /**
     * findIdsInRange between fromValue and toValue
     *
     * @param fromValue
     * @param toValue
     * @param fromInclusive
     * @param toInclusive
     * @return Ids
     */
    @Override
    public Ids findIdsInRange(long fromValue, long toValue, boolean fromInclusive, boolean toInclusive) throws InvalidRangeException {
        if (fromValue < 0 || toValue < 0 || toValue < fromValue || fromValue > toValue) {
            log.error("Invalid Salary Range :: From Value:: " + fromValue + " toValue::" + toValue);
            throw new InvalidRangeException("Invalid Salary Range :: From Value:: " + fromValue + " toValue::" + toValue);
        }
        Query<PayrollResult> query = between(PayrollResult.NET_SALARY, fromValue, fromInclusive, toValue, toInclusive);
        ResultSet<PayrollResult> result = indexedPayrollData.retrieve(query,
                queryOptions(orderBy(ascending(PayrollResult.WORKER_ID))));
        return new PayrollResultIds(result);
    }

    /**
     * Populate payroll data and create index on salary
     */
    private void populateData(long[] data) {

        //Add index on the attributes search is performed
        indexedPayrollData.addIndex(NavigableIndex.onAttribute(PayrollResult.NET_SALARY));
        IntStream.range(0, data.length).forEach(index -> {
            PayrollResult payrollResultCQE = build(payrollResult -> payrollResult.setNetSalary(data[index]), payrollResult -> payrollResult.setWorkerId((short) index));
            indexedPayrollData.add(payrollResultCQE);
        });
    }
}
