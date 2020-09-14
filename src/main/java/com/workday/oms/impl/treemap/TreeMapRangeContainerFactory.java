package com.workday.oms.impl.treemap;

import com.workday.oms.exception.DataThresholdException;
import com.workday.oms.interfaces.RangeContainer;
import com.workday.oms.interfaces.RangeContainerFactory;

public class TreeMapRangeContainerFactory implements RangeContainerFactory {

    /**
     * Create TreeMapRangeContainer
     * @param data
     * @return
     */
    public RangeContainer createContainer(long[] data) throws DataThresholdException {
        return new TreeMapRangeContainer(data);
    }

}
