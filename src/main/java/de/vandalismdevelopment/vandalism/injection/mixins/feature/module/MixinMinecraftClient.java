package de.vandalismdevelopment.vandalism.injection.mixins.feature.module;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.misc.FastUseModule;
import de.vandalismdevelopment.vandalism.util.interfaces.MinecraftWrapper;
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
        if (entity == this.player()) return;
        if (Vandalism.getInstance().getModuleRegistry().getEspModule().isEnabled()) {
            cir.setReturnValue(true);
        }
    }

    @ModifyConstant(method = "doItemUse", constant = @Constant(intValue = 4))
    private int vandalism$fastUseItemUseCooldown(final int value) {
        final FastUseModule fastUseModule = Vandalism.getInstance().getModuleRegistry().getFastUseModule();
        if (fastUseModule.isEnabled()) return fastUseModule.itemUseCooldown.getValue();
        return value;
    }

}