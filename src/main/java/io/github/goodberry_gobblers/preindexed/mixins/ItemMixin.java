package io.github.goodberry_gobblers.preindexed.mixins;

import io.github.goodberry_gobblers.preindexed.EnchantingSlots;
import io.github.goodberry_gobblers.preindexed.Preindexed;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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

    @Shadow @Final private static Logger LOGGER;

    @Shadow public abstract boolean isCorrectToolForDrops(BlockState pState);

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER, ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    public void getTooltipLines(Player pPlayer, TooltipFlag pIsAdvanced, CallbackInfoReturnable<List<Component>> cir, List<Component> list) {
        Optional<Registry<Map<String, Short>>> slotsRegistry = Objects.requireNonNull(Minecraft.getInstance().getConnection()).registryAccess().registry(Preindexed.SLOTS_REGISTRY_KEY);
        if (slotsRegistry.isPresent()) {
            ResourceLocation location = ForgeRegistries.ITEMS.getKey(this.getItem());
            if (location != null) {
                Short maxSlots = Objects.requireNonNull(slotsRegistry.get().get(Preindexed.SLOTS_KEY)).get(location.toString());
                if (maxSlots != null) {
                    Integer usedSlots = EnchantingSlots.getUsedSlots((ItemStack) (Object) this);

                    if (maxSlots < 0) {
                        list.add(Component.translatable(
                                "item.preindexed.enchant_limit_tooltip",
                                Math.abs(maxSlots)
                        ).withStyle(ChatFormatting.DARK_PURPLE));
                    } else if (maxSlots == 0) {
                        list.add(Component.translatable(
                                "item.preindexed.unenchantable_tooltip"
                        ).withStyle(ChatFormatting.DARK_RED));
                    } else {
                        list.add(Component.translatable(
                                "item.preindexed.enchant_slots_tooltip",
                                usedSlots,
                                maxSlots == Short.MAX_VALUE ? "\u221e" : maxSlots
                        ).withStyle(ChatFormatting.BLUE));
                    }
                }
            }
        }
    }
}