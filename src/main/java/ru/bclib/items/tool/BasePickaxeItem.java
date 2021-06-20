package ru.bclib.items.tool;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.fabricmc.fabric.impl.tool.attribute.ToolManagerImpl;
import net.fabricmc.fabric.impl.tool.attribute.ToolManagerImpl.Entry;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;
import ru.bclib.client.models.ItemModelProvider;
import ru.bclib.client.models.ModelsHelper;

public class BasePickaxeItem extends PickaxeItem implements DynamicAttributeTool, ItemModelProvider {
	public BasePickaxeItem(Tier material, int attackDamage, float attackSpeed, Properties settings) {
		super(material, attackDamage, attackSpeed, settings);
	}
	
	@Override
	public int getMiningLevel(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		if (tag.equals(FabricToolTags.PICKAXES)) {
			return getTier().getLevel();
		}
		return 0;
	}
	
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		Entry entry = ToolManagerImpl.entryNullable(state.getBlock());
		return (entry != null && entry.getMiningLevel(FabricToolTags.PICKAXES) >= 0) ? speed : super.getDestroySpeed(stack, state);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public BlockModel getItemModel(ResourceLocation resourceLocation) {
		return ModelsHelper.createHandheldItem(resourceLocation);
	}
}
