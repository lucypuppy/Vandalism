package de.vandalismdevelopment.vandalism.injection.mixins.feature.module;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.module.impl.misc.IllegalBlockPlaceModule;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {

    @Redirect(method = "interactBlockInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"))
    private ActionResult vandalism$illegalBlockPlaceViaVersionBug(final ItemStack instance, final ItemUsageContext context) {
        ActionResult actionResult = instance.useOnBlock(context);
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            final IllegalBlockPlaceModule illegalBlockPlaceModule = Vandalism.getInstance().getModuleManager().getIllegalBlockPlaceModule();
            if (illegalBlockPlaceModule.isActive() && illegalBlockPlaceModule.viaVersionBug.getValue()) {
                if (actionResult == ActionResult.FAIL) {
                    actionResult = ActionResult.SUCCESS;
                }
            }
        }
        return actionResult;
    }

}
