package de.nekosarekawaii.foxglove.injection.mixins;

import de.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(TridentItem.class)
public abstract class MixinTridentItem {

    /**
     * I need to cast to double because Mixin will cry otherwise :(
     * 0, 1, 2 are the x, y, z values which are all doubles.
     *
     * @param args arguments of addVelocity that we want to change
     * @author Lucy
     */
    @ModifyArgs(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addVelocity(DDD)V"))
    public void modifyOnStoppedUsing(final Args args) {
        if (!Foxglove.getInstance().getModuleRegistry().getRiptideBoostMultiplierModule().isEnabled()) return;

        args.set(0, (double) args.get(0) * Foxglove.getInstance().getModuleRegistry().getRiptideBoostMultiplierModule().multiplier.getValue());
        args.set(1, (double) args.get(1) * Foxglove.getInstance().getModuleRegistry().getRiptideBoostMultiplierModule().multiplier.getValue());
        args.set(2, (double) args.get(2) * Foxglove.getInstance().getModuleRegistry().getRiptideBoostMultiplierModule().multiplier.getValue());
    }
}
