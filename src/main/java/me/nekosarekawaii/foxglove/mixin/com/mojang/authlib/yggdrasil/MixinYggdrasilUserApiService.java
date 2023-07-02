package me.nekosarekawaii.foxglove.mixin.com.mojang.authlib.yggdrasil;

import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import me.nekosarekawaii.foxglove.Foxglove;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.Executor;

@Mixin(value = YggdrasilUserApiService.class, remap = false)
public abstract class MixinYggdrasilUserApiService {

    @Inject(method = "newTelemetrySession", at = @At("RETURN"), cancellable = true)
    private void injectNewTelemetrySession(final Executor executor, final CallbackInfoReturnable<TelemetrySession> cir) {
        if (Foxglove.getInstance().getFeatures().getAntiTelemetryModule().isEnabled()) {
            cir.setReturnValue(TelemetrySession.DISABLED);
        }
    }

}
