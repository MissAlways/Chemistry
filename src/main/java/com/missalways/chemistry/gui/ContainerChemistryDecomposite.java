package com.missalways.chemistry.gui;


import com.missalways.chemistry.tileentity.TileEntityChemistryDecomposer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by MissAlways on 3.11.2016.
 */
public class ContainerChemistryDecomposite extends Container {

    private TileEntityChemistryDecomposer tileEntityChemistryDecomposer;

    private int[] cachedFields;

    private final int HOTBAR = 9;
    private final int PLAYER_INVENTORY_ROW = 3;
    private final int PLAYER_INVENTORY_COLUMN = 9;
    private final int PLAYER_INVENTORY = PLAYER_INVENTORY_COLUMN * PLAYER_INVENTORY_ROW;
    private final int VANILLA_SLOTS = HOTBAR + PLAYER_INVENTORY;

    private final int BOTTLE_SLOTS = 1;
    private final int INPUT_SLOTS = 1;
    private final int OUTPUT_SLOTS = 27;
    private final int DECOMPOSER_SLOTS = BOTTLE_SLOTS + INPUT_SLOTS + OUTPUT_SLOTS;

    // 0 - 35 for invPlayer then 36 - 64 for tileInventoryFurnace
    private final int VANILLA_FIRST_INDEX = 0;
    private final int FIRST_BOTTLE_INDEX = VANILLA_FIRST_INDEX + VANILLA_SLOTS;
    private final int FIRST_INPUT_INDEX = FIRST_BOTTLE_INDEX + BOTTLE_SLOTS;
    private final int FIRST_OUTPUT_INDEX = FIRST_INPUT_INDEX + INPUT_SLOTS;

    private final int FIRST_BOTLLE_SLOT = 0;
    private final int FIRST_INPUT_SLOT = FIRST_BOTLLE_SLOT + BOTTLE_SLOTS;
    private final int FIRST_OUTPUT_SLOT = FIRST_INPUT_SLOT + INPUT_SLOTS;


    public ContainerChemistryDecomposite(InventoryPlayer player, TileEntityChemistryDecomposer tileEntityChemistryDecomposer) {
        this.tileEntityChemistryDecomposer = tileEntityChemistryDecomposer;

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int HOTBAR_X_POS = 7;
        final int HOTBAR_Y_POS = 209;

        //Hotbar
        for (int x = 0; x < HOTBAR; x++) {
            int slotNumber = x;
            addSlotToContainer(new Slot(player, slotNumber, HOTBAR_X_POS + SLOT_X_SPACING * x, HOTBAR_Y_POS));
        }
        final int PLAYER_INVENTORY_X_POS = 7;
        final int PLAYER_INVENTORY_Y_POS = 151;
        //Player inventory
        for (int y = 0; y < PLAYER_INVENTORY_ROW; y++) {
            for (int x = 0; x < PLAYER_INVENTORY_COLUMN; x++) {
                int slotNumber = HOTBAR + y * PLAYER_INVENTORY_COLUMN + x;
                int xpos = PLAYER_INVENTORY_X_POS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INVENTORY_Y_POS + y * SLOT_Y_SPACING;
                addSlotToContainer(new Slot(player, slotNumber, xpos, ypos));
            }
        }

        final int BOTTLE_SLOTS_X_POS = 55;
        final int BOTTLE_SLOTS_Y_POS = 52;
        //Fuel slot
        addSlotToContainer(new SlotBottle(tileEntityChemistryDecomposer, FIRST_BOTLLE_SLOT, BOTTLE_SLOTS_X_POS, BOTTLE_SLOTS_Y_POS));


        final int INPUT_SLOTS_X_POS = 55;
        final int INPUT_SLOTS_Y_POS = 16;
        //Input slot

        addSlotToContainer(new SlotDecompositableInput(tileEntityChemistryDecomposer, FIRST_INPUT_SLOT, INPUT_SLOTS_X_POS, INPUT_SLOTS_Y_POS));

        final int OUTPUT_SLOTS_X_POS = 7;
        final int OUTPUT_SLOTS_Y_POS = 83;
        //Output slots
        for (int y = 0; y < PLAYER_INVENTORY_ROW; y++) {
            for (int x = 0; x < PLAYER_INVENTORY_COLUMN; x++) {
                int slotNumber = FIRST_OUTPUT_SLOT + y * FIRST_OUTPUT_SLOT + x;
                int xpos = OUTPUT_SLOTS_X_POS + x * SLOT_X_SPACING;
                int ypos = OUTPUT_SLOTS_Y_POS + y * SLOT_Y_SPACING;
                addSlotToContainer(new SlotOutput(tileEntityChemistryDecomposer, slotNumber, xpos, ypos));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntityChemistryDecomposer.isUseableByPlayer(player);
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        Slot sourceSlot = (Slot) inventorySlots.get(index);
        if (sourceSlot == null || !sourceSlot.getHasStack()) {
            return null;
        }
        ItemStack sourceStack = sourceSlot.getStack();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index >= VANILLA_FIRST_INDEX && index < VANILLA_FIRST_INDEX + VANILLA_SLOTS) {
            if (TileEntityChemistryDecomposer.getDecompositeResultForItem(sourceStack) != null) {
                if (!mergeItemStack(sourceStack, FIRST_INPUT_INDEX, FIRST_INPUT_INDEX + INPUT_SLOTS, false)) {
                    return null;
                }
            } else if (TileEntityChemistryDecomposer.getItemDecompositeTime(sourceStack) > 0) {
                if (!mergeItemStack(sourceStack, FIRST_BOTTLE_INDEX, FIRST_BOTTLE_INDEX + BOTTLE_SLOTS, true)) {
                    return null;
                }
            } else {
                return null;
            }
        } else if (index >= FIRST_BOTLLE_SLOT && index < FIRST_BOTTLE_INDEX + DECOMPOSER_SLOTS) {
            if (!mergeItemStack(sourceStack, VANILLA_FIRST_INDEX, VANILLA_FIRST_INDEX + VANILLA_SLOTS, false)) {
                return null;
            }
        } else {
            System.err.print("Invalid slotIndex:" + index);
            return null;
        }

        if (sourceStack.stackSize == 0) {
            sourceSlot.putStack(null);
        } else {
            sourceSlot.onSlotChanged();
        }

        sourceSlot.onPickupFromSlot(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        boolean allFieldsHaveChanged = false;
        boolean fieldHasChanged[] = new boolean[this.tileEntityChemistryDecomposer.getFieldCount()];
        if (cachedFields == null) {
            cachedFields = new int[tileEntityChemistryDecomposer.getFieldCount()];
            allFieldsHaveChanged = true;
        }
        for (int i = 0; i < cachedFields.length; ++i) {
            if (allFieldsHaveChanged || cachedFields[i] != this.tileEntityChemistryDecomposer.getField(i)) {
                cachedFields[i] = this.tileEntityChemistryDecomposer.getField(i);
                fieldHasChanged[i] = true;
            }
        }
        for (IContainerListener listener : this.listeners) {
            for (int fieldID = 0; fieldID < this.tileEntityChemistryDecomposer.getFieldCount(); ++fieldID) {
                if (fieldHasChanged[fieldID]) {
                    listener.sendProgressBarUpdate(this, fieldID, cachedFields[fieldID]);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        tileEntityChemistryDecomposer.setField(id, data);
    }

    public class SlotBottle extends Slot {

        public SlotBottle(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            String name = stack.getItem().getUnlocalizedName();
            System.out.println(name);
            if(name.equalsIgnoreCase("item.glassBottle")){
                return TileEntityChemistryDecomposer.isItemValidForBottleSlot(stack);
            }
            else{
                return false;
            }


        }
    }

    public class SlotDecompositableInput extends Slot {
        public SlotDecompositableInput(IInventory inventory, int index, int xPosition, int yPosition) {
            super(inventory, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return tileEntityChemistryDecomposer.isItemValidForInputSlot(stack);
        }
    }

    public class SlotOutput extends Slot {

        public SlotOutput(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return tileEntityChemistryDecomposer.isItemValidForOutputSlot(stack);
        }
    }


}
