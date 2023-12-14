package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import de.nekosarekawaii.vandalism.Vandalism;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.Executor;

@Mixin(value = YggdrasilUserApiService.class, remap = false)
public abstract class MixinYggdrasilUserApiService {

    @Inject(method = "newTelemetrySession", at = @At("RETURN"), cancellable = true)
    private void vandalism$antiTelemetry(final Executor executor, final CallbackInfoReturnable<TelemetrySession> cir) {
        if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().antiTelemetry.getValue()) {
            cir.setReturnValue(TelemetrySession.DISABLED);
        }
    }

}
