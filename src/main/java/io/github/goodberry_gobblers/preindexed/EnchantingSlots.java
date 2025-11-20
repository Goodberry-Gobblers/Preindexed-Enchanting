package io.github.goodberry_gobblers.preindexed;

public class EnchantingSlots {
    private short usedSlots;
    private short cursedSlots;

    public EnchantingSlots(short base, short cursed) {
        this.usedSlots = base;
        this.cursedSlots = cursed;
    }

    public short getUsedSlots() {
        return this.usedSlots;
    }

    public void setUsedSlots(short i) {
        this.usedSlots = i;
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
            this.usedSlots += i;
        }
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", this.usedSlots, this.cursedSlots);
    }
}
