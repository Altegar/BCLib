package org.betterx.bclib;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.impl.biome.NetherBiomeData;
import net.fabricmc.loader.api.FabricLoader;

import org.betterx.bclib.api.WorldDataAPI;
import org.betterx.bclib.api.dataexchange.DataExchangeAPI;
import org.betterx.bclib.api.dataexchange.handler.autosync.*;
import org.betterx.bclib.api.features.blockpredicates.Types;
import org.betterx.bclib.api.features.placement.PlacementModifiers;
import org.betterx.bclib.api.surface.rules.Conditions;
import org.betterx.bclib.api.tag.TagAPI;
import org.betterx.bclib.commands.CommandRegistry;
import org.betterx.bclib.config.Configs;
import org.betterx.bclib.presets.worldgen.BCLWorldPresets;
import org.betterx.bclib.recipes.AnvilRecipe;
import org.betterx.bclib.recipes.CraftingRecipes;
import org.betterx.bclib.registry.BaseBlockEntities;
import org.betterx.bclib.registry.BaseRegistry;
import org.betterx.bclib.util.Logger;
import org.betterx.bclib.world.generator.BCLibEndBiomeSource;
import org.betterx.bclib.world.generator.BCLibNetherBiomeSource;
import org.betterx.bclib.world.generator.GeneratorOptions;
import org.betterx.bclib.world.structures.TemplatePiece;

import java.util.List;

public class BCLib implements ModInitializer {
    public static final String MOD_ID = "bclib";
    public static final String TOGETHER_WORLDS = "worlds_together";
    public static final Logger LOGGER = new Logger(MOD_ID);

    @Override
    public void onInitialize() {
        Types.ensureStaticInitialization();
        BaseRegistry.register();
        GeneratorOptions.init();
        BaseBlockEntities.register();
        BCLibEndBiomeSource.register();
        BCLibNetherBiomeSource.register();
        TagAPI.init();
        CraftingRecipes.init();
        WorldDataAPI.registerModCache(MOD_ID);
        WorldDataAPI.registerModCache(TOGETHER_WORLDS);
        DataExchangeAPI.registerMod(MOD_ID);
        BCLWorldPresets.registerPresets();
        AnvilRecipe.register();
        Conditions.registerAll();
        CommandRegistry.register();

        DataExchangeAPI.registerDescriptors(List.of(
                        HelloClient.DESCRIPTOR,
                        HelloServer.DESCRIPTOR,
                        RequestFiles.DESCRIPTOR,
                        SendFiles.DESCRIPTOR,
                        Chunker.DESCRIPTOR
                )
        );

        BCLibPatch.register();
        TemplatePiece.ensureStaticInitialization();
        PlacementModifiers.ensureStaticInitialization();
        Configs.save();
        if (isDevEnvironment()) {
            Biome.BiomeBuilder builder = new Biome.BiomeBuilder()
                    .precipitation(Biome.Precipitation.NONE)
                    .temperature(1.0f)
                    .downfall(1.0f)
                    .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                    .specialEffects(new BiomeSpecialEffects.Builder().fogColor(0xff00ff)
                                                                     .waterColor(0xff00ff)
                                                                     .waterFogColor(0xff00ff)
                                                                     .skyColor(0xff00ff)
                                                                     .build())
                    .generationSettings(new BiomeGenerationSettings.Builder().build());

            Biome biome = builder.build();
            ResourceLocation loc = makeID("testbiome");
            biome = Registry.register(BuiltinRegistries.BIOME, loc, biome);
            ResourceKey<Biome> key = BuiltinRegistries.BIOME.getResourceKey(biome).orElseThrow();
            NetherBiomeData.addNetherBiome(key, Climate.parameters(-1, 1, 0, 0, 0, 0, 0));
        }
    }

    public static boolean isDevEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public static ResourceLocation makeID(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
