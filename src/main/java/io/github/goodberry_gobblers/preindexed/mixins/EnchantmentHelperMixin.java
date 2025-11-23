package io.github.goodberry_gobblers.preindexed.mixins;

import com.google.common.collect.Lists;
import io.github.goodberry_gobblers.preindexed.EnchantingSlots;
import io.github.goodberry_gobblers.preindexed.EnchantingSlotsHelper;
import io.github.goodberry_gobblers.preindexed.Preindexed;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.item.enchantment.EnchantmentHelper.getAvailableEnchantmentResults;

//legend has it I knew what this all meant when I wrote it
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @Inject(method = "selectEnchantment", at = @At(value = "HEAD"), cancellable = true)
    private static void selectEnchantment(RandomSource pRandom, ItemStack pItemStack, int pLevel, boolean pAllowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        Item item = pItemStack.getItem();
        int i = item.getEnchantmentValue();
        if (i <= 0) {
            cir.setReturnValue(list);
        }
        pLevel += 1 + pRandom.nextInt(i / 4 + 1) + pRandom.nextInt(i / 4 + 1);
        float f = (pRandom.nextFloat() + pRandom.nextFloat() - 1.0F) * 0.15F;
        pLevel = Mth.clamp(Math.round((float)pLevel + (float)pLevel * f), 1, Integer.MAX_VALUE);
        List<EnchantmentInstance> list1 = getAvailableEnchantmentResults(pLevel, pItemStack, pAllowTreasure);

        if (!list1.isEmpty()) {
            int length = (int) Math.max(1, (pRandom.nextGaussian() + 3 * (float)pLevel/50) * 2);

            //create shuffled list of enchantments
            List<EnchantmentInstance> list2 = Lists.newArrayList();
            while (!list1.isEmpty()) {
                WeightedRandom.getRandomItem(pRandom, list1).ifPresent((EnchantmentInstance e) -> {
                    list2.add(e);
                    list1.remove(e);
                });
            }
            List<EnchantmentInstance> list3 = list2.subList(0, Math.min(length, list2.size()));

            Optional<Short> maxSlots = EnchantingSlotsHelper.getMaxSlots(pItemStack, Preindexed.serverReference.overworld());
            if (maxSlots.isPresent()) {
                if (maxSlots.get() > 0) {
                    for (int j = list3.size(); j > 0; j--) {
                        list = list3.subList(0, j);
                        EnchantingSlots slots = EnchantingSlotsHelper.getUsedSlotsFromList(list);
                        if (slots.getUsedSlots() <= slots.getCursedSlots() + maxSlots.get()) {
                            break;
                        }
                    }
                } else if (maxSlots.get() < 0) {
                    for (EnchantmentInstance e : list3) {
                        list.add(new EnchantmentInstance(e.enchantment, Math.min(e.level, Math.abs(maxSlots.get()))));
                    }
                }
            }
            if (item.equals(Items.BOOK)) {
                list = list3;
            }
        }
        cir.setReturnValue(list);
    }

    @Redirect(method = "getAvailableEnchantmentResults", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxCost(I)I"))
    private static int removeMaxCheck(Enchantment instance, int pLevel) {
        return Integer.MAX_VALUE;
    }
}
