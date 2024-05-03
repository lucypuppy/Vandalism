package de.nekosarekawaii.vandalism.injection.mixins.fix.minecraftauth;

import io.jsonwebtoken.gson.io.GsonDeserializer;
import io.jsonwebtoken.impl.DefaultJwtParserBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = DefaultJwtParserBuilder.class, remap = false)
public abstract class DefaultJwtParserBuilderMixin {

    @Redirect(method = "build()Lio/jsonwebtoken/JwtParser;", at = @At(value = "INVOKE", target = "Lio/jsonwebtoken/impl/lang/Services;loadFirst(Ljava/lang/Class;)Ljava/lang/Object;"))
    public Object removeServicesSupport(Class<?> spi) {
        return new GsonDeserializer<>();
    }

}