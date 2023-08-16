package de.nekosarekawaii.foxglove.injection.mixins;

import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.util.minecraft.inventory.InventoryUtil;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LecternScreen.class)
public abstract class MixinLecternScreen extends BookScreen {

    @Inject(method = "init", at = @At("RETURN"))
    public void injectInit(final CallbackInfo ci) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().lecternCrasher.getValue()) {
            addDrawableChild(ButtonWidget.builder(Text.of("Lectern Crasher (1.14+)"), button -> {
                if (client == null) return;
                final ClientPlayerEntity player = client.player;
                final ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
                if (player == null || networkHandler == null) return;
                final ScreenHandler screenHandler = player.currentScreenHandler;
                networkHandler.sendPacket(new ClickSlotC2SPacket(screenHandler.syncId, screenHandler.getRevision(), 0, 0, SlotActionType.QUICK_MOVE, screenHandler.getCursorStack().copy(), InventoryUtil.createDummyModifiers()));
            }).position(5, 5).size(110, 20).build());
        }
    }

}
