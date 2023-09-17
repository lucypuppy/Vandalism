package de.foxglovedevelopment.foxglove.injection.mixins.minecraft;

import com.mojang.authlib.minecraft.UserApiService;
import de.foxglovedevelopment.foxglove.Foxglove;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.telemetry.PropertyMap;
import net.minecraft.client.util.telemetry.TelemetryManager;
import net.minecraft.client.util.telemetry.TelemetrySender;
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

    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/util/telemetry/TelemetryManager;propertyMap:Lnet/minecraft/client/util/telemetry/PropertyMap;"))
    private void injectInit(final MinecraftClient client, final UserApiService userApi, final Session session, final CallbackInfo ci) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().antiTelemetry.getValue()) {
            this.propertyMap = PropertyMap.builder().build();
        }
    }

    @Inject(method = "getSender", at = @At("RETURN"), cancellable = true)
    private void injectComputeSender(final CallbackInfoReturnable<TelemetrySender> cir) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().antiTelemetry.getValue()) {
            cir.setReturnValue(TelemetrySender.NOOP);
        }
    }

}
