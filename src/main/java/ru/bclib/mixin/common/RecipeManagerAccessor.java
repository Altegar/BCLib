package ru.bclib.mixin.common;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {
	@Accessor("recipes")
	Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> bcl_getRecipes();

	@Accessor("recipes")
	void bcl_setRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes);
}