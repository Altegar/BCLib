package org.betterx.bclib.mixin.common;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import net.fabricmc.fabric.impl.biome.TheEndBiomeData;
import net.fabricmc.fabric.impl.biome.WeightedPicker;

import org.betterx.bclib.interfaces.TheEndBiomeDataAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(value = TheEndBiomeData.Overrides.class, remap = false)
public class TheEndBiomeDataMixin implements TheEndBiomeDataAccessor {
    @Shadow
    @Final
    @Nullable
    private Map<Holder<Biome>, WeightedPicker<Holder<Biome>>> endBiomesMap;
    @Shadow
    @Final
    @Nullable
    private Map<Holder<Biome>, WeightedPicker<Holder<Biome>>> endMidlandsMap;
    @Shadow
    @Final
    @Nullable
    private Map<Holder<Biome>, WeightedPicker<Holder<Biome>>> endBarrensMap;

    public boolean bcl_canGenerateAsEndBiome(ResourceKey<Biome> key) {
        return endBiomesMap == null ? false : endBiomesMap.containsKey(key);
    }

    public boolean bcl_canGenerateAsEndMidlandBiome(ResourceKey<Biome> key) {
        return endMidlandsMap == null ? false : endMidlandsMap.containsKey(key);
    }

    public boolean bcl_canGenerateAsEndBarrensBiome(ResourceKey<Biome> key) {
        return endBarrensMap == null ? false : endBarrensMap.containsKey(key);
    }
}
