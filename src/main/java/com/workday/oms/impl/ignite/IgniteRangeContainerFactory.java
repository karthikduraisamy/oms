package com.workday.oms.impl.ignite;

import com.workday.oms.exception.DataThresholdException;
import com.workday.oms.impl.bplusTree.BinaryPlusTreeRangeContainer;
import com.workday.oms.interfaces.RangeContainer;
import com.workday.oms.interfaces.RangeContainerFactory;

public class IgniteRangeContainerFactory implements RangeContainerFactory {

    /**
     * Create IgniteRangeContainerFactory
     *
     * @param data
     * @return
     */
    public RangeContainer createContainer(long[] data) throws DataThresholdException {
        return new IgniteRangeContainer(data);
    }

}
