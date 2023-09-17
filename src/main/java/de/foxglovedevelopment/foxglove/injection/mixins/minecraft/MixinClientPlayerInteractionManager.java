package de.foxglovedevelopment.foxglove.injection.mixins.minecraft;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.foxglovedevelopment.foxglove.Foxglove;
import de.foxglovedevelopment.foxglove.feature.impl.module.impl.misc.IllegalBlockPlaceModule;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {

    @Inject(method = "hasLimitedAttackSpeed", at = @At("HEAD"), cancellable = true)
    public void injectHasLimitedAttackSpeed(final CallbackInfoReturnable<Boolean> cir) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().eliminateHitDelay.getValue()) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "interactBlockInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"))
    private ActionResult redirectInteractBlockInternal(final ItemStack instance, final ItemUsageContext context) {
        ActionResult actionResult = instance.useOnBlock(context);
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            final IllegalBlockPlaceModule illegalBlockPlaceModule = Foxglove.getInstance().getModuleRegistry().getIllegalBlockPlaceModule();
            if (illegalBlockPlaceModule.isEnabled() && illegalBlockPlaceModule.viaVersionBug.getValue()) {
                if (actionResult == ActionResult.FAIL) {
                    actionResult = ActionResult.SUCCESS;
                }
            }
        }
        return actionResult;
    }

}
