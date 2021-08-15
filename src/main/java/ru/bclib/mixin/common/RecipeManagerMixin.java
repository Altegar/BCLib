package ru.bclib.mixin.common;

import com.google.gson.JsonElement;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.bclib.recipes.BCLRecipeManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
	@Shadow
	private Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes;
	
	@Shadow
	private <C extends Container, T extends Recipe<C>> Map<ResourceLocation, Recipe<C>> byType(RecipeType<T> type) {
		return null;
	}
	
	/**
	 * @author paulevs
	 * @reason Remove conflicts with vanilla tags
	 * Change recipe order to show mod recipes first, helps when block have vanilla tag
	 * (example - mod stone with vanilla tags and furnace from that stone)
	 */
	@Overwrite
	public <C extends Container, T extends Recipe<C>> Optional<T> getRecipeFor(RecipeType<T> type, C inventory, Level world) {
		Collection<Recipe<C>> values = byType(type).values();
		List<Recipe<C>> list = new ArrayList<Recipe<C>>(values);
		list.sort((v1, v2) -> {
			boolean b1 = v1.getId().getNamespace().equals("minecraft");
			boolean b2 = v2.getId().getNamespace().equals("minecraft");
			return b1 ^ b2 ? (b1 ? 1 : -1) : 0;
		});
		
		return list.stream().flatMap((recipe) -> {
			return Util.toStream(type.tryMatch(recipe, world, inventory));
		}).findFirst();
	}
}