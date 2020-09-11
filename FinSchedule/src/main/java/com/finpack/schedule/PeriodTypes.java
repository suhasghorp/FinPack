package com.finpack.schedule;

public enum PeriodTypes {
    DAY (1),
    WEEKS (2),
    MONTHS (3),
    YEARS (4);

    private final int num;
    PeriodTypes(int i){
        this.num = i;
    }

}
