package org.betterx.bclib.api.tag;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class CommonBlockTags {
    public static final TagKey<Block> BARREL = TagAPI.makeCommonBlockTag("barrel");
    public static final TagKey<Block> BOOKSHELVES = TagAPI.makeCommonBlockTag("bookshelves");
    public static final TagKey<Block> CHEST = TagAPI.makeCommonBlockTag("chest");
    public static final TagKey<Block> END_STONES = TagAPI.makeCommonBlockTag("end_stones");
    public static final TagKey<Block> GEN_END_STONES = END_STONES;
    public static final TagKey<Block> IMMOBILE = TagAPI.makeCommonBlockTag("immobile");
    public static final TagKey<Block> LEAVES = TagAPI.makeCommonBlockTag("leaves");
    public static final TagKey<Block> NETHERRACK = TagAPI.makeCommonBlockTag("netherrack");
    public static final TagKey<Block> NETHER_MYCELIUM = TagAPI.makeCommonBlockTag("nether_mycelium");
    public static final TagKey<Block> NETHER_PORTAL_FRAME = TagAPI.makeCommonBlockTag("nether_pframe");
    public static final TagKey<Block> NETHER_STONES = TagAPI.makeCommonBlockTag("nether_stones");
    public static final TagKey<Block> NETHER_ORES = TagAPI.makeCommonBlockTag("nether_ores");
    public static final TagKey<Block> SAPLINGS = TagAPI.makeCommonBlockTag("saplings");
    public static final TagKey<Block> SOUL_GROUND = TagAPI.makeCommonBlockTag("soul_ground");
    public static final TagKey<Block> WOODEN_BARREL = TagAPI.makeCommonBlockTag("wooden_barrels");
    public static final TagKey<Block> WOODEN_CHEST = TagAPI.makeCommonBlockTag("wooden_chests");
    public static final TagKey<Block> WORKBENCHES = TagAPI.makeCommonBlockTag("workbench");

    public static final TagKey<Block> DRAGON_IMMUNE = TagAPI.makeCommonBlockTag("dragon_immune");

    public static final TagKey<Block> MINABLE_WITH_HAMMER = TagAPI.makeCommonBlockTag("mineable/hammer");

    public static final TagKey<Block> IS_OBSIDIAN = TagAPI.makeCommonBlockTag("is_obsidian");
    public static final TagKey<Block> STALAGMITE_BASE = TagAPI.makeCommonBlockTag("stalagmite_base_blocks");

    static {
        TagAPI.BLOCKS.add(END_STONES, Blocks.END_STONE);
        TagAPI.BLOCKS.addOtherTags(NETHER_STONES, BlockTags.BASE_STONE_NETHER);

        TagAPI.BLOCKS.add(NETHERRACK,
                Blocks.NETHERRACK,
                Blocks.NETHER_QUARTZ_ORE,
                Blocks.NETHER_GOLD_ORE,
                Blocks.CRIMSON_NYLIUM,
                Blocks.WARPED_NYLIUM);

        TagAPI.BLOCKS.add(NETHER_ORES, Blocks.NETHER_QUARTZ_ORE, Blocks.NETHER_GOLD_ORE);
        TagAPI.BLOCKS.add(SOUL_GROUND, Blocks.SOUL_SAND, Blocks.SOUL_SOIL);

        TagAPI.BLOCKS.add(IS_OBSIDIAN, Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN);

        TagAPI.BLOCKS.add(STALAGMITE_BASE, Blocks.DIAMOND_BLOCK);
        TagAPI.BLOCKS.addOtherTags(STALAGMITE_BASE,
                BlockTags.DRIPSTONE_REPLACEABLE,
                BlockTags.BASE_STONE_OVERWORLD,
                NETHER_STONES,
                NETHER_ORES,
                SOUL_GROUND,
                NETHER_MYCELIUM,
                END_STONES);
    }
}
