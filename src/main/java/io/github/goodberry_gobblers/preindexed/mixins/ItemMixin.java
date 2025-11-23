package io.github.goodberry_gobblers.preindexed.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.goodberry_gobblers.preindexed.EnchantingSlots;
import io.github.goodberry_gobblers.preindexed.EnchantingSlotsHelper;
import io.github.goodberry_gobblers.preindexed.Preindexed;
import io.github.goodberry_gobblers.preindexed.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Mixin(Item.class)
public abstract class ItemMixin implements EnchantingSlotsHelper {
    @Shadow @Final private static Logger LOGGER;

    @Override
    public EnchantingSlots preindexed$getUsedEnchantingSlots(ItemStack itemStack) {
        return EnchantingSlotsHelper.getUsedSlotsFromMap(EnchantmentHelper.getEnchantments(itemStack));
    }

    @Override
    public Optional<Short> preindexed$getMaxEnchantingSlots(ItemStack itemStack, Level level) {
        Optional<Registry<Map<String, Short>>> slotsRegistry;
        if (level.isClientSide) {
            slotsRegistry = Objects.requireNonNull(Minecraft.getInstance().getConnection()).registryAccess().registry(Preindexed.SLOTS_REGISTRY_KEY);
        } else {
            slotsRegistry = Objects.requireNonNull(level.getServer()).registryAccess().registry(Preindexed.SLOTS_REGISTRY_KEY);
        }

        if (slotsRegistry.isPresent()) {
            return Optional.ofNullable(slotsRegistry.get().get(Preindexed.SLOTS_KEY).get(ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString()));
        }
        return Optional.empty();
    }
}

@Mixin(ItemStack.class)
class ItemStackMixin {
    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER, ordinal = 0))
    public void getTooltipLines(Player pPlayer, TooltipFlag pIsAdvanced, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
        Optional<Short> maxSlotsOptional = EnchantingSlotsHelper.getMaxSlots((ItemStack) (Object) this, Minecraft.getInstance().level);
        if (maxSlotsOptional.isPresent() && maxSlotsOptional.get() != null) {
            short maxSlots = maxSlotsOptional.get();
            EnchantingSlots usedSlots = EnchantingSlotsHelper.getUsedSlots((ItemStack) (Object) this);

            if (maxSlots < 0) {
                list.add(Component.translatable(
                        "item.preindexed.enchant_limit_tooltip"
                ).append(CommonComponents.SPACE).append(Component.translatable(
                        "enchantment.level." + Math.abs(maxSlots)
                )).withStyle(ChatFormatting.DARK_PURPLE));
            } else if (maxSlots == 0) {
                list.add(Component.translatable(
                        "item.preindexed.unenchantable_tooltip"
                ).withStyle(ChatFormatting.DARK_RED));
            } else {
                int curseModifier = CommonConfig.CURSE_SLOT_VALUE.get() * usedSlots.getCursedSlots();
                Component tooltip = Component.translatable(
                        "item.preindexed.enchant_slots_tooltip",
                        usedSlots.getUsedSlots(),
                        maxSlots == Short.MAX_VALUE ? "\u221e" : maxSlots
                ).withStyle(usedSlots.getUsedSlots() <= maxSlots + curseModifier? ChatFormatting.BLUE : ChatFormatting.DARK_RED);
                if (usedSlots.hasCursedSlots()) {
                    if (curseModifier != 0) {
                        tooltip = tooltip.copy().append(CommonComponents.SPACE).append(Component.translatable(
                                curseModifier > 0 ? "item.preindexed.cursed_slots_pos_tooltip" : "item.preindexed.cursed_slots_neg_tooltip",
                                Math.min(Short.MAX_VALUE, curseModifier)
                        ).withStyle(ChatFormatting.RED));
                    }
                }
                list.add(tooltip);
            }
        }
    }
}