package me.nekosarekawaii.foxglove.mixin.net.minecraft.client.gui.screen;

import me.nekosarekawaii.foxglove.util.LastServerUtils;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DisconnectedScreen.class)
public abstract class MixinDisconnectedScreen extends Screen {

    protected MixinDisconnectedScreen(final Text ignored) {
        super(ignored);
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget$Adder;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;", ordinal = 2))
    private <T extends Widget> T redirectInit(final GridWidget.Adder instance, final T widget) {
        instance.add(widget);
        instance.add(ButtonWidget.builder(Text.literal("Reconnect"), button -> LastServerUtils.connectToLastServer()).width(70).build(), instance.getMainPositioner().copy().marginTop(-8));
        return widget;
    }

}
