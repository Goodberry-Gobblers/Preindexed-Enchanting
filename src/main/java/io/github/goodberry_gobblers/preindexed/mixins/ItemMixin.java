package io.github.goodberry_gobblers.preindexed.mixins;

import com.mojang.logging.LogUtils;
import io.github.goodberry_gobblers.preindexed.IncompatibleEnchantHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "overrideOtherStackedOnMe", at = @At(value = "HEAD"), cancellable = true)
    private void onInventoryClick(ItemStack pStack, ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess, CallbackInfoReturnable<Boolean> cir) {
        // right click specifically
        if (pAction == ClickAction.SECONDARY && pStack.isEnchanted()) {
            LogUtils.getLogger().info(pOther.toString());
            if (!IncompatibleEnchantHelper.getConflicts(pStack, pPlayer.level()).isEmpty()) {
                IncompatibleEnchantHelper.resolveConflicts(pStack, pPlayer.level());
            }
            if (IncompatibleEnchantHelper.swap(pStack, pPlayer.level())) {
                if (pPlayer.level().isClientSide) {
                    pPlayer.playSound(SoundEvents.AMETHYST_BLOCK_RESONATE);
                }
                cir.setReturnValue(true);
            }
        }
    }
}