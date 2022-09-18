package com.neep.neepmeat.client.screen.tablet;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.guide.GuideNode;
import com.neep.neepmeat.guide.GuideReloadListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Environment(value= EnvType.CLIENT)
public class TabletListPane extends ContentPane implements Drawable, Element, Selectable
{
    public static final Identifier TERMINAL_ICON = new Identifier(NeepMeat.NAMESPACE, "textures/gui/tablet/widgets/terminal.png");

    // Currently available entries
    protected final List<EntryWidget> entries = new ArrayList<>();

    private int menuPage;
    protected int entryHeight = 11;

    public TabletListPane(ITabletScreen parent)
    {
        super(Text.of("eeeee"), parent);
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        super.render(matrices, mouseX, mouseY, delta);
        GUIUtil.renderBorder(matrices, x, y, width, height, 0xFF888800, 0);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode != GLFW.GLFW_KEY_ESCAPE)
        {
            return false;
        }
        super.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers)
    {
        if (!(chr == GLFW.GLFW_KEY_ESCAPE))
        {
        }
        super.charTyped(chr, modifiers);
        return true;
    }

    @Override
    public void init()
    {
        super.init();
        clearChildren();
        generateMenu();
    }

    protected void generateMenu()
    {
        entries.clear();

        List<GuideNode> nodes = parent.getPath().peek().getChildren();
        // TODO: pages
        for (int i = 0; i < nodes.size(); ++i)
        {
            GuideNode node = nodes.get(i);
            ItemStack icon = new ItemStack(Registry.ITEM.get(node.getIcon()));
            entries.add(new EntryWidget(screenOffsetX + this.x, screenOffsetY + this.y + i * entryHeight, width - 2 * screenOffsetX, entryHeight, icon, node.getText(), node));
        }
        entries.forEach(this::addDrawableChild);
    }

    protected int getPageEntries()
    {
        return 10;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder)
    {

    }

    @Override
    public SelectionType getType()
    {
        return SelectionType.NONE;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return false;
    }

    public class EntryWidget extends ClickableWidget
    {
        private final GuideNode node;
        private final ItemStack icon;

        public EntryWidget(int x, int y, int w, int h, ItemStack icon, Text text, GuideNode node)
        {
            super(x, y, w, h, text);
            this.node = node;
            this.icon = icon;
        }

        @Override
        public void playDownSound(SoundManager soundManager)
        {
            soundManager.play(PositionedSoundInstance.master(SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, 1.0f));
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            this.onPress();
        }

        protected void onPress()
        {
            node.visitScreen(parent);
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder)
        {

        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            VertexConsumerProvider vertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            this.renderBackground(matrices, MinecraftClient.getInstance(), mouseX, mouseY);
            int borderCol = 0xFF008800;
            this.drawHorizontalLine(matrices, this.x, this.x + width, this.y + height, borderCol);
            this.drawHorizontalLine(matrices, this.x, this.x + width, this.y, borderCol);
            this.drawVerticalLine(matrices, this.x, this.y, this.y + height, borderCol);
            this.drawVerticalLine(matrices, this.x + width, this.y, this.y + height, borderCol);
//            int j = this.active ? 0xFFFFFF : 0xA0A0A0;
            textRenderer.draw(matrices, this.getMessage(), this.x + 2, this.y + (this.height - 7) / 2f, 0x008800);
            renderItemIcon(this.x + width - 16, this.y - 1, itemRenderer, getZOffset(), icon, matrices, vertexConsumers, 15, 0);
        }
    }

    public static void renderItemIcon(int x, int y, ItemRenderer renderer, int zOffset, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        if (stack.isEmpty())
        {
            return;
        }

        ItemModels models = renderer.getModels();
        BakedModel bakedModel = models.getModel(stack);

        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 100.0f + zOffset);
        matrixStack.translate(8.0, 8.0, 0.0);
        matrixStack.scale(1.0f, -1.0f, 1.0f);
        matrixStack.scale(12.0f, 12.0f, 12.0f);
        RenderSystem.applyModelViewMatrix();
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
//        renderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack2, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, bakedModel);
        renderItem(stack, matrices, vertexConsumers, light, overlay, bakedModel);
        immediate.draw();
        RenderSystem.enableDepthTest();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();

//        renderer.zOffset = bakedModel.hasDepth() ? zOffset + 50.0f + 0 : zOffset + 50.0f;
//
//        BakedModel model = renderer.getModels().getModel(stack);
//        renderItem(stack, matrices, vertexConsumers, light, overlay, model);
    }

    public static void renderItem(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model)
    {
        ModelTransformation.Mode renderMode = ModelTransformation.Mode.GUI;
        matrices.push();
        model.getTransformation().getTransformation(renderMode).apply(false, matrices);
        matrices.translate(-0.5, -0.5, -0.5);
        RenderLayer renderLayer = RenderLayers.getItemLayer(stack, true);
        VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
        renderBakedItemModel(model, stack, light, overlay, matrices, vertexConsumer);
        matrices.pop();
    }

    private static void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices)
    {
        Random random = new Random();

        for (Direction direction : Direction.values())
        {
            random.setSeed(42L);
            renderGreenQuads(matrices, vertices, model.getQuads(null, direction, random), stack, light, overlay);
        }
        random.setSeed(42L);
        renderGreenQuads(matrices, vertices, model.getQuads(null, null, random), stack, light, overlay);
    }

    private static void renderGreenQuads(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay)
    {
        MatrixStack.Entry entry = matrices.peek();
        for (BakedQuad bakedQuad : quads)
        {

            int i = 0x77FF77;
            float r = (float)(i >> 16 & 0xFF) / 255.0f;
            float g = (float)(i >> 8 & 0xFF) / 255.0f;
            float b = (float)(i & 0xFF) / 255.0f;

            vertices.quad(entry, bakedQuad, r, g, b, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
        }
    }
}