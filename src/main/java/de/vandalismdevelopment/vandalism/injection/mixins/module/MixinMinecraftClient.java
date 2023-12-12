package de.vandalismdevelopment.vandalism.injection.mixins.module;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.module.impl.misc.FastUseModule;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MinecraftClient.class)
public abstract class MixinMinecraftClient implements MinecraftWrapper {

    @Inject(method = "hasOutline", at = @At("RETURN"), cancellable = true)
    private void vandalism$espForceOutline(final Entity entity, final CallbackInfoReturnable<Boolean> cir) {
        if (entity == this.mc.player) return;
        if (Vandalism.getInstance().getModuleManager().getEspModule().isActive()) {
            cir.setReturnValue(true);
        }
    }

    @ModifyConstant(method = "doItemUse", constant = @Constant(intValue = 4))
    private int vandalism$fastUseItemUseCooldown(final int value) {
        final FastUseModule fastUseModule = Vandalism.getInstance().getModuleManager().getFastUseModule();
        if (fastUseModule.isActive()) return fastUseModule.itemUseCooldown.getValue();
        return value;
    }

}