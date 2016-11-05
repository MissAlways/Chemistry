package com.missalways.chemistry;

/**
 * Created by MissAlways on 2.11.2016.
 */
public class Reference {
    public static final String MOD_ID = "chemistry";
    public static final String NAME = "Chemistry";
    public static final String VERSION = "1.0";
    public static final String ACCEPTED_VERSION = "[1.10.2]";

    public static final String CLIENT_PROXY_CLASS = "com.missalways.chemistry.proxy.ClientProxy";
    public static final String SERVER_PROXY_CLASS = "com.missalways.chemistry.proxy.ServerProxy";

    public static enum ChemistryItems {
        HYDROGEN("hydrogen", "ItemHydrogen"),
        HELIUM("helium", "ItemHelium");

        private String unlocalizedName;
        private String registryName;

        ChemistryItems(String unlocalizedName, String registryName) {
            this.unlocalizedName = unlocalizedName;
            this.registryName = registryName;
        }

        public String getUnlocalizedName() {
            return unlocalizedName;
        }

        public String getRegistryName() {
            return registryName;
        }

    }

    public static enum ChemistryBlocks {
        CHEMISTRYTABLE("chemistryTable", "BlockChemistryTable"),
        CHEMISTRYDECOMPOSER("chemistryDecomposer","BlockChemistryDecomposer");

        private String unlocalizedName;
        private String registryName;

        ChemistryBlocks(String unlocalizedName, String registryName) {
            this.unlocalizedName = unlocalizedName;
            this.registryName = registryName;
        }

        public String getUnlocalizedName() {
            return unlocalizedName;
        }

        public String getRegistryName() {
            return registryName;
        }

    }

}
