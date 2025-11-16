package io.github.goodberry_gobblers.preindexed.mixins;

import io.github.goodberry_gobblers.preindexed.EnchantingSlots;
import io.github.goodberry_gobblers.preindexed.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(Item.class)
public abstract class ItemMixin implements EnchantingSlots {
    @Override
    public int preindexed$getEnchantingSlots(ItemStack itemStack) {
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(itemStack);

        return enchants.values().stream().reduce(0, Integer::sum);
    }
}

@Mixin(ItemStack.class)
abstract
class ItemStackMixin {
    @Shadow public abstract Item getItem();

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER, ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    public void getTooltipLines(Player pPlayer, TooltipFlag pIsAdvanced, CallbackInfoReturnable<List<Component>> cir, List list) {
        if (this.getItem() != Items.ENCHANTED_BOOK) {
            ChatFormatting style = ChatFormatting.BLUE;

            Integer maxSlots = CommonConfig.DIAMON_SWORD_MAX_SLOTS.get();

            list.add(Component.translatable(
                    "item.preindexed.enchant_slots_tooltip",
                    EnchantingSlots.getUsedSlots((ItemStack) (Object) this),
                    maxSlots == -1 ? "\u221e": maxSlots
            ).withStyle(style));
        }
    }
}