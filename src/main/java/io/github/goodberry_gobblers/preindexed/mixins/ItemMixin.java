package io.github.goodberry_gobblers.preindexed.mixins;

import io.github.goodberry_gobblers.preindexed.IncompatibleEnchantHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "overrideOtherStackedOnMe", at = @At(value = "HEAD"), cancellable = true)
    private void onInventoryClick(ItemStack pStack, ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess, CallbackInfoReturnable<Boolean> cir) {
        if (pAction == ClickAction.SECONDARY && pOther.isEmpty() && pStack.isEnchanted()) {
            IncompatibleEnchantHelper.resolveConflicts(pStack, pPlayer.level());
            if (IncompatibleEnchantHelper.swap(pStack, pPlayer.level())) {
                if (pPlayer.level().isClientSide) {
                    pPlayer.playSound(SoundEvents.AMETHYST_BLOCK_RESONATE);
                }
                cir.setReturnValue(true);
            }
        }
    }
}