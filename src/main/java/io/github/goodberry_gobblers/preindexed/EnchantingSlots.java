package io.github.goodberry_gobblers.preindexed;

public class EnchantingSlots {
    private short baseSlots;
    private short cursedSlots;

    public EnchantingSlots(short base, short cursed) {
        this.baseSlots = base;
        this.cursedSlots = cursed;
    }

    public short getBaseSlots() {
        return this.baseSlots;
    }

    public void setBaseSlots(short i) {
        this.baseSlots = i;
    }

    public short getCursedSlots() {
        return this.cursedSlots;
    }

    public boolean hasCursedSlots() {
        return this.cursedSlots != 0;
    }

    public void setCursedSlots(short i) {
        this.cursedSlots = i;
    }

    public void add(short i, boolean isCursed) {
        if (isCursed) {
            this.cursedSlots += i;
        } else {
            this.baseSlots += i;
        }
    }
}
