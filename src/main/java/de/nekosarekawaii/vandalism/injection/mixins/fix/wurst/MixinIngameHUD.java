package de.nekosarekawaii.vandalism.injection.mixins.fix.wurst;

import net.minecraft.client.gui.DrawContext;
import net.wurstclient.hud.HackListHUD;
import net.wurstclient.hud.IngameHUD;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = IngameHUD.class, remap = false)
public abstract class MixinIngameHUD {

    @Redirect(method = "onRenderGUI", at = @At(value = "INVOKE", target = "Lnet/wurstclient/hud/HackListHUD;render(Lnet/minecraft/client/gui/DrawContext;F)V"))
    private void disableWurstHackList(final HackListHUD instance, final DrawContext context, final float partialTicks) {
    }

}
