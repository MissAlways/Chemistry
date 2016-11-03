package com.missalways.chemistry.proxy;


import com.missalways.chemistry.init.ModBlocks;
import com.missalways.chemistry.init.ModItems;

/**
 * Created by MissAlways on 2.11.2016.
 */
public class ClientProxy implements CommonProxy {

    @Override
    public void init() {
        ModItems.registerRenders();
        ModBlocks.registerRenders();
    }
}
