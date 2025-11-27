package io.github.goodberry_gobblers.preindexed;

import com.mojang.logging.LogUtils;
import io.github.goodberry_gobblers.preindexed.config.CommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.TagTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public interface EnchantingSlotsHelper {
    static EnchantingSlots getUsedSlots(ItemStack itemStack) {
        if (itemStack != null) {
            return getUsedSlots(IncompatibleEnchantHelper.flatten(itemStack.copy()).getAllEnchantments());
        } else {
            return new EnchantingSlots((short) 0, (short) 0 );
        }
    }

    static EnchantingSlots getUsedSlots(List<EnchantmentInstance> enchants) {
        Map<Enchantment, Integer> enchantMap = enchants.stream().collect(Collectors.toMap((EnchantmentInstance e) -> e.enchantment, (EnchantmentInstance e) -> e.level));

        return getUsedSlots(enchantMap);
    }

    static EnchantingSlots getUsedSlots(Map<Enchantment, Integer> enchants) {
        EnchantingSlots total = new EnchantingSlots((short)0, (short)0);

        for (var entry : enchants.entrySet()) total.add((short) entry.getValue().intValue(), entry.getKey().isCurse());
        return total;
    }

    static Optional<Short> getMaxSlots(ItemStack itemStack, Level level) {
        CompoundTag itemTag = itemStack.getTag();
        List<MaxSlotModifier> modifierList = new ArrayList<>();
        if (itemTag != null) {
            modifierList = MaxSlotModifier.fromTags(itemTag.getList("maxSlotModifiers", 10));

            if (itemTag.contains("maxSlotOverride")) {
                return Optional.of(MaxSlotModifier.applyModifiers(itemTag.getShort("maxSlotOverride"), modifierList));
            }
        }


        Optional<Registry<Map<String, Short>>> slotsRegistry;
        if (level.isClientSide) {
            slotsRegistry = Objects.requireNonNull(Minecraft.getInstance().getConnection()).registryAccess().registry(Preindexed.SLOTS_REGISTRY_KEY);
        } else {
            slotsRegistry = Objects.requireNonNull(level.getServer()).registryAccess().registry(Preindexed.SLOTS_REGISTRY_KEY);
        }

        if (slotsRegistry.isPresent()) {
            Optional<Short> out = Optional.ofNullable(slotsRegistry.get().get(Preindexed.SLOTS_KEY).get(ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString()));
            if (out.isPresent()) {
                return Optional.of(MaxSlotModifier.applyModifiers(out.get(), modifierList));
            }
        }
        return Optional.empty();
    }

    static boolean isOverBudget(ItemStack itemStack, Level level) {
        Optional<Short> maxSlots = EnchantingSlotsHelper.getMaxSlots(itemStack, level);
        if (maxSlots.isPresent()) {
            return isOverBudget(maxSlots.get(), itemStack);
        }
        return false;
    }

    static boolean isOverBudget(short maxSlots, ItemStack itemStack) {
        if (maxSlots != Short.MAX_VALUE) {
            return isOverBudget(maxSlots, EnchantingSlotsHelper.getUsedSlots(itemStack), itemStack.getAllEnchantments());
        }
        return false;
    }

    static boolean isOverBudget(short maxSlots, EnchantingSlots enchantingSlots, List<EnchantmentInstance> enchantList) {
        return isOverBudget(maxSlots, enchantingSlots, enchantList.stream().collect(Collectors.toMap((EnchantmentInstance e) -> e.enchantment, (EnchantmentInstance e) -> e.level)));
    }

    static boolean isOverBudget(short maxSlots, EnchantingSlots enchantingSlots, Map<Enchantment, Integer> enchantMap) {
        if (maxSlots == Short.MIN_VALUE && enchantingSlots.getUsedSlots() > 0) {
            return true;
        } else if (maxSlots < 0) {
            return enchantMap.values().stream().anyMatch(enchantLevel -> enchantLevel > Math.abs(maxSlots));
        } else {
            return enchantingSlots.getUsedSlots() > maxSlots + CommonConfig.CURSE_SLOT_VALUE.get() * enchantingSlots.getCursedSlots();
        }
    }
}