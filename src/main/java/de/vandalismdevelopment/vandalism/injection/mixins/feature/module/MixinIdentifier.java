package de.vandalismdevelopment.vandalism.injection.mixins.feature.module;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.module.ModuleRegistry;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.exploit.ExploitFixerModule;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Identifier.class)
public abstract class MixinIdentifier {

    @Shadow
    public static boolean isNamespaceValid(final String namespace) {
        return false;
    }

    @Shadow
    public static boolean isPathValid(final String path) {
        return false;
    }

    @Inject(method = "validateNamespace", at = @At("HEAD"), cancellable = true)
    private static void vandalism$exploitFixerBlockInvalidIdentifierCrash1(final String namespace, final String path, final CallbackInfoReturnable<String> cir) {
        final Vandalism instance = Vandalism.getInstance();
        if (instance != null) {
            final ModuleRegistry moduleRegistry = instance.getModuleRegistry();
            if (moduleRegistry != null && moduleRegistry.isDone()) {
                final ExploitFixerModule exploitFixerModule = moduleRegistry.getExploitFixerModule();
                if (exploitFixerModule != null && exploitFixerModule.isEnabled() && exploitFixerModule.blockInvalidIdentifierCrash.getValue()) {
                    if (!isNamespaceValid(namespace)) {
                        cir.setReturnValue("invalid");
                    }
                }
            }
        }
    }

    @Inject(method = "validatePath", at = @At("HEAD"), cancellable = true)
    private static void vandalism$exploitFixerBlockInvalidIdentifierCrash2(final String namespace, final String path, final CallbackInfoReturnable<String> cir) {
        final Vandalism instance = Vandalism.getInstance();
        if (instance != null) {
            final ModuleRegistry moduleRegistry = instance.getModuleRegistry();
            if (moduleRegistry != null && moduleRegistry.isDone()) {
                final ExploitFixerModule exploitFixerModule = moduleRegistry.getExploitFixerModule();
                if (exploitFixerModule != null && exploitFixerModule.isEnabled() && exploitFixerModule.blockInvalidIdentifierCrash.getValue()) {
                    if (!isPathValid(namespace)) {
                        cir.setReturnValue("invalid");
                    }
                }
            }
        }
    }

}
