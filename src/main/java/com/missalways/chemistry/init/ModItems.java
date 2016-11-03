package com.missalways.chemistry.init;

import com.missalways.chemistry.items.ItemHelium;
import com.missalways.chemistry.items.ItemHydrogen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by MissAlways on 2.11.2016.
 */
public class ModItems {

    public static Item hydrogen;
    public static Item helium;

    public static void init(){
        hydrogen = new ItemHydrogen();
        helium = new ItemHelium();
    }

    public static void register(){
        GameRegistry.register(hydrogen);
        GameRegistry.register(helium);
    }

    public static void registerRenders(){
        registerRender(hydrogen);
        registerRender(helium);
    }

    private static void registerRender(Item item){
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
