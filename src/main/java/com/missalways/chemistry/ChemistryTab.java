package com.missalways.chemistry;

import com.missalways.chemistry.init.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * Created by MissAlways on 3.11.2016.
 */
public class ChemistryTab extends CreativeTabs {


    public ChemistryTab() {
        super("tabChemistry");
    }

    @Override
    public Item getTabIconItem() {
        return ModItems.helium;
    }
}
