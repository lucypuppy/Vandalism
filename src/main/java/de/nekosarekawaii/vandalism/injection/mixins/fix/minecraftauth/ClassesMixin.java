package de.nekosarekawaii.vandalism.injection.mixins.fix.minecraftauth;

import io.jsonwebtoken.lang.Classes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
 * JsonWebToken is used by MinecraftAuth and since it's using Java services, it's not working with the fabric loader
 * So we have to change all services usages by using the normal Java API
 */
@Mixin(value = Classes.class, remap = false)
public abstract class ClassesMixin {

    @Inject(method = "forName", at = @At("HEAD"), cancellable = true)
    private static void removeServicesSupport(String fqcn, CallbackInfoReturnable<Class<Object>> cir) throws ClassNotFoundException {
        cir.setReturnValue((Class<Object>) Class.forName(fqcn));
    }

}