package io.github.goodberry_gobblers.preindexed.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;


    public static final ForgeConfigSpec.ConfigValue<Integer> DIAMON_SWORD_MAX_SLOTS;

    static {
        BUILDER.push("Preindexed Enchanting Configs");

        DIAMON_SWORD_MAX_SLOTS = defineMaxSlots("diamond sword", 9);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private static ForgeConfigSpec.ConfigValue<Integer> defineMaxSlots(String path, Integer defaultValue) {
        return BUILDER.defineInRange(path, defaultValue, -1 ,32768);
    }
}
