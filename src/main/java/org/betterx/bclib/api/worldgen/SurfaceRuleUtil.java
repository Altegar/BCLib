package org.betterx.bclib.api.worldgen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import org.betterx.bclib.api.biomes.BiomeAPI;
import org.betterx.bclib.mixin.common.NoiseGeneratorSettingsMixin;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SurfaceRuleUtil {
	private static final Map<ResourceLocation, RuleSource> SURFACE_RULES = Maps.newHashMap();
	
	private static List<RuleSource> getRuleSourcesForBiomes(Set<Holder<Biome>> biomes) {
		Set<ResourceLocation> biomeIDs = biomes
				.stream()
				.map(biome -> BiomeAPI.getBiomeID(biome))
				.collect(Collectors.toSet());
		return getRuleSourcesFromIDs(biomeIDs);
	}
	
	/**
	 * Creates a list of SurfaceRules for all Biomes that are managed by the passed {@link BiomeSource}.
	 * If we have Surface rules for any of the Biomes from the given set of {@link BiomeSource}, they
	 * will be added to the result
	 * <p>
	 * Note: This Method is used in the {@link NoiseGeneratorSettingsMixin} which in turn
	 * is called from {@link BiomeAPI#applyModifications(ServerLevel)}.
	 *
	 * @param sources The Set of {@link BiomeSource} we want to consider
	 * @return A list of {@link RuleSource}-Objects that are needed to create those Biomes
	 */
	public static List<RuleSource> getRuleSources(Set<BiomeSource> sources) {
		final Set<Holder<Biome>> biomes = new HashSet<>();
		for (BiomeSource s : sources) {
			biomes.addAll(s.possibleBiomes());
		}

		return getRuleSourcesForBiomes(biomes);
	}
	
	public static List<RuleSource> getRuleSources(BiomeSource biomeSource) {
		return getRuleSourcesForBiomes(Sets.newHashSet(biomeSource.possibleBiomes()));
	}
	
	private static List<RuleSource> getRuleSourcesFromIDs(Set<ResourceLocation> biomeIDs) {
		List<RuleSource> rules = Lists.newArrayList();
		SURFACE_RULES.forEach((biomeID, rule) -> {
			if (biomeIDs.contains(biomeID)) {
				rules.add(rule);
			}
		});
		return rules;
	}
	
	/**
	 * Adds surface rule to specified biome.
	 *
	 * @param biomeID biome {@link ResourceLocation}.
	 * @param source  {@link RuleSource}.
	 */
	public static void addSurfaceRule(ResourceLocation biomeID, RuleSource source) {
		SURFACE_RULES.put(biomeID, source);
		//NOISE_GENERATOR_SETTINGS.forEach(BiomeAPI::changeSurfaceRulesForGenerator);
	}
	
	public static RuleSource addRulesForBiomeSource(RuleSource org, BiomeSource biomeSource) {
		List<RuleSource> additionalRules = getRuleSources(biomeSource);
		if (org instanceof SurfaceRules.SequenceRuleSource sequenceRule) {
			List<RuleSource> existingSequence = sequenceRule.sequence();
			additionalRules = additionalRules.stream().filter(r -> existingSequence.indexOf(r) < 0).collect(Collectors.toList());
			additionalRules.addAll(sequenceRule.sequence());
		} else {
			additionalRules.add(org);
		}
		
		return SurfaceRules.sequence(additionalRules.toArray(new RuleSource[additionalRules.size()]));
	}
}
