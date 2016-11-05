package com.missalways.chemistry.init;

import com.missalways.chemistry.blocks.BlockChemistryDecomposer;
import com.missalways.chemistry.blocks.BlockChemistryTable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by MissAlways on 3.11.2016.
 */
public class ModBlocks {

    public static Block chemistryTable;
    public static Block chemistryDecomposer;

    public static void init() {
        chemistryTable = new BlockChemistryTable();
        chemistryDecomposer = new BlockChemistryDecomposer();
    }

    public static void register() {
        registerBlock(chemistryTable);
        registerBlock(chemistryDecomposer);
    }

    private static void registerBlock(Block block) {
        GameRegistry.register(block);
        ItemBlock item = new ItemBlock(block);
        item.setRegistryName(block.getRegistryName());
        GameRegistry.register(item);
    }

    public static void registerRenders() {
        registerRender(chemistryTable);
        registerRender(chemistryDecomposer);
    }

    private static void registerRender(Block block) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
    }
}
