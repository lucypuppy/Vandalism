package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import de.vandalismdevelopment.vandalism.Vandalism;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.Executor;

@Mixin(value = YggdrasilUserApiService.class, remap = false)
public abstract class MixinYggdrasilUserApiService {

    @Inject(method = "newTelemetrySession", at = @At("RETURN"), cancellable = true)
    private void injectNewTelemetrySession(final Executor executor, final CallbackInfoReturnable<TelemetrySession> cir) {
        if (Vandalism.getInstance().getConfigManager().getMainConfig().antiTelemetry.getValue()) {
            cir.setReturnValue(TelemetrySession.DISABLED);
        }
    }

}
