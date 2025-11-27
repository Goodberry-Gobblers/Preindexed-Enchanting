package io.github.goodberry_gobblers.preindexed.mixins;

import io.github.goodberry_gobblers.preindexed.EnchantingSlotsHelper;
import io.github.goodberry_gobblers.preindexed.IncompatibleEnchantHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import java.util.Optional;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    @Shadow @Final private DataSlot cost;

    public AnvilMenuMixin(@Nullable MenuType<?> pType, int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(pType, pContainerId, pPlayerInventory, pAccess);
    }

    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;setEnchantments(Ljava/util/Map;Lnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.AFTER), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void stopOverenchanting(CallbackInfo ci, ItemStack itemstack, int i, int j, int k, ItemStack itemstack1, ItemStack itemstack2, Map map, boolean flag, int k2) {
        Level level = this.player.level();
        Optional<Short> maxSlots = EnchantingSlotsHelper.getMaxSlots(itemstack1, level);
        if (maxSlots.isPresent()) {
            if (maxSlots.get() == Short.MIN_VALUE || EnchantingSlotsHelper.isOverBudget(itemstack1, level)) {
                this.resultSlots.setItem(0, ItemStack.EMPTY);
                this.cost.set(0);
                ci.cancel();
            }
        }

        IncompatibleEnchantHelper.resolveConflicts(itemstack1, this.player.level());
    }

    @Unique
    private static ItemStack preindexed$flatten(ItemStack itemStack) {
        if (itemStack.getItem() != Items.ENCHANTED_BOOK) IncompatibleEnchantHelper.flatten(itemStack);
        return itemStack;
    }

    @ModifyVariable(method = "createResult", at = @At("STORE"), ordinal = 1)
    private ItemStack flatten1(ItemStack value) {return preindexed$flatten(value);};
    @ModifyVariable(method = "createResult", at = @At("STORE"), ordinal = 2)
    private ItemStack flatten2(ItemStack value) {return preindexed$flatten(value);};
}
