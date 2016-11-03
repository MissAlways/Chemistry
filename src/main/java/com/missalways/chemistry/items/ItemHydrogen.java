package com.missalways.chemistry.items;

import com.missalways.chemistry.Chemistry;
import com.missalways.chemistry.Reference;
import net.minecraft.item.Item;

/**
 * Created by MissAlways on 2.11.2016.
 */
public class ItemHydrogen extends Item {

    public ItemHydrogen() {
        setUnlocalizedName(Reference.ChemistryItems.HYDROGEN.getUnlocalizedName());
        setRegistryName(Reference.ChemistryItems.HYDROGEN.getRegistryName());
        setCreativeTab(Chemistry.CREATIVE_TAB);
    }
}
