package com.missalways.chemistry.blocks;

import com.missalways.chemistry.Chemistry;
import com.missalways.chemistry.Reference;
import com.missalways.chemistry.gui.ChemistryDecomposerHandler;
import com.missalways.chemistry.gui.GuiChemistryDecomposite;
import com.missalways.chemistry.tileentity.TileEntityChemistryDecomposer;
import com.missalways.chemistry.tileentity.TileEntityChemistryTable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by MissAlways on 3.11.2016.
 */
public class BlockChemistryDecomposer extends BlockContainer{

    public BlockChemistryDecomposer() {
        super(Material.ROCK);
        setUnlocalizedName(Reference.ChemistryBlocks.CHEMISTRYDECOMPOSER.getUnlocalizedName());
        setRegistryName(Reference.ChemistryBlocks.CHEMISTRYDECOMPOSER.getRegistryName());
        setHardness(1.0F);
        setCreativeTab(Chemistry.CREATIVE_TAB);
    }

    //Called when block is placed of loaded
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityChemistryDecomposer();
    }

    //Called when block is right clicked
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand,@Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(worldIn.isRemote){
            return true;
        }
        playerIn.openGui(Reference.MOD_ID, ChemistryDecomposerHandler.getGUIID(),  worldIn, pos.getX(), pos.getY(), pos.getZ());
        return  true;
    }

    //Drops inventory when block is broken
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof IInventory){
            InventoryHelper.dropInventoryItems(worldIn,pos, (IInventory) tileEntity);
        }

        super.breakBlock(worldIn, pos, state);
    }

    // The block will render SOLID layer
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
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
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityChemistryDecomposer();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
