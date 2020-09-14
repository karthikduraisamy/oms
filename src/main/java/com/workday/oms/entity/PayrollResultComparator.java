package com.workday.oms.entity;

import com.workday.oms.entity.PayrollResult;

import java.util.Comparator;

public class PayrollResultComparator implements Comparator<PayrollResult> {

    @Override
    public int compare(PayrollResult pr1, PayrollResult pr2) {
        if (pr1.netSalary > pr2.netSalary) {
            return 1;
        } else if (pr1.netSalary < pr2.netSalary) {
            return -1;
        }
        return 0;
    }

}