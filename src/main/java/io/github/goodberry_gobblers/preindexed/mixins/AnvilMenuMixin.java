package io.github.goodberry_gobblers.preindexed.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.goodberry_gobblers.preindexed.EnchantingSlots;
import io.github.goodberry_gobblers.preindexed.EnchantingSlotsHelper;
import io.github.goodberry_gobblers.preindexed.config.CommonConfig;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import java.util.Optional;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    @Shadow @Final private DataSlot cost;

    @Shadow @Final private static Logger LOGGER;

    public AnvilMenuMixin(@Nullable MenuType<?> pType, int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(pType, pContainerId, pPlayerInventory, pAccess);
    }

    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;setEnchantments(Ljava/util/Map;Lnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void stopOverenchanting(CallbackInfo ci, @Local(ordinal = 1) ItemStack itemStack1) {
        EnchantingSlots enchantingSlots = EnchantingSlotsHelper.getUsedSlots(itemStack1);
        Level level = this.player.level();
        Optional<Short> maxSlots = (((EnchantingSlotsHelper) itemStack1.getItem())).preindexed$getMaxEnchantingSlots(itemStack1, level);
        if (maxSlots.isPresent() && enchantingSlots.getUsedSlots() > maxSlots.get() + CommonConfig.CURSE_SLOT_VALUE.get() * enchantingSlots.getCursedSlots()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
            ci.cancel();
        }
    }
}
