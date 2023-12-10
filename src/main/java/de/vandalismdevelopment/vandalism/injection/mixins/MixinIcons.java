package de.vandalismdevelopment.vandalism.injection.mixins;

import de.vandalismdevelopment.vandalism.base.FabricBootstrap;
import net.minecraft.client.main.Main;
import net.minecraft.client.util.Icons;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

@Mixin(Icons.class)
public abstract class MixinIcons {

    @Inject(method = "getIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePack;openRoot([Ljava/lang/String;)Lnet/minecraft/resource/InputSupplier;"), cancellable = true)
    private void vandalism$forceModIcon(final ResourcePack resourcePack, final String fileName, final CallbackInfoReturnable<InputSupplier<InputStream>> cir) {
        if (!fileName.endsWith(".png")) return;
        cir.setReturnValue(() -> Main.class.getClassLoader().getResourceAsStream("assets/" + FabricBootstrap.MOD_ID + "/textures/icon/" + fileName));
    }

}
