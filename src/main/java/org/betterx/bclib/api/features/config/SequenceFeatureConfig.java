package org.betterx.bclib.api.features.config;

import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.bclib.api.features.BCLFeature;

import java.util.List;

public class SequenceFeatureConfig implements FeatureConfiguration {
    public static final Codec<SequenceFeatureConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ExtraCodecs.nonEmptyList(PlacedFeature.CODEC.listOf())
                               .fieldOf("features")
                               .forGetter(a -> a.features)
            ).apply(instance, SequenceFeatureConfig::new)
    );

    private final List<Holder<PlacedFeature>> features;

    public static SequenceFeatureConfig create(List<BCLFeature> features) {
        return new SequenceFeatureConfig(features.stream().map(f -> f.getPlacedFeature()).toList());
    }

    public SequenceFeatureConfig(List<Holder<PlacedFeature>> features) {
        this.features = features;
    }

    public boolean placeAll(FeaturePlaceContext<SequenceFeatureConfig> ctx) {
        boolean placed = false;
        for (Holder<PlacedFeature> f : features) {
            placed |= f.value().place(ctx.level(), ctx.chunkGenerator(), ctx.random(), ctx.origin());
        }
        return placed;

    }
}
