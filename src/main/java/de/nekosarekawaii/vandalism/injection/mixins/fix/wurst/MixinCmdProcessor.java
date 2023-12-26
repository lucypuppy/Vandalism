package de.nekosarekawaii.vandalism.injection.mixins.fix.wurst;

import net.wurstclient.command.CmdProcessor;
import net.wurstclient.events.ChatOutputListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CmdProcessor.class, remap = false)
public abstract class MixinCmdProcessor {

    @Inject(method = "onSentMessage", at = @At("HEAD"), cancellable = true)
    private void cancelWurstCommandHandling(final ChatOutputListener.ChatOutputEvent event, final CallbackInfo ci) {
        ci.cancel();
    }

}
