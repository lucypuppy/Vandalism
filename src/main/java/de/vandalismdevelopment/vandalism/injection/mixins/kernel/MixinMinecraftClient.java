package de.vandalismdevelopment.vandalism.injection.mixins.kernel;

import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(value = MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    @Final
    private Window window;

    @Shadow
    @Final
    public File runDirectory;

    @Unique
    private long vandalism_startTime = 0L;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/io/File;toPath()Ljava/nio/file/Path;", ordinal = 0))
    private void vandalism$startTimeCalculation1(final CallbackInfo ci) {
        vandalism_startTime = System.currentTimeMillis();
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;onResolutionChanged()V"))
    private void vandalism$startMod(final CallbackInfo ci) {
        Vandalism.getInstance().start(this.window, this.runDirectory);
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void vandalism$startTimeCalculation2(final CallbackInfo ci) {
        Vandalism.getInstance().getLogger().info("");
        Vandalism.getInstance().getLogger().info("Minecraft loading took ~" + (System.currentTimeMillis() - vandalism_startTime) + "ms.");
        Vandalism.getInstance().getLogger().info("");
    }

    @Inject(method = "updateWindowTitle", at = @At("HEAD"), cancellable = true)
    private void vandalism$forceCancelWindowTitleUpdates(final CallbackInfo ci) {
        ci.cancel();
    }

}