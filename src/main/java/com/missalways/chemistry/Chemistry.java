package com.missalways.chemistry;


import com.missalways.chemistry.init.ModBlocks;
import com.missalways.chemistry.init.ModCrafting;
import com.missalways.chemistry.init.ModItems;
import com.missalways.chemistry.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by MissAlways on 2.11.2016.
 */
@Mod(modid = Reference.Mod_ID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.ACCEPTED_VERSION)
public class Chemistry {


    @Instance
    public static Chemistry instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    public static final CreativeTabs CREATIVE_TAB = new ChemistryTab();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("Pre Init");

        ModItems.init();
        ModItems.register();
        ModBlocks.init();
        ModBlocks.register();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("Init");

        proxy.init();

        ModCrafting.register();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        System.out.println("Post Init");
    }


}
