package com.missalways.chemistry.gui;

import com.missalways.chemistry.tileentity.TileEntityChemistryDecomposer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Created by MissAlways on 4.11.2016.
 */
public class ChemistryDecomposerHandler implements IGuiHandler{

    public static final int CHEMISTRY_DECOMPOSER_GUI = 35;

    public static int getGUIID(){
        return CHEMISTRY_DECOMPOSER_GUI;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID != getGUIID()){
            System.err.println("Invalid ID: expected " + getGUIID() + ", recieved " + ID);
        }

        BlockPos xyz = new BlockPos(x,y,z);
        TileEntity tileEntity = world.getTileEntity(xyz);
        if(tileEntity instanceof TileEntityChemistryDecomposer){
            TileEntityChemistryDecomposer tileEntityChemistryDecomposer = (TileEntityChemistryDecomposer) tileEntity;
            return new ContainerChemistryDecomposite(player.inventory, tileEntityChemistryDecomposer);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID != CHEMISTRY_DECOMPOSER_GUI){
            System.err.println("Invalid ID: expected " + getGUIID() + ", recieved " + ID);
        }

        BlockPos xyz = new BlockPos(x,y,z);
        TileEntity tileEntity = world.getTileEntity(xyz);
        if(tileEntity instanceof TileEntityChemistryDecomposer){
            TileEntityChemistryDecomposer tileEntityChemistryDecomposer = (TileEntityChemistryDecomposer) tileEntity;
            return new GuiChemistryDecomposite(player.inventory, tileEntityChemistryDecomposer);
        }
        return null;
    }
}
