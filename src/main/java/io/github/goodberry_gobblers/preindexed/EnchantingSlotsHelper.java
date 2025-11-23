package io.github.goodberry_gobblers.preindexed;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface EnchantingSlotsHelper {
    static EnchantingSlots getUsedSlots(ItemStack itemStack) {
        if (itemStack != null) {
            return ((EnchantingSlotsHelper) itemStack.getItem()).preindexed$getUsedEnchantingSlots(itemStack);
        } else {
            return new EnchantingSlots((short) 0, (short) 0 );
        }
    }

    static EnchantingSlots getUsedSlotsFromMap(Map<Enchantment, Integer> enchants) {
        EnchantingSlots total = new EnchantingSlots((short)0, (short)0);

        for (var entry : enchants.entrySet()) total.add((short) entry.getValue().intValue(), entry.getKey().isCurse());
        return total;
    }

    static EnchantingSlots getUsedSlotsFromList(List<EnchantmentInstance> enchants) {
        Map<Enchantment, Integer> enchantMap = enchants.stream().collect(Collectors.toMap((EnchantmentInstance e) -> e.enchantment, (EnchantmentInstance e) -> e.level));

        return getUsedSlotsFromMap(enchantMap);
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