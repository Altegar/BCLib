package org.betterx.bclib.world.generator;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

import com.google.common.collect.Sets;
import org.betterx.bclib.api.biomes.BiomeAPI;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class BCLBiomeSource extends BiomeSource {
    public static int BIOME_SOURCE_VERSION_NONE = -1;
    public static int BIOME_SOURCE_VERSION_VANILLA = 0;
    public static int BIOME_SOURCE_VERSION_SQUARE = 17;
    public static int BIOME_SOURCE_VERSION_HEX = 18;
    public static int DEFAULT_BIOME_SOURCE_VERSION = BIOME_SOURCE_VERSION_HEX;
    protected final Registry<Biome> biomeRegistry;
    protected long currentSeed;

    public final int biomeSourceVersion;

    private static List<Holder<Biome>> preInit(Registry<Biome> biomeRegistry, List<Holder<Biome>> biomes) {
        biomes = biomes.stream().sorted(Comparator.comparing(holder -> holder.unwrapKey()
                                                                             .get()
                                                                             .location()
                                                                             .toString()))
                       .toList();
        biomes.forEach(biome -> BiomeAPI.sortBiomeFeatures(biome));
        return biomes;
    }

    protected BCLBiomeSource(Registry<Biome> biomeRegistry,
                             List<Holder<Biome>> list,
                             long seed,
                             Optional<Integer> biomeSourceVersion) {
        super(preInit(biomeRegistry, list));

        this.biomeRegistry = biomeRegistry;
        this.biomeSourceVersion = biomeSourceVersion.orElse(DEFAULT_BIOME_SOURCE_VERSION);
        this.currentSeed = seed;

        System.out.println(this + " with Registry: " + biomeRegistry.getClass().getName() + "@" + Integer.toHexString(
                biomeRegistry.hashCode()));

        //BiomeAPI.initRegistry(biomeRegistry);
    }

    final public void setSeed(long seed) {
        if (seed != currentSeed) {
            System.out.println(this + " set Seed: " + seed);
            this.currentSeed = seed;
            initMap(seed);
        }
    }

    protected final void initMap(long seed) {
        System.out.println(this + " updates Map");
        onInitMap(seed);
    }

    protected abstract void onInitMap(long newSeed);

    public static int getVersionBiomeSource(BiomeSource biomeSource) {
        if (biomeSource == null) return BCLBiomeSource.BIOME_SOURCE_VERSION_NONE;

        if (biomeSource instanceof BCLBiomeSource bcl) {
            return bcl.biomeSourceVersion;
        } else {
            return BCLBiomeSource.BIOME_SOURCE_VERSION_VANILLA;
        }
    }

    public BCLBiomeSource createCopyForDatapack(Set<Holder<Biome>> datapackBiomes) {
        Set<Holder<Biome>> mutableSet = Sets.newHashSet();
        mutableSet.addAll(datapackBiomes);
        return cloneForDatapack(mutableSet);
    }

    protected abstract BCLBiomeSource cloneForDatapack(Set<Holder<Biome>> datapackBiomes);

    public interface ValidBiomePredicate {
        boolean isValid(Holder<Biome> biome, ResourceLocation location);
    }

    protected static List<Holder<Biome>> getBiomes(Registry<Biome> biomeRegistry,
                                                   List<String> exclude,
                                                   List<String> include,
                                                   BCLibNetherBiomeSource.ValidBiomePredicate test) {
        return biomeRegistry.stream()
                            .filter(biome -> biomeRegistry.getResourceKey(biome).isPresent())

                            .map(biome -> biomeRegistry.getOrCreateHolderOrThrow(biomeRegistry.getResourceKey(biome)
                                                                                              .get()))
                            .filter(biome -> {
                                ResourceLocation location = biome.unwrapKey().orElseThrow().location();
                                final String strLocation = location.toString();
                                if (exclude.contains(strLocation)) return false;
                                if (include.contains(strLocation)) return true;

                                return test.isValid(biome, location);
                            })
                            .toList();
    }
}
