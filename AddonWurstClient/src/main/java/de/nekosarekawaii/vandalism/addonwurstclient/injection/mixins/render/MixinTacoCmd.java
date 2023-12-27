package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.render;

import net.wurstclient.commands.TacoCmd;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = TacoCmd.class, remap = false)
public abstract class MixinTacoCmd {

    @ModifyArgs(method = "onRenderGUI", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V"), remap = false)
    private void moveWurstTaco(final Args args) {
        args.set(1, (int) args.get(1) + 64);
        args.set(2, (int) args.get(2) + 18);
    }

}
