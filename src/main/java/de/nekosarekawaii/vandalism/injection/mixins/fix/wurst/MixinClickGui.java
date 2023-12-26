package de.nekosarekawaii.vandalism.injection.mixins.fix.wurst;

import net.wurstclient.clickgui.ClickGui;
import net.wurstclient.clickgui.Component;
import net.wurstclient.clickgui.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ClickGui.class, remap = false)
public abstract class MixinClickGui {

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/wurstclient/clickgui/Window;add(Lnet/wurstclient/clickgui/Component;)V", ordinal = 2))
    private void removeWurstHackListButton(final Window instance, final Component component) {
    }

}
