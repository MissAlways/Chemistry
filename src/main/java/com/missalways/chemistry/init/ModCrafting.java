package com.missalways.chemistry.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by MissAlways on 3.11.2016.
 */
public class ModCrafting {
    public static void register() {
        GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.chemistryTable), "GBG", "WLW", "WAW", 'G', Blocks.GLASS, 'B', Items.BOOK, 'W', Blocks.LOG, 'A', Items.GOLD_INGOT, 'L', Items.BLAZE_ROD);
    }
}
