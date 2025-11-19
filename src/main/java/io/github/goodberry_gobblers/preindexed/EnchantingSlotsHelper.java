package io.github.goodberry_gobblers.preindexed;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public interface EnchantingSlotsHelper {
    static EnchantingSlots getUsedSlots(ItemStack itemStack) {
        if (itemStack != null) {
            return ((EnchantingSlotsHelper) itemStack.getItem()).preindexed$getUsedEnchantingSlots(itemStack);
        } else {
            return new EnchantingSlots((short) 0, (short) 0 );
        }
    }

    static Optional<Short> getMaxSlots(ItemStack itemStack, Level level) {
        if (itemStack != null) {
            return ((EnchantingSlotsHelper) itemStack.getItem()).preindexed$getMaxEnchantingSlots(itemStack, level);
        } else {
            return Optional.empty();
        }
    }

    EnchantingSlots preindexed$getUsedEnchantingSlots(ItemStack itemStack);

    Optional<Short> preindexed$getMaxEnchantingSlots(ItemStack itemStack, Level level);

}