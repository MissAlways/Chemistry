package com.missalways.chemistry.gui;

import com.missalways.chemistry.Reference;
import com.missalways.chemistry.tileentity.TileEntityChemistryDecomposer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by MissAlways on 4.11.2016.
 */
@SideOnly(Side.CLIENT)
public class GuiChemistryDecomposite extends GuiContainer{

    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID +":"+"textures/gui/container/chemistryDecompositorGui.png");
    private TileEntityChemistryDecomposer entity;

    public GuiChemistryDecomposite(InventoryPlayer player, TileEntityChemistryDecomposer tileEntityChemistryDecomposer) {
        super(new ContainerChemistryDecomposite(player, tileEntityChemistryDecomposer));

        xSize=176;
        ySize=234;
        this.entity = tileEntityChemistryDecomposer;
    }

    //Progress Bar
    final int PROGRESS_X_POS = 90;
    final int PROGRESS_Y_POS = 38;
    final int PROGRESS_ICON_U = 177;
    final int PROGRESS_ICON_V = 3;
    final int PROGRESS_WIDTH = 53;
    final int PROGRESS_HEIGHT = 10;

    // funnel
    final int FUNNEL_X_POS = 56;
    final int FUNNEL_Y_POS = 37;
    final int FUNNEL_ICON_U = 177;
    final int FUNNEL_ICON_V = 13;
    final int FUNNEL_WIDTH = 15;
    final int FUNNEL_HEIGHT = 10;


    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        GlStateManager.color(1.0f,1.0f,1.0f, 1.0f);
        drawTexturedModalRect(guiLeft, guiTop,0,0,xSize,ySize);
        double decomposeProsess = entity.fractionOfDecompositeTimeComplete();
        drawTexturedModalRect(guiLeft + PROGRESS_X_POS, guiTop + PROGRESS_Y_POS, PROGRESS_ICON_U, PROGRESS_ICON_V, (int)(decomposeProsess * PROGRESS_WIDTH), PROGRESS_HEIGHT);
    }
/*
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        final int LABEL_X_POS = 5;
        final int LABEL_Y_POS = 5;
        fontRendererObj.drawString(entity.getDisplayName().getUnformattedText(), LABEL_X_POS, LABEL_Y_POS, Color.GRAY.getRGB());
        List<String> text = new ArrayList<String>();

        if(isInRect(guiLeft + PROGRESS_X_POS, guiTop + PROGRESS_Y_POS, PROGRESS_WIDTH, PROGRESS_HEIGHT, mouseX, mouseY)){
            text.add("Progress:");
            int decompositePercentage = (int)(entity.fractionOfDecompositeTimeComplete() * 100);
            text.add(decompositePercentage+"%");
        }
        if(!text.isEmpty()){
            drawHoveringText(text, mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
        }
    }
*/
    private static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));

    }
}
