package com.workday.oms.entity;


import com.workday.oms.interfaces.Ids;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

public class PayrollResultIds implements Ids {

    private final Iterator<PayrollResult> ids;

    /**
     * PayrollResultIds Constructor
     *
     * @param ids
     */
    public PayrollResultIds(Iterable<PayrollResult> ids) {
        if (Objects.isNull(ids)) {
            this.ids = Collections.emptyIterator();
        } else {
            this.ids = ids.iterator();
        }
    }

    /**
     * return the next id in the sequence and -1 if there is no data
     * The ids are already sorted
     */
    @Override
    public short nextId() {
        return ids.hasNext() ? ids.next().getWorkerId() : END_OF_IDS;
    }

}
