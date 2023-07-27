package me.nekosarekawaii.foxglove.injection.mixins;

import me.nekosarekawaii.foxglove.FabricBridge;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.exploit.ExploitFixerModule;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Identifier.class)
public abstract class MixinIdentifier {

    @Shadow
    private static boolean isNamespaceValid(final String namespace) {
        return false;
    }

    @Shadow
    private static boolean isPathValid(final String path) {
        return false;
    }

    @Inject(method = "validateNamespace", at = @At("HEAD"), cancellable = true)
    private static void injectValidateNamespace(final String namespace, final String path, final CallbackInfoReturnable<String> cir) {
        if (FabricBridge.modInitialized) {
            final ExploitFixerModule exploitFixerModule = Foxglove.getInstance().getModuleRegistry().getExploitFixerModule();
            if (exploitFixerModule != null && exploitFixerModule.isEnabled() && exploitFixerModule.blockInvalidIdentifierCrash.getValue()) {
                if (!isNamespaceValid(namespace)) {
                    cir.setReturnValue("invalid");
                }
            }
        }
    }

    @Inject(method = "validatePath", at = @At("HEAD"), cancellable = true)
    private static void injectValidatePath(final String namespace, final String path, final CallbackInfoReturnable<String> cir) {
        if (FabricBridge.modInitialized) {
            final ExploitFixerModule exploitFixerModule = Foxglove.getInstance().getModuleRegistry().getExploitFixerModule();
            if (exploitFixerModule != null && exploitFixerModule.isEnabled() && exploitFixerModule.blockInvalidIdentifierCrash.getValue()) {
                if (!isPathValid(namespace)) {
                    cir.setReturnValue("invalid");
                }
            }
        }
    }

}
