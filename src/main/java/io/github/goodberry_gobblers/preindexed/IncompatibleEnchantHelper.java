package io.github.goodberry_gobblers.preindexed;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public interface IncompatibleEnchantHelper {

    //reinstates all stored enchantments
    static ItemStack flatten(ItemStack itemStack) {
        if (itemStack.getItem() != Items.ENCHANTED_BOOK) {
            ListTag fakeEnchants = EnchantedBookItem.getEnchantments(itemStack);
            for (int i = 0; i < fakeEnchants.size(); i++) {
                CompoundTag compoundTag = fakeEnchants.getCompound(i);
                Optional<Enchantment> enchant = Optional.ofNullable(ForgeRegistries.ENCHANTMENTS.getValue(EnchantmentHelper.getEnchantmentId(compoundTag)));
                enchant.ifPresent(enchantment -> itemStack.enchant(enchantment, EnchantmentHelper.getEnchantmentLevel(compoundTag)));
            }
            itemStack.removeTagKey("StoredEnchantments");
        }
        return itemStack;
    }

    //tries to swap enchants. returns if it was successful
    static boolean swap(ItemStack itemStack, Level level) {
        Optional<List<List<String>>> conflictListList = getIncompatibilityMap(level);
        if (conflictListList.isPresent() && itemStack.getItem() != Items.ENCHANTED_BOOK) {
            ListTag fakeEnchants = EnchantedBookItem.getEnchantments(itemStack);
            if (!fakeEnchants.isEmpty()) {
                Map<Enchantment, Integer> enchantMap = itemStack.getAllEnchantments();

                for (int i = 0; i < enchantMap.size(); i++) {
                    Enchantment activeEnchant = new ArrayList<>(enchantMap.keySet()).get(i);

                    for (List<String> list : conflictListList.get()) {
                        if (list.contains(Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getKey(activeEnchant)).toString())) {

                            for (int j = 0; j < fakeEnchants.size(); j++) {
                                CompoundTag compoundtag = fakeEnchants.getCompound(j);
                                Enchantment fakeEnchant = ForgeRegistries.ENCHANTMENTS.getValue(EnchantmentHelper.getEnchantmentId(compoundtag));
                                if (list.contains(Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getKey(fakeEnchant)).toString())) {
                                    //  swap fake and active enchants
                                    itemStack.getEnchantmentTags().remove(i);
                                    itemStack.getEnchantmentTags().addTag(i, EnchantmentHelper.storeEnchantment(EnchantmentHelper.getEnchantmentId(compoundtag), EnchantmentHelper.getEnchantmentLevel(compoundtag)));
                                    fakeEnchants.remove(j);
                                    EnchantedBookItem.addEnchantment(itemStack, new EnchantmentInstance(activeEnchant, enchantMap.get(activeEnchant)));

                                    itemStack.getOrCreateTag().put("StoredEnchantments", fakeEnchants);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    //moves all but the first conflicting enchant to StoredEnchantments for every set of mutex enchants
    static void resolveConflicts(ItemStack itemStack, Level level) {
        List<List<Enchantment>> conflictListList = getConflicts(itemStack, level);
        if (!conflictListList.isEmpty()) {
            for (List<Enchantment> list : conflictListList) {
                for (Enchantment enchant : list.subList(1, list.size())) {
                    Map<Enchantment, Integer> enchantMap = itemStack.getAllEnchantments();
                    for (int i = 0; i < enchantMap.size(); ++i) {
                        if (new ArrayList<>(enchantMap.keySet()).get(i) == enchant) {
                            itemStack.getEnchantmentTags().remove(i);
                            EnchantedBookItem.addEnchantment(itemStack, new EnchantmentInstance(enchant, enchantMap.get(enchant)));
                        }
                    }
                }
            }
        }
    }

    //returns a list of sets of mutex enchantments
    static List<List<Enchantment>> getConflicts(ItemStack itemStack, Level level) {
        Optional<List<List<String>>> list = getIncompatibilityMap(level);
        if (list.isPresent()) {
            Map<Enchantment, Integer> enchantMap = itemStack.getAllEnchantments();
            List<List<Enchantment>> out = new ArrayList<>();
            for (List<String> list1: list.get()) {
                List<Enchantment> conflictList = enchantMap.keySet().stream().filter(e -> list1.contains(Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getKey(e)).toString())).toList();

                if (conflictList.size() > 1) out.add(conflictList);
            }
            return out.stream().filter(l -> !l.isEmpty()).toList();
        }
        return List.of();
    }

    //read list of sets of mutex enchants from the datapack
    private static Optional<List<List<String>>> getIncompatibilityMap(Level level) {
        Optional<Registry<List<List< String>>>> slotsRegistry;
        if (level.isClientSide) {
            slotsRegistry = Objects.requireNonNull(Minecraft.getInstance().getConnection()).registryAccess().registry(Preindexed.INCOMPATIBLE_ENCHANTMENTS_REGISTRY_KEY);
        } else {
            slotsRegistry = level.registryAccess().registry(Preindexed.INCOMPATIBLE_ENCHANTMENTS_REGISTRY_KEY);
        }

        if (slotsRegistry.isPresent()) {
            return slotsRegistry.get().getOptional(Preindexed.INCOMPATIBLE_ENCHANTMENTS_KEY);
        }
        return Optional.empty();
    }
}
