package com.finpack.schedule;

public enum FrequencyTypes {
    ANNUAL (1),
    SEMI_ANNUAL (2),
    QUARTERLY (4),
    MONTHLY (12);

    private final int num;
    FrequencyTypes(int i){
        this.num = i;
    }
    int getFrequency(){
        return this.num;
    }
}
