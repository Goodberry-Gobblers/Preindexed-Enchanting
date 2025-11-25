package io.github.goodberry_gobblers.preindexed.mixins;

import io.github.goodberry_gobblers.preindexed.EnchantingSlotsHelper;
import io.github.goodberry_gobblers.preindexed.Preindexed;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(EnchantmentMenu.class)
public class EnchantmentMenuMixin {
    @Redirect(method = "slotsChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEnchantable()Z"))
    private boolean isEnchantable(ItemStack instance) {
        Optional<Short> maxSlots = EnchantingSlotsHelper.getMaxSlots(instance, Preindexed.serverReference.overworld());
        if (maxSlots.isPresent()) {
            return instance.isEnchantable() && maxSlots.get() != Short.MIN_VALUE;
        }
        return instance.isEnchantable();
    }
}
