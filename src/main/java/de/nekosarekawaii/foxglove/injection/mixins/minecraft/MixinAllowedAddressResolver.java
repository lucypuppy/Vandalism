package de.nekosarekawaii.foxglove.injection.mixins.minecraft;

import de.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.BlockListChecker;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AllowedAddressResolver.class)
public abstract class MixinAllowedAddressResolver {

    @Redirect(method = "resolve", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/BlockListChecker;isAllowed(Lnet/minecraft/client/network/Address;)Z"))
    public boolean alwaysAllowAddressParsing(final BlockListChecker instance, final Address address) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().antiServerBlockList.getValue()) {
            return true;
        }
        return instance.isAllowed(address);
    }

    @Redirect(method = "resolve", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/BlockListChecker;isAllowed(Lnet/minecraft/client/network/ServerAddress;)Z"))
    public boolean alwaysAllowServerAddressParsing(final BlockListChecker instance, final ServerAddress serverAddress) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().antiServerBlockList.getValue()) {
            return true;
        }
        return instance.isAllowed(serverAddress);
    }

}
