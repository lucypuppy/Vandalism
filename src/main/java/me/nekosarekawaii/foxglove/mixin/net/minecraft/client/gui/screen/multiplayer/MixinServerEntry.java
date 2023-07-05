package me.nekosarekawaii.foxglove.mixin.net.minecraft.client.gui.screen.multiplayer;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.impl.MultiplayerServerEntriesListener;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public abstract class MixinServerEntry {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I", shift = At.Shift.AFTER))
    private void injectRender(final DrawContext context, final int index, final int y, final int x, final int entryWidth, final int entryHeight, final int mouseX, final int mouseY, final boolean hovered, final float tickDelta, final CallbackInfo ci) {
        DietrichEvents2.global().postInternal(MultiplayerServerEntriesListener.TextRenderEvent.ID, new MultiplayerServerEntriesListener.TextRenderEvent(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta));
    }

}
