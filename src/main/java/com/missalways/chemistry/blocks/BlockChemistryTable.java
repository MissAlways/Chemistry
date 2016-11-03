package com.missalways.chemistry.blocks;

import com.missalways.chemistry.Chemistry;
import com.missalways.chemistry.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;

/**
 * Created by MissAlways on 3.11.2016.
 */
public class BlockChemistryTable extends Block {

    public BlockChemistryTable() {
        super(Material.PISTON);
        setUnlocalizedName(Reference.ChemistryBlocks.CHEMISTRYTABLE.getUnlocalizedName());
        setRegistryName(Reference.ChemistryBlocks.CHEMISTRYTABLE.getRegistryName());
        setHardness(1.0F);
        setCreativeTab(Chemistry.CREATIVE_TAB);

    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
}
