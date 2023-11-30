package de.vandalismdevelopment.vandalism.gui.minecraft;

import de.vandalismdevelopment.vandalism.util.interfaces.MinecraftWrapper;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.text.Text;

public class CustomResourcePackConfirmScreen extends ConfirmScreen implements MinecraftWrapper {

    public static boolean skipDownload = false, dump = false;

    public CustomResourcePackConfirmScreen(final BooleanConsumer callback, final Text title, final Text message) {
        super(callback, title, message);
        skipDownload = false;
        dump = false;
    }

    @Override
    protected void addButtons(final int y) {
        super.addButtons(y);
        this.addButton(ButtonWidget.builder(Text.literal("Spoof"), (button) -> {
            if (this.networkHandler() != null) {
                this.networkHandler().sendPacket(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.ACCEPTED));
                this.networkHandler().sendPacket(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
                if (this.mc().inGameHud != null) {
                    this.setScreen(null);
                }
            }
        }).dimensions(this.width / 2 - 155, y + 22, 150, 20).build());
        this.addButton(ButtonWidget.builder(Text.literal("Spoof Download"), (button) -> {
            skipDownload = true;
            this.callback.accept(true);
        }).dimensions(this.width / 2 - 155 + 160, y + 22, 150, 20).build());
        this.addButton(ButtonWidget.builder(Text.literal("Dump"), (button) -> {
            dump = true;
            this.callback.accept(true);
        }).dimensions(this.width / 2 - 155, y + 44, 150, 20).build());
        if (this.mc().inGameHud != null) {
            this.addButton(ButtonWidget.builder(Text.literal("Close Screen"), (button) -> this.setScreen(null)).dimensions(this.width / 2 - 155 + 160, y + 44, 150, 20).build());
        }
    }

}
