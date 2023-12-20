package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.nekosarekawaii.vandalism.Vandalism;
import net.minecraft.client.gui.screen.ReconfiguringScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReconfiguringScreen.class)
public abstract class MixinReconfigurationScreen {

    @Shadow
    private ButtonWidget disconnectButton;

    @Inject(method = "init", at = @At("TAIL"))
    private void forceEnableRespawnButton(final CallbackInfo ci) {
        if (Vandalism.getInstance().getClientSettings().getMenuSettings().forceEnableReconfiguartionDisconnectButton.getValue()) {
            this.disconnectButton.active = true;
        }
    }

}
