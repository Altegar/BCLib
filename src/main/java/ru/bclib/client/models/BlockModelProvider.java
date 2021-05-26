package ru.bclib.client.models;

import static net.minecraft.client.resources.model.ModelBakery.MISSING_MODEL_LOCATION;

import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import ru.bclib.BCLib;

public interface BlockModelProvider extends ItemModelProvider {
	default @Nullable BlockModel getBlockModel(ResourceLocation resourceLocation, BlockState blockState) {
		Optional<String> pattern = PatternsHelper.createBlockSimple(resourceLocation);
		return ModelsHelper.fromPattern(pattern);
	}

	default UnbakedModel getModelVariant(ResourceLocation stateId, BlockState blockState, Map<ResourceLocation, UnbakedModel> modelCache) {
		ResourceLocation modelId = new ResourceLocation(stateId.getNamespace(), "block/" + stateId.getPath());
		registerBlockModel(stateId, modelId, blockState, modelCache);
		return ModelsHelper.createBlockSimple(modelId);
	}

	default void registerBlockModel(ResourceLocation stateId, ResourceLocation modelId, BlockState blockState, Map<ResourceLocation, UnbakedModel> modelCache) {
		if (!modelCache.containsKey(modelId)) {
			BlockModel model = getBlockModel(stateId, blockState);
			if (model != null) {
				model.name = modelId.toString();
				modelCache.put(modelId, model);
			} else {
				BCLib.LOGGER.warning("Error loading model: {}", modelId);
				modelCache.put(modelId, modelCache.get(MISSING_MODEL_LOCATION));
			}
		}
	}
}
