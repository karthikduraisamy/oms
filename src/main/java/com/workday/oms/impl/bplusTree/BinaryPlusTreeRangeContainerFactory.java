package com.workday.oms.impl.bplusTree;

import com.workday.oms.exception.DataThresholdException;
import com.workday.oms.interfaces.RangeContainer;
import com.workday.oms.interfaces.RangeContainerFactory;

public class BinaryPlusTreeRangeContainerFactory implements RangeContainerFactory {

    /**
     * Create BinaryPlusTreeRangeContainer
     *
     * @param data
     * @return
     */
    public RangeContainer createContainer(long[] data) throws DataThresholdException {
        return new BinaryPlusTreeRangeContainer(data);
    }

}
