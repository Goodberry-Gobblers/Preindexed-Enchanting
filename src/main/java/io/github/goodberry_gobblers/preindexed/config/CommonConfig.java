package io.github.goodberry_gobblers.preindexed.config;

import net.minecraftforge.common.ForgeConfigSpec;


public class CommonConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> CURSE_SLOT_VALUE;

    static {
        BUILDER.push("Preindexed Enchanting Configs");
        BUILDER.comment("The amount a single curse increases an item's max enchantment slots by.");
        CURSE_SLOT_VALUE = BUILDER.defineInRange("Curse value:", 2, -32767, 32767);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}