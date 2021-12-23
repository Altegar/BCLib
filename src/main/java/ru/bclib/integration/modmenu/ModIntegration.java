package ru.bclib.integration.modmenu;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.Named;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import ru.bclib.BCLib;
import ru.bclib.world.features.BCLFeature;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class ModIntegration {
	private final String modID;
	
	public void init() {}
	
	public ModIntegration(String modID) {
		this.modID = modID;
	}
	
	public ResourceLocation getID(String name) {
		return new ResourceLocation(modID, name);
	}
	
	public Block getBlock(String name) {
		return Registry.BLOCK.get(getID(name));
	}
	
	public Item getItem(String name) {
		return Registry.ITEM.get(getID(name));
	}
	
	public BlockState getDefaultState(String name) {
		return getBlock(name).defaultBlockState();
	}
	
	public ResourceKey<Biome> getKey(String name) {
		return ResourceKey.create(Registry.BIOME_REGISTRY, getID(name));
	}
	
	public boolean modIsInstalled() {
		return FabricLoader.getInstance().isModLoaded(modID);
	}
	
	public BCLFeature getFeature(String featureID, String placedFeatureID, GenerationStep.Decoration featureStep) {
		ResourceLocation id = getID(featureID);
		Feature<?> feature = Registry.FEATURE.get(id);
		PlacedFeature featureConfigured = BuiltinRegistries.PLACED_FEATURE.get(getID(placedFeatureID));
		return new BCLFeature(id, feature, featureStep, featureConfigured);
	}
	
	public BCLFeature getFeature(String name, GenerationStep.Decoration featureStep) {
		return getFeature(name, name, featureStep);
	}
	
	public ConfiguredFeature<?, ?> getConfiguredFeature(String name) {
		return BuiltinRegistries.CONFIGURED_FEATURE.get(getID(name));
	}
	
	public Biome getBiome(String name) {
		return BuiltinRegistries.BIOME.get(getID(name));
	}
	
	public Class<?> getClass(String path) {
		Class<?> cl = null;
		try {
			cl = Class.forName(path);
		}
		catch (ClassNotFoundException e) {
			BCLib.LOGGER.error(e.getMessage());
			if (BCLib.isDevEnvironment()) {
				e.printStackTrace();
			}
		}
		return cl;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T getStaticFieldValue(Class<?> cl, String name) {
		if (cl != null) {
			try {
				Field field = cl.getDeclaredField(name);
				if (field != null) {
					return (T) field.get(null);
				}
			}
			catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public Object getFieldValue(Class<?> cl, String name, Object classInstance) {
		if (cl != null) {
			try {
				Field field = cl.getDeclaredField(name);
				if (field != null) {
					return field.get(classInstance);
				}
			}
			catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public Method getMethod(Class<?> cl, String functionName, Class<?>... args) {
		if (cl != null) {
			try {
				return cl.getMethod(functionName, args);
			}
			catch (NoSuchMethodException | SecurityException e) {
				BCLib.LOGGER.error(e.getMessage());
				if (BCLib.isDevEnvironment()) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public Object executeMethod(Object instance, Method method, Object... args) {
		if (method != null) {
			try {
				return method.invoke(instance, args);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				BCLib.LOGGER.error(e.getMessage());
				if (BCLib.isDevEnvironment()) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public Object getAndExecuteStatic(Class<?> cl, String functionName, Object... args) {
		if (cl != null) {
			Class<?>[] classes = new Class<?>[args.length];
			for (int i = 0; i < args.length; i++) {
				classes[i] = args[i].getClass();
			}
			Method method = getMethod(cl, functionName, classes);
			return executeMethod(null, method, args);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T getAndExecuteRuntime(Class<?> cl, Object instance, String functionName, Object... args) {
		if (instance != null) {
			Class<?>[] classes = new Class<?>[args.length];
			for (int i = 0; i < args.length; i++) {
				classes[i] = args[i].getClass();
			}
			Method method = getMethod(cl, functionName, classes);
			return (T) executeMethod(instance, method, args);
		}
		return null;
	}
	
	public Object newInstance(Class<?> cl, Object... args) {
		if (cl != null) {
			for (Constructor<?> constructor : cl.getConstructors()) {
				if (constructor.getParameterCount() == args.length) {
					try {
						return constructor.newInstance(args);
					}
					catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						BCLib.LOGGER.error(e.getMessage());
						if (BCLib.isDevEnvironment()) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return null;
	}
	
	public Tag.Named<Item> getItemTag(String name) {
		ResourceLocation id = getID(name);
		Tag<Item> tag = ItemTags.getAllTags().getTag(id);

		//return tag == null ? (Named<Item>) TagRegistry.item(id) : (Named<Item>) tag;
		return tag == null ? (Named<Item>) TagFactory.ITEM.create(id) : (Named<Item>) tag;
	}
	
	public Tag.Named<Block> getBlockTag(String name) {
		ResourceLocation id = getID(name);
		Tag<Block> tag = BlockTags.getAllTags().getTag(id);
		//return tag == null ? (Named<Block>) TagRegistry.block(id) : (Named<Block>) tag;
		return tag == null ? (Named<Block>) TagFactory.BLOCK.create(id) : (Named<Block>) tag;
	}
}
