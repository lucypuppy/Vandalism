package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(TridentItem.class)
public abstract class MixinTridentItem {

    @ModifyArgs(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addVelocity(DDD)V"))
    public void vandalism$customizeRiptideBoostMultipier(final Args args) {
        if (!Vandalism.getInstance().getClientSettings().getMovementSettings().customizeRiptideBoostMultiplier.getValue()) {
            return;
        }
        final float multiplier = Vandalism.getInstance().getClientSettings().getMovementSettings().riptideBoostMultiplier.getValue();
        for (int i = 0; i < 2; i++) args.set(i, (double) args.get(i) * multiplier);
    }

}
