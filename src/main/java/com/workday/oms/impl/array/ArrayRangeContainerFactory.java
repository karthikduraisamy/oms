package com.workday.oms.impl.array;

import com.workday.oms.exception.DataThresholdException;
import com.workday.oms.interfaces.RangeContainer;
import com.workday.oms.interfaces.RangeContainerFactory;

/**
 *
 */
public class ArrayRangeContainerFactory implements RangeContainerFactory {

    public RangeContainer createContainer(long[] data) throws DataThresholdException {
        return new ArrayRangeContainer(data);
    }
}
