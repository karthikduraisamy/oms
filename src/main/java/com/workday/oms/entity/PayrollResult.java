package com.workday.oms.entity;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;

/**
 * PayrollResult Entity
 */
public class PayrollResult implements Comparable<PayrollResult> {

    public short workerId;
    public long netSalary;

    /**
     * PayrollResult Constructor
     *
     * @param id
     * @param netSalary
     */
    public PayrollResult(short id, long netSalary) {
        super();
        this.workerId = id;
        this.netSalary = netSalary;
    }

    /**
     * PayrollResult No-Arg Constructor
     */
    public PayrollResult() {
    }

    /**
     * Compare PayrollResultIds
     *
     * @param payrollResult
     * @return
     */
    @Override
    public int compareTo(PayrollResult payrollResult) {
        if (this.workerId < payrollResult.workerId) {
            return -1;
        } else {
            if (this.workerId <= payrollResult.workerId) {
                return 0;
            }
            return 1;
        }
    }

    public short getWorkerId() {
        return workerId;
    }

    public void setWorkerId(short workerId) {
        this.workerId = workerId;
    }

    public long getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(long netSalary) {
        this.netSalary = netSalary;
    }

    /**
     * CQE Index Attribute for Salary
     */
    public static final Attribute<PayrollResult, Long> NET_SALARY = new SimpleAttribute<PayrollResult, Long>("netSalary") {
        public Long getValue(PayrollResult employee, QueryOptions queryOptions) {
            return employee.getNetSalary();
        }
    };

    /**
     * CQE Index Attribute for WorkerId
     */
    public static final Attribute<PayrollResult, Short> WORKER_ID = new SimpleAttribute<PayrollResult, Short>("workerId") {
        public Short getValue(PayrollResult employee, QueryOptions queryOptions) {
            return employee.getWorkerId();
        }
    };
}
