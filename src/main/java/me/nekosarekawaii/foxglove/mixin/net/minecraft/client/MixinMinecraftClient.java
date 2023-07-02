package me.nekosarekawaii.foxglove.mixin.net.minecraft.client;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.event.ClientListener;
import me.nekosarekawaii.foxglove.event.TickListener;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;onResolutionChanged()V"))
	private void startMod(final CallbackInfo callbackInfo) {
		DietrichEvents2.global().postInternal(ClientListener.ClientEvent.ID, new ClientListener.ClientEvent(ClientListener.ClientEventType.START));
	}

	@Inject(method = "getWindowTitle", at = @At("RETURN"), cancellable = true)
	private void overwriteWindowTitle(final CallbackInfoReturnable<String> callbackInfo) {
		callbackInfo.setReturnValue(Foxglove.getInstance().getWindowTitle());
	}

	@Inject(method = "tick", at = @At(value = "HEAD"))
	private void injectTick(final CallbackInfo ci) {
		DietrichEvents2.global().postInternal(TickListener.TickEvent.ID, new TickListener.TickEvent());
	}

}
