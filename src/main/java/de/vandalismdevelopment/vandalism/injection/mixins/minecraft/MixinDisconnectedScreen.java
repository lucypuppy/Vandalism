package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.vandalismdevelopment.vandalism.util.ServerUtil;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DisconnectedScreen.class)
public abstract class MixinDisconnectedScreen extends Screen {

    protected MixinDisconnectedScreen(final Text ignored) {
        super(ignored);
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/DirectionalLayoutWidget;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;", ordinal = 2))
    private <T extends Widget> T redirectInit(final DirectionalLayoutWidget instance, final T widget) {
        instance.add(widget);
        final Positioner positioner = instance.getMainPositioner().copy().marginTop(-8);
        instance.add(ButtonWidget.builder(Text.literal("Reconnect"), button -> ServerUtil.connectToLastServer()).build(), positioner);
        instance.add(ButtonWidget.builder(Text.literal("Copy Text"), button -> {
            final StringBuilder textBuilder = new StringBuilder(ServerUtil.lastServerExists() ? "Disconnect Message from " + ServerUtil.getLastServerInfo().address : "");
            final String emptyLine = "\n\n";
            textBuilder.append(emptyLine);
            instance.forEachElement(w -> {
                if (w instanceof final TextWidget textWidget) {
                    textBuilder.append("[Title]").append(emptyLine).append(textWidget.getMessage().getString());
                } else if (w instanceof final MultilineTextWidget multilineTextWidget) {
                    textBuilder.append(emptyLine).append("[Reason]").append(emptyLine).append(multilineTextWidget.getMessage().getString());
                }
            });
            final String text = textBuilder.toString();
            if (client != null && !text.isBlank()) {
                client.keyboard.setClipboard(text);
            }
        }).build(), positioner);
        return widget;
    }

}
