package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.render.CameraClipRaytraceListener;
import de.vandalismdevelopment.vandalism.base.event.render.TextDrawListener;
import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TextVisitFactory.class)
public abstract class MixinTextVisitFactory {

    @ModifyArg(method = {"visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z", ordinal = 0), index = 0)
    private static String vandalism$callTextDrawEvent(final String text) {
        final TextDrawListener.TextDrawEvent textDrawEvent = new TextDrawListener.TextDrawEvent(text);
        DietrichEvents2.global().postInternal(TextDrawListener.TextDrawEvent.ID, textDrawEvent);
        return textDrawEvent.text;
    }

}