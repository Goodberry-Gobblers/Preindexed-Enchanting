package io.github.goodberry_gobblers.preindexed;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import io.github.goodberry_gobblers.preindexed.config.CommonConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Preindexed.MOD_ID)
public class Preindexed {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "preindexedenchanting";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    //this feels so jank, but I couldn't figure out how to get registryAccess from the enchantmentHelperMixin
    public static MinecraftServer serverReference;

    //Datapack datagen constants
    // max slots
    public static final ResourceKey<Registry<Map<String, Short>>> SLOTS_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MOD_ID, "maxslots"));
    public static final ResourceKey<Map<String, Short>> SLOTS_KEY = ResourceKey.create(SLOTS_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(MOD_ID, "maxslots"));
    public static final Codec<Map<String, Short>> SLOTS_MAP_CODEC = Codec.unboundedMap(Codec.STRING, Codec.SHORT);
    // incompatible enchantments
    public static final ResourceKey<Registry<List<List<String>>>> INCOMPATIBLE_ENCHANTMENTS_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MOD_ID, "incompatible"));
    public static final ResourceKey<List<List<String>>> INCOMPATIBLE_ENCHANTMENTS_KEY = ResourceKey.create(INCOMPATIBLE_ENCHANTMENTS_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(MOD_ID, "incompatible"));
    public static final Codec<List<List<String>>> INCOMPATIBLE_ENCHANTMENTS_CODEC = Codec.list(Codec.list( Codec.STRING));

    public Preindexed(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        context.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // gen data (for datapack specifically)
        modEventBus.addListener(this::onGatherData);
        modEventBus.addListener(this::registerDatapackRegistries);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    public void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                SLOTS_REGISTRY_KEY,
                SLOTS_MAP_CODEC,
                SLOTS_MAP_CODEC
        );
        event.dataPackRegistry(
                INCOMPATIBLE_ENCHANTMENTS_REGISTRY_KEY,
                INCOMPATIBLE_ENCHANTMENTS_CODEC,
                INCOMPATIBLE_ENCHANTMENTS_CODEC
        );
    }

    //datagen shit :100:
    public void onGatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
                        output,
                        event.getLookupProvider(),
                        new RegistrySetBuilder()
                                .add(SLOTS_REGISTRY_KEY, bootstrap -> {
                                    Map<String, Short> map = new java.util.HashMap<>(Map.of());
                                    //  Defaults
                                    //wood tier
                                    map.put("minecraft:wooden_shovel", (short)5);
                                    map.put("minecraft:wooden_pickaxe", (short)5);
                                    map.put("minecraft:wooden_axe", (short)9);
                                    map.put("minecraft:wooden_hoe", (short)4);
                                    map.put("minecraft:wooden_sword", (short)8);
                                    map.put("minecraft:leather_helmet", (short)5);
                                    map.put("minecraft:leather_chestplate", (short)4);
                                    map.put("minecraft:leather_leggings", (short)4);
                                    map.put("minecraft:leather_boots", (short)5);
                                    //stone tier
                                    map.put("minecraft:stone_shovel", (short)0);
                                    map.put("minecraft:stone_pickaxe", (short)0);
                                    map.put("minecraft:stone_axe", (short)0);
                                    map.put("minecraft:stone_hoe", (short)0);
                                    map.put("minecraft:stone_sword", (short)0);
                                    map.put("minecraft:chainmail_helmet", (short)0);
                                    map.put("minecraft:chainmail_chestplate", (short)0);
                                    map.put("minecraft:chainmail_leggings", (short)0);
                                    map.put("minecraft:chainmail_boots", (short)0);
                                    //iron tier
                                    map.put("minecraft:iron_shovel", (short)-3);
                                    map.put("minecraft:iron_pickaxe", (short)-3);
                                    map.put("minecraft:iron_axe", (short)-3);
                                    map.put("minecraft:iron_hoe", (short)-3);
                                    map.put("minecraft:iron_sword", (short)-3);
                                    map.put("minecraft:iron_helmet", (short)-3);
                                    map.put("minecraft:iron_chestplate", (short)-3);
                                    map.put("minecraft:iron_leggings", (short)-3);
                                    map.put("minecraft:iron_boots", (short)-3);
                                    //gold tier
                                    map.put("minecraft:golden_shovel", Short.MAX_VALUE);
                                    map.put("minecraft:golden_pickaxe", Short.MAX_VALUE);
                                    map.put("minecraft:golden_axe", Short.MAX_VALUE);
                                    map.put("minecraft:golden_hoe", Short.MAX_VALUE);
                                    map.put("minecraft:golden_sword", Short.MAX_VALUE);
                                    map.put("minecraft:golden_helmet", Short.MAX_VALUE);
                                    map.put("minecraft:golden_chestplate", Short.MAX_VALUE);
                                    map.put("minecraft:golden_leggings", Short.MAX_VALUE);
                                    map.put("minecraft:golden_boots", Short.MAX_VALUE);
                                    //diamond tier
                                    map.put("minecraft:diamond_shovel", (short)9);
                                    map.put("minecraft:diamond_pickaxe", (short)9);
                                    map.put("minecraft:diamond_axe", (short)15);
                                    map.put("minecraft:diamond_hoe", (short)7);
                                    map.put("minecraft:diamond_sword", (short)14);
                                    map.put("minecraft:diamond_helmet", (short)9);
                                    map.put("minecraft:diamond_chestplate", (short)6);
                                    map.put("minecraft:diamond_leggings", (short)8);
                                    map.put("minecraft:diamond_boots", (short)9);
                                    //netherite tier
                                    map.put("minecraft:netherite_shovel", (short)10);
                                    map.put("minecraft:netherite_pickaxe", (short)10);
                                    map.put("minecraft:netherite_axe", (short)16);
                                    map.put("minecraft:netherite_hoe", (short)8);
                                    map.put("minecraft:netherite_sword", (short)15);
                                    map.put("minecraft:netherite_helmet", (short)10);
                                    map.put("minecraft:netherite_chestplate", (short)8);
                                    map.put("minecraft:netherite_leggings", (short)10);
                                    map.put("minecraft:netherite_boots", (short)11);
                                    //misc
                                    map.put("minecraft:fishing_rod", (short)8);
                                    map.put("minecraft:flint_and_steel", (short)3);
                                    map.put("minecraft:shears", (short)3);
                                    map.put("minecraft:brush", (short)4);
                                    map.put("minecraft:elytra", (short)4);
                                    map.put("minecraft:carrot_on_a_stick", (short)3);
                                    map.put("minecraft:warped_fungus_on_a_stick", (short)4);
                                    map.put("minecraft:trident", (short)8);
                                    map.put("minecraft:shield", (short)3);
                                    map.put("minecraft:turtle_helmet", (short)9);
                                    map.put("minecraft:bow", (short)6);
                                    map.put("minecraft:crossbow", (short)8);

                                    bootstrap.register(
                                            SLOTS_KEY,
                                            map
                                    );
                                }).add(INCOMPATIBLE_ENCHANTMENTS_REGISTRY_KEY, bootstrap -> {
                                    List<List<String>> list = new ArrayList<>();

                                    list.add(List.of("minecraft:silk_touch", "minecraft:fortune"));
                                    list.add(List.of("minecraft:riptide", "minecraft:loyalty"));

                                    bootstrap.register(
                                            INCOMPATIBLE_ENCHANTMENTS_KEY,
                                            list
                                    );
                                }),
                        Set.of(MOD_ID)
                )
        );
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        serverReference = event.getServer();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}