package de.nekosarekawaii.foxglove.injection.mixins;

import de.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(TridentItem.class)
public abstract class MixinTridentItem {

    @ModifyArgs(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addVelocity(DDD)V"))
    public void modifyArgsOnStoppedUsing(final Args args) {
        if (!Foxglove.getInstance().getConfigManager().getMainConfig().customizeRiptideBoostMultiplier.getValue())
            return;
        final float multiplier = Foxglove.getInstance().getConfigManager().getMainConfig().riptideBoostMultiplier.getValue();
        for (int i = 0; i < 2; i++) args.set(i, (double) args.get(i) * multiplier);
    }

}
