package com.smartVend.app.model;

public class MachineBalance {
    private int fiveCentsCount;
    private int tenCentsCount;
    private int twentyCentsCount;
    private int fiftyCentsCount;
    private int oneEurosCount;
    private int twoEurosCount;

    public MachineBalance() {}

    public MachineBalance(int fiveCentsCount, int tenCentsCount, int twentyCentsCount,
                          int fiftyCentsCount, int oneEurosCount, int twoEurosCount) {
        this.fiveCentsCount = fiveCentsCount;
        this.tenCentsCount = tenCentsCount;
        this.twentyCentsCount = twentyCentsCount;
        this.fiftyCentsCount = fiftyCentsCount;
        this.oneEurosCount = oneEurosCount;
        this.twoEurosCount = twoEurosCount;
    }

    // Getter e setter qui...
}
