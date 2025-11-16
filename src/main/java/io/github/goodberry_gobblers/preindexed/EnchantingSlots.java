package io.github.goodberry_gobblers.preindexed;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface EnchantingSlots {
    static int getUsedSlots(ItemStack itemStack) {
        if (itemStack != null) {
            return ((EnchantingSlots) itemStack.getItem()).preindexed$getEnchantingSlots(itemStack);
        } else {
            return 0;
        }
    }

    static int getMaxSlots(Item item) {
        if (item != null) {
            return ((EnchantingSlots) item).preindexed$getMaxEnchantingSlots(item);
        } else {
            return 0;
        }
    }

    int preindexed$getEnchantingSlots(ItemStack itemStack);

    int preindexed$getMaxEnchantingSlots(Item item);
}