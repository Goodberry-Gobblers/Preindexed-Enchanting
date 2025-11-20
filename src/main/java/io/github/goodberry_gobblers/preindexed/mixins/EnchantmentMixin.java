package io.github.goodberry_gobblers.preindexed.mixins;


import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Inject(method = "isCompatibleWith", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;checkCompatibility(Lnet/minecraft/world/item/enchantment/Enchantment;)Z"), cancellable = true)
    private void injectAlwaysTrue(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
