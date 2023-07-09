package me.nekosarekawaii.foxglove.mixin.net.minecraft.client.gui.screen;

import me.nekosarekawaii.foxglove.util.ServerUtils;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DownloadingTerrainScreen.class)
public abstract class MixinDownloadingTerrainScreen extends Screen {

    @Shadow
    public abstract void close();

    protected MixinDownloadingTerrainScreen(final Text ignored) {
        super(ignored);
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), button -> ServerUtils.disconnect()).dimensions(this.width / 2 - 50, this.height / 2 + 22, 100, 20).build());
    }

}
