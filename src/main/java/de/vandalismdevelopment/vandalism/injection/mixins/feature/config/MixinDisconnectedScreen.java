package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.PlayerUtil;
import de.vandalismdevelopment.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DisconnectedScreen.class)
public abstract class MixinDisconnectedScreen extends Screen implements MinecraftWrapper {

    @Shadow
    @Final
    private Screen parent;

    protected MixinDisconnectedScreen(final Text ignored) {
        super(ignored);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (Vandalism.getInstance().getConfigManager().getMainConfig().menuCategory.disconnectedScreenEscaping.getValue()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                this.setScreen(this.parent);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/DirectionalLayoutWidget;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;", ordinal = 2))
    private <T extends Widget> T vandalism$addMoreButtons(final DirectionalLayoutWidget instance, final T widget) {
        instance.add(widget);
        if (Vandalism.getInstance().getConfigManager().getMainConfig().menuCategory.moreDisconnectedScreenButtons.getValue()) {
            final Positioner positioner = instance.getMainPositioner().copy().marginTop(-8);
            instance.add(ButtonWidget.builder(Text.literal("Reconnect"), button -> PlayerUtil.connectToLastServer()).build(), positioner);
            instance.add(ButtonWidget.builder(Text.literal("Copy Message"), button -> {
                final StringBuilder textBuilder = new StringBuilder(PlayerUtil.lastServerExists() ? "Disconnect Message from " + PlayerUtil.getLastServerInfo().address : "");
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
                if (!text.isBlank()) {
                    this.keyboard().setClipboard(text);
                }
            }).build(), positioner);
        }
        return widget;
    }

}
