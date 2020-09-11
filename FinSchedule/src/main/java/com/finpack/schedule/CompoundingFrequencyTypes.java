package com.finpack.schedule;

public enum CompoundingFrequencyTypes {
    SIMPLE_INTEREST(0),
    CONTINOUS_INTEREST(-1),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    SIX(6),
    TWELVE(12);

    private final int num;
    CompoundingFrequencyTypes(int i){
        this.num = i;
    }
    int getFrequency(){
        return this.num;
    }

}
