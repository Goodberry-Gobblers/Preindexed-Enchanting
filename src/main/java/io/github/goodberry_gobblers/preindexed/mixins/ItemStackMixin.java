package io.github.goodberry_gobblers.preindexed.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.goodberry_gobblers.preindexed.EnchantingSlots;
import io.github.goodberry_gobblers.preindexed.EnchantingSlotsHelper;
import io.github.goodberry_gobblers.preindexed.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(ItemStack.class)
abstract
class ItemStackMixin {
    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER, ordinal = 0))
    private void addSlotsTooltip(Player pPlayer, TooltipFlag pIsAdvanced, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
        Optional<Short> maxSlotsOptional = EnchantingSlotsHelper.getMaxSlots((ItemStack) (Object) this, Minecraft.getInstance().level);
        if (maxSlotsOptional.isPresent()) {
            short maxSlots = maxSlotsOptional.get();
            EnchantingSlots usedSlots = EnchantingSlotsHelper.getUsedSlots((ItemStack) (Object) this);
            boolean isOverBudget = EnchantingSlotsHelper.isOverBudget(maxSlots, (ItemStack) (Object) this);

            if (maxSlots == Short.MIN_VALUE) {
                list.add(Component.translatable(
                        "item.preindexed.unenchantable_tooltip"
                ).withStyle(ChatFormatting.DARK_RED));

            } else if (maxSlots < 0) {
                list.add(Component.translatable(
                        "item.preindexed.enchant_limit_tooltip"
                ).append(CommonComponents.SPACE).append(Component.translatable(
                        "enchantment.level." + Math.abs(maxSlots)
                )).withStyle(isOverBudget ? ChatFormatting.DARK_RED : ChatFormatting.DARK_PURPLE));

            } else {
                int curseModifier = CommonConfig.CURSE_SLOT_VALUE.get() * usedSlots.getCursedSlots();
                Component tooltip = Component.translatable(
                        "item.preindexed.enchant_slots_tooltip",
                        usedSlots.getUsedSlots(),
                        maxSlots == Short.MAX_VALUE ? "\u221e" : maxSlots
                ).withStyle(isOverBudget ? ChatFormatting.DARK_RED : ChatFormatting.BLUE);

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

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;appendEnchantmentNames(Ljava/util/List;Lnet/minecraft/nbt/ListTag;)V", shift = At.Shift.AFTER))
    private void addDisabledEnchantmentsTooltip(Player pPlayer, TooltipFlag pIsAdvanced, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
        ListTag fakeEnchants = EnchantedBookItem.getEnchantments((ItemStack) (Object) this);
        for(int i = 0; i < fakeEnchants.size(); ++i) {
            CompoundTag compoundtag = fakeEnchants.getCompound(i);
            list.add(ForgeRegistries.ENCHANTMENTS.getValue(EnchantmentHelper.getEnchantmentId(compoundtag)).getFullname(EnchantmentHelper.getEnchantmentLevel(compoundtag)).copy().withStyle(ChatFormatting.ITALIC));
        }
    }
}