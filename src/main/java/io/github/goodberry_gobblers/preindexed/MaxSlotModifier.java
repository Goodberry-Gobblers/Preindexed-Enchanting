package io.github.goodberry_gobblers.preindexed;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class MaxSlotModifier {
    //id
    private final String id;
    //numerical modifier
    private final float modifier;
    //operation to use between the modifier and current max slots count
    private final ModifierOperation operation;

    public enum ModifierOperation {
        MULTIPLICATION_BASE,
        ADDITION,
        MULTIPLICATION_TOTAL;

        public String toString() {
            return switch (this) {
                case MULTIPLICATION_BASE -> "mult_base";
                case ADDITION -> "add";
                case MULTIPLICATION_TOTAL -> "mult_total";
            };
        }

    }
    public String getId() {
        return this.id;
    }

    public static ModifierOperation operationFromString(String string) {
        for (ModifierOperation op : ModifierOperation.values()) {
            if (op.toString().equals(string)) {
                return op;
            }
        }
        return null;
    }

    public MaxSlotModifier(String id, float modifier, ModifierOperation operation) {
        this.id = id;
        this.modifier = modifier;
        this.operation = operation;
    }

    public CompoundTag getTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", this.id);
        tag.putFloat("modifier", this.modifier);
        tag.putString("operation", this.operation.toString());

        return tag;
    }

    public static List<MaxSlotModifier> fromTags(ListTag listTag) {
        List<MaxSlotModifier> out = new ArrayList<>();
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag tag = listTag.getCompound(i);
            out.add(new MaxSlotModifier(
                    tag.getString("id"),
                    tag.getFloat("modifier"),
                    operationFromString(tag.getString("operation"))
            ));
        }
        return out;
    }

    public static short applyModifiers(short base, List<MaxSlotModifier> modifiers) {
        if (base > 0 && base != Short.MAX_VALUE) {
            List<MaxSlotModifier> sortedModifiers = modifiers.stream()
                    .filter(distinctByKey(MaxSlotModifier::getId))
                    .sorted(Comparator.comparing(o -> o.operation))
                    .toList();

            short out = base;
            for (MaxSlotModifier modifier : sortedModifiers) {
                switch (modifier.operation) {
                    case MULTIPLICATION_BASE, MULTIPLICATION_TOTAL ->
                            out = (short) Math.max(Math.min(Math.ceil(out * modifier.modifier), Short.MAX_VALUE), 0);
                    case ADDITION ->
                            out = (short) Math.max(Math.min(Math.ceil(out + modifier.modifier), Short.MAX_VALUE), 0);
                }
            }
            return out;
        }
        return base;
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
