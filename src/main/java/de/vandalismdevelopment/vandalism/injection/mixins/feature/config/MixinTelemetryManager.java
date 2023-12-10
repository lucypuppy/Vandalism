package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import com.mojang.authlib.minecraft.UserApiService;
import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.telemetry.PropertyMap;
import net.minecraft.client.session.telemetry.TelemetryManager;
import net.minecraft.client.session.telemetry.TelemetrySender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TelemetryManager.class)
public abstract class MixinTelemetryManager {

    @Mutable
    @Shadow
    @Final
    private PropertyMap propertyMap;

    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/session/telemetry/TelemetryManager;propertyMap:Lnet/minecraft/client/session/telemetry/PropertyMap;"))
    private void vandalism$antiTelemetry1(final MinecraftClient client, final UserApiService userApi, final Session session, final CallbackInfo ci) {
        if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().antiTelemetry.getValue()) {
            this.propertyMap = PropertyMap.builder().build();
        }
    }

    @Inject(method = "getSender", at = @At("RETURN"), cancellable = true)
    private void vandalism$antiTelemetry2(final CallbackInfoReturnable<TelemetrySender> cir) {
        if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().antiTelemetry.getValue()) {
            cir.setReturnValue(TelemetrySender.NOOP);
        }
    }

}
