package de.nekosarekawaii.foxglove.injection.mixins;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.event.ScreenListener;
import de.nekosarekawaii.foxglove.event.TickListener;
import de.nekosarekawaii.foxglove.event.WorldListener;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.misc.FastUseModule;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.render.ESPModule;
import de.nekosarekawaii.foxglove.util.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow @Nullable public ClientPlayerEntity player;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;onResolutionChanged()V"))
    private void injectInit(final CallbackInfo ci) {
        Foxglove.getInstance().start((MinecraftClient) (Object) this);
    }

    @Inject(method = "updateWindowTitle", at = @At("HEAD"), cancellable = true)
    private void injectUpdateWindowTitle(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void injectTick(final CallbackInfo ci) {
        DietrichEvents2.global().postInternal(TickListener.TickEvent.ID, new TickListener.TickEvent());
    }

    @Inject(method = "setScreen", at = @At(value = "HEAD"), cancellable = true)
    private void injectSetScreen(Screen screen, final CallbackInfo ci) {
        final ScreenListener.OpenScreenEvent openScreenEvent = new ScreenListener.OpenScreenEvent(screen);
        DietrichEvents2.global().postInternal(ScreenListener.OpenScreenEvent.ID, openScreenEvent);
        if (openScreenEvent.isCancelled()) ci.cancel();
        else screen = openScreenEvent.screen;
    }

    @Inject(method = "setWorld", at = @At("HEAD"))
    private void injectSetWorldHead(final ClientWorld world, final CallbackInfo ci) {
        DietrichEvents2.global().postInternal(WorldListener.WorldLoadEvent.ID, new WorldListener.WorldLoadEvent(WorldListener.State.PRE));
    }

    @Inject(method = "setWorld", at = @At("RETURN"))
    private void injectSetWorldReturn(final ClientWorld world, final CallbackInfo ci) {
        DietrichEvents2.global().postInternal(WorldListener.WorldLoadEvent.ID, new WorldListener.WorldLoadEvent(WorldListener.State.POST));
    }

    @ModifyConstant(method = "doItemUse", constant = @Constant(intValue = 4))
    private int modifyDoItemUse(final int value) {
        final FastUseModule fastUseModule = Foxglove.getInstance().getModuleRegistry().getFastUseModule();
        if (fastUseModule.isEnabled()) return fastUseModule.itemUseCooldown.getValue();
        return value;
    }

    @Inject(method = "hasOutline", at = @At("RETURN"), cancellable = true)
    private void injectHasOutline(final Entity entity, final CallbackInfoReturnable<Boolean> cir) {
        if (entity == this.player) return;
        final ESPModule espModule = Foxglove.getInstance().getModuleRegistry().getEspModule();
        if (espModule.isEnabled()) cir.setReturnValue(true);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void onRender(boolean tick, CallbackInfo ci) {
        RenderUtils.drawFrame();
    }

}
