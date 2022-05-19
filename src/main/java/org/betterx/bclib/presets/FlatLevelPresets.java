package org.betterx.bclib.presets;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FlatLevelGeneratorPresetTags;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;

import org.betterx.bclib.api.tag.TagAPI;
import org.betterx.bclib.api.tag.TagType;

public class FlatLevelPresets {
    public static TagType.Simple<FlatLevelGeneratorPreset> FLAT_LEVEL_PRESETS =
            TagAPI.registerType(Registry.FLAT_LEVEL_GENERATOR_PRESET_REGISTRY,
                                "tags/worldgen/flat_level_generator_preset",
                                (b) -> null);


    public static ResourceKey<FlatLevelGeneratorPreset> register(ResourceLocation loc) {
        ResourceKey<FlatLevelGeneratorPreset> key = ResourceKey.create(Registry.FLAT_LEVEL_GENERATOR_PRESET_REGISTRY,
                                                                       loc);
        FLAT_LEVEL_PRESETS.addUntyped(FlatLevelGeneratorPresetTags.VISIBLE, key.location());
        return key;
    }
}
