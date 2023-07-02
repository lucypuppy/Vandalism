package me.nekosarekawaii.foxglove.gui.screen;

import me.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class TitleScreen extends Screen {

    public TitleScreen() {
        super(Text.literal(""));
    }

    @Override
    protected void init() {
        super.init();
        int y = 50;
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("menu.singleplayer"),
                button -> {
                    if (this.client != null) {
                        this.client.setScreen(new SelectWorldScreen(this));
                    }
                }
        ).dimensions(10, y, 98, 20).build());
        y += 22;
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("menu.multiplayer"),
                button -> {
                    if (this.client != null) {
                        this.client.setScreen(this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this));
                    }
                }
        ).dimensions(10, y, 98, 20).build());
        y += 22;
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("menu.options"),
                button -> {
                    if (this.client != null) {
                        this.client.setScreen(new OptionsScreen(this, this.client.options));
                    }
                }
        ).dimensions(10, y, 98, 20).build());
        y += 22;
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("menu.quit"),
                button -> {
                    if (this.client != null) {
                        this.client.scheduleStop();
                    }
                }
        ).dimensions(10, y, 98, 20).build());
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        this.renderBackground(context);
        context.drawText(this.textRenderer, Foxglove.getInstance().getName() + " v" + Foxglove.getInstance().getVersion(), 5, 10, -1, true);
        final String author = "Made by " + Foxglove.getInstance().getAuthor();
        context.drawText(this.textRenderer, author, this.width - 5 - this.textRenderer.getWidth(author), this.height - 20, -1, true);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

}
