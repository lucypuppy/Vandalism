package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.nekosarekawaii.vandalism.Vandalism;
import net.minecraft.client.gui.screen.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DeathScreen.class)
public abstract class MixinDeathScreen {

    @Shadow
    protected abstract void setButtonsActive(final boolean active);

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/DeathScreen;setButtonsActive(Z)V"))
    private void forceEnableRespawnButton(final DeathScreen instance, boolean active) {
        if (Vandalism.getInstance().getClientSettings().getMenuSettings().forceEnableRespawnButton.getValue()) {
            active = true;
        }
        this.setButtonsActive(active);
    }

}
