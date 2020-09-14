package com.workday.oms.impl.cqe;

import com.workday.oms.exception.DataThresholdException;
import com.workday.oms.interfaces.RangeContainer;
import com.workday.oms.interfaces.RangeContainerFactory;
import org.apache.log4j.Logger;

/**
 * CQERangeContainerFactory
 */
public class CQERangeContainerFactory implements RangeContainerFactory {

    private static final Logger log = Logger.getLogger(CQERangeContainerFactory.class);

    @Override
    public RangeContainer createContainer(long[] data) {
        try {
            return new CQERangeContainer(data);
        } catch (DataThresholdException e) {
            log.error("The maximum threshold for data exceeded");
        }
        return null;
    }

}
