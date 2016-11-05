package com.missalways.chemistry.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumSkyBlock;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Created by MissAlways on 3.11.2016.
 */
public class TileEntityChemistryDecomposer extends TileEntity implements IInventory, ITickable {

    public static final int BOTTLE_SLOTS = 1;
    public static final int INPUT_SLOTS = 1;
    public static final int OUTPUT_SLOTS = 27;
    public static final int INVENTORY_SLOTS = 36;
    public static final int TOTAL_SLOTS = BOTTLE_SLOTS + INPUT_SLOTS + OUTPUT_SLOTS + INVENTORY_SLOTS;

    public static final int FIRST_BOTTLE_SLOT = 0;
    public static final int FIRST_INPUT_SLOT = FIRST_BOTTLE_SLOT + BOTTLE_SLOTS;
    public static final int FIRST_OUTPUT_SLOT = FIRST_INPUT_SLOT + INPUT_SLOTS;

    private ItemStack[] itemStacks = new ItemStack[TOTAL_SLOTS];

    private int[] usingTimeRemaining = new int[BOTTLE_SLOTS];
    private int[] usingTimeInitialValue = new int[BOTTLE_SLOTS];

    private short decompositeTime;
    private static final short DECOMPOSITE_TIME_FOR_COMPLETION = 200;
    private int cachedNumberOfUsingSlots = -1;

    // Returns amount of bottle remaining on currently using item in the given bottle slot
    public double fractionOfBottleRemaining(int bottleSlot) {
        if (usingTimeInitialValue[bottleSlot] <= 0) {
            return 0;
        }
        double fraction = usingTimeRemaining[bottleSlot] / (double) usingTimeInitialValue[bottleSlot];
        return MathHelper.clamp_double(fraction, 0.0, 1.0);
    }

    // Get the number of slots which have bottles using in them.
    public int numberOfUsingBottleSlots() {
        int usingCount = 0;
        for (int usingTime : usingTimeRemaining) {
            if (usingTime > 0) {
                ++usingCount;
            }
        }
        return usingCount;
    }

    // Returns the amount of decomposite time completed on the currently decomposite item.
    public double fractionOfDecompositeTimeComplete() {
        double fraction = decompositeTime / (double) DECOMPOSITE_TIME_FOR_COMPLETION;
        return MathHelper.clamp_double(fraction, 0.0, 1.0);
    }

    @Override
    public void update() {
        if (canDecomposite()) {
            int numberOfBottleusing = useBottle();

            // If bottle is available, keep decomposite the item, otherwise start "undecomposite" it at double speed
            if (numberOfBottleusing > 0) {
                decompositeTime += numberOfBottleusing;
            } else {
                decompositeTime -= 2;
            }

            if (decompositeTime < 0) {
                decompositeTime = 0;
            }

            // If decompositeTime has reached maxDecompositeTime decomposite the item and reset decompositeTime
            if (decompositeTime >= DECOMPOSITE_TIME_FOR_COMPLETION) {
                decompositeItem();
                decompositeTime = 0;
            }
        } else {
            decompositeTime = 0;
        }


        int numberUsing = numberOfUsingBottleSlots();
        if (cachedNumberOfUsingSlots != numberUsing) {
            cachedNumberOfUsingSlots = numberUsing;
            if (worldObj.isRemote) {
                IBlockState iblockstate = this.worldObj.getBlockState(pos);
                final int FLAGS = 3;
                worldObj.notifyBlockUpdate(pos, iblockstate, iblockstate, FLAGS);
            }
            worldObj.checkLightFor(EnumSkyBlock.BLOCK, pos);
        }
    }

    private int useBottle() {
        int usingCount = 0;
        boolean inventoryChanged = false;
        for (int i = 0; i < BOTTLE_SLOTS; i++) {
            int bottleSlotNumber = i + FIRST_BOTTLE_SLOT;
            if (usingTimeRemaining[i] > 0) {
                --usingTimeRemaining[i];
                ++usingCount;
            }
            if (usingTimeRemaining[i] == 0) {
                if (itemStacks[bottleSlotNumber] != null && getItemDecompositeTime(itemStacks[bottleSlotNumber]) > 0) {
                    usingTimeRemaining[i] = usingTimeInitialValue[i] = getItemDecompositeTime(itemStacks[bottleSlotNumber]);
                    --itemStacks[bottleSlotNumber].stackSize;
                    ++usingCount;
                    inventoryChanged = true;
                    if (itemStacks[bottleSlotNumber].stackSize == 0) {
                        itemStacks[bottleSlotNumber] = itemStacks[bottleSlotNumber].getItem().getContainerItem(itemStacks[bottleSlotNumber]);
                    }
                }
            }
        }
        if (inventoryChanged) markDirty();
        return usingCount;
    }

    private boolean canDecomposite() {
        return decompositeItem(false);
    }

    private void decompositeItem() {
        decompositeItem(true);
    }

    private boolean decompositeItem(boolean performDecomposite) {
        Integer firstSuitableInputSlot = null;
        Integer firstSuitableOutputSlot = null;
        ItemStack result = null;

        for (int inputSlot = FIRST_INPUT_SLOT; inputSlot < FIRST_INPUT_SLOT + INPUT_SLOTS; inputSlot++) {
            if (itemStacks[inputSlot] != null) {
                result = getDecompositeResultForItem(itemStacks[inputSlot]);
                if (result != null) {
                    // find the first suitable output slot- either empty, or with identical item that has enough space
                    for (int outputSlot = FIRST_OUTPUT_SLOT; outputSlot < FIRST_OUTPUT_SLOT + OUTPUT_SLOTS; outputSlot++) {
                        ItemStack outputStack = itemStacks[outputSlot];
                        if (outputStack == null) {
                            firstSuitableInputSlot = inputSlot;
                            firstSuitableOutputSlot = outputSlot;
                            break;
                        }

                        if (outputStack.getItem() == result.getItem() && (!outputStack.getHasSubtypes() || outputStack.getMetadata() == outputStack.getMetadata())
                                && ItemStack.areItemStackTagsEqual(outputStack, result)) {
                            int combinedSize = itemStacks[outputSlot].stackSize + result.stackSize;
                            if (combinedSize <= getInventoryStackLimit() && combinedSize <= itemStacks[outputSlot].getMaxStackSize()) {
                                firstSuitableInputSlot = inputSlot;
                                firstSuitableOutputSlot = outputSlot;
                                break;
                            }
                        }
                    }
                    if (firstSuitableInputSlot != null) break;
                }
            }
        }

        if (firstSuitableInputSlot == null) return false;
        if (!performDecomposite) return true;

        // alter input and output
        itemStacks[firstSuitableInputSlot].stackSize--;
        if (itemStacks[firstSuitableInputSlot].stackSize <= 0) itemStacks[firstSuitableInputSlot] = null;
        if (itemStacks[firstSuitableOutputSlot] == null) {
            itemStacks[firstSuitableOutputSlot] = result.copy(); // Use deep .copy() to avoid altering the recipe
        } else {
            itemStacks[firstSuitableOutputSlot].stackSize += result.stackSize;
        }
        markDirty();
        return true;
    }

    public static ItemStack getDecompositeResultForItem(ItemStack itemStack) {
        //TODO
        return FurnaceRecipes.instance().getSmeltingResult(itemStack);
    }

    public static short getItemDecompositeTime(ItemStack itemStack) {
        //TODO
        int decompositeTime = TileEntityFurnace.getItemBurnTime(itemStack);
        return (short) MathHelper.clamp_int(decompositeTime, 0, Short.MAX_VALUE);
    }

    @Override
    public int getSizeInventory() {
        return itemStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return itemStacks[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemStackInSlot = getStackInSlot(index);
        if (itemStackInSlot == null) {
            return null;
        }
        ItemStack itemStackRemoved;
        if (itemStackInSlot.stackSize <= count) {
            itemStackRemoved = itemStackInSlot;
            setInventorySlotContents(index, null);
        } else {
            itemStackRemoved = itemStackInSlot.splitStack(count);
            if (itemStackInSlot.stackSize == 0) {
                setInventorySlotContents(index, null);
            }
        }
        markDirty();
        return itemStackRemoved;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        itemStacks[index] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        if (this.worldObj.getTileEntity(this.pos) != this) {
            return false;
        }
        final double x = 0.5;
        final double y = 0.5;
        final double z = 0.5;
        final double maxDistanceSq = 8.0 * 8.0;
        return player.getDistanceSq(pos.getX() + x, pos.getY() + y, pos.getZ() + z) < maxDistanceSq;
    }

    public static boolean isItemValidForBottleSlot(ItemStack itemStack) {
        return true;
    }

    public boolean isItemValidForInputSlot(ItemStack stack) {
        return true;
    }

    public boolean isItemValidForOutputSlot(ItemStack stack) {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagList dataForAllSlots = new NBTTagList();
        for (int i = 0; i < this.itemStacks.length; ++i) {
            if (this.itemStacks[i] != null) {
                NBTTagCompound dataForSlot = new NBTTagCompound();
                dataForSlot.setByte("Slot", (byte) i);
                this.itemStacks[i].writeToNBT(dataForSlot);
                dataForAllSlots.appendTag(dataForSlot);
            }
        }
        compound.setTag("Items", dataForAllSlots);

        compound.setShort("decompositeTime", decompositeTime);
        compound.setTag("usingTimeRemaining", new NBTTagIntArray(usingTimeRemaining));
        compound.setTag("usingTimeInitialValue", new NBTTagIntArray(usingTimeInitialValue));
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        final byte NBT_TYPE_COMPOUND = 10;
        NBTTagList dataForAllSlots = nbtTagCompound.getTagList("Items", NBT_TYPE_COMPOUND);

        Arrays.fill(itemStacks, null);
        for (int i = 0; i < dataForAllSlots.tagCount(); ++i) {
            NBTTagCompound dataForOneSlot = dataForAllSlots.getCompoundTagAt(i);
            byte slotNumber = dataForOneSlot.getByte("Slot");
            if (slotNumber >= 0 && slotNumber < this.itemStacks.length) {
                this.itemStacks[slotNumber] = ItemStack.loadItemStackFromNBT(dataForOneSlot);
            }
        }

        // Load everything else.  Trim the arrays (or pad with 0) to make sure they have the correct number of elements
        decompositeTime = nbtTagCompound.getShort("decompositeTime");
        usingTimeRemaining = Arrays.copyOf(nbtTagCompound.getIntArray("usingTimeRemaining"), BOTTLE_SLOTS);
        usingTimeInitialValue = Arrays.copyOf(nbtTagCompound.getIntArray("usingTimeInitialValue"), BOTTLE_SLOTS);
        cachedNumberOfUsingSlots = -1;
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        NBTTagCompound updateTagDescribingTileEntityState = getUpdateTag();
        final int METADATA = 0;
        return new SPacketUpdateTileEntity(this.pos, METADATA, updateTagDescribingTileEntityState);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound updateTagDescribingTileEntityState = pkt.getNbtCompound();
        handleUpdateTag(updateTagDescribingTileEntityState);
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        writeToNBT(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        this.readFromNBT(tag);
    }

    @Override
    public void clear() {
        Arrays.fill(itemStacks, null);
    }

    @Override
    public String getName() {
        return "container.chemistry_decompositor.name";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    private static final byte DECOMPOSITE_FIELD_ID = 0;
    private static final byte FIRST_USING_TIME_REMAINING_FIELD_ID = 1;
    private static final byte FIRST_USING_TIME_INITIAL_FIELD_ID = FIRST_USING_TIME_REMAINING_FIELD_ID + (byte)BOTTLE_SLOTS;
    private static final byte NUMBER_OF_FIELDS = FIRST_USING_TIME_INITIAL_FIELD_ID + (byte)BOTTLE_SLOTS;

    @Override
    public int getField(int id) {
        if (id == DECOMPOSITE_FIELD_ID) return decompositeTime;
        if (id >= FIRST_USING_TIME_REMAINING_FIELD_ID && id < FIRST_USING_TIME_REMAINING_FIELD_ID + BOTTLE_SLOTS) {
            return usingTimeRemaining[id - FIRST_USING_TIME_REMAINING_FIELD_ID];
        }
        if (id >= FIRST_USING_TIME_INITIAL_FIELD_ID && id < FIRST_USING_TIME_INITIAL_FIELD_ID + BOTTLE_SLOTS) {
            return usingTimeInitialValue[id - FIRST_USING_TIME_INITIAL_FIELD_ID];
        }
        System.err.println("Invalid field ID in TileEntityChemistryDecomposer.getField:" + id);
        return 0;
    }

    @Override
    public void setField(int id, int value)
    {
        if (id == DECOMPOSITE_FIELD_ID) {
            decompositeTime = (short)value;
        } else if (id >= FIRST_USING_TIME_REMAINING_FIELD_ID && id < FIRST_USING_TIME_REMAINING_FIELD_ID + BOTTLE_SLOTS) {
            usingTimeRemaining[id - FIRST_USING_TIME_REMAINING_FIELD_ID] = value;
        } else if (id >= FIRST_USING_TIME_INITIAL_FIELD_ID && id < FIRST_USING_TIME_INITIAL_FIELD_ID + BOTTLE_SLOTS) {
            usingTimeInitialValue[id - FIRST_USING_TIME_INITIAL_FIELD_ID] = value;
        } else {
            System.err.println("Invalid field ID in TileEntityChemistryDecomposer.setField:" + id);
        }
    }

    @Override
    public int getFieldCount() {
        return NUMBER_OF_FIELDS;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack itemStack = getStackInSlot(index);
        if (itemStack != null) setInventorySlotContents(index, null);
        return itemStack;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }


}

