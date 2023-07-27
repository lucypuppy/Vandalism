package me.nekosarekawaii.foxglove.injection.mixins;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSignEditScreen.class)
public abstract class MixinAbstractSignEditScreen extends Screen {

    @Shadow
    protected abstract void setCurrentRowMessage(String message);

    protected MixinAbstractSignEditScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void injectInit(final CallbackInfo ci) {
        //TODO: Implement this -> Have a look at Formatting.FORMATTING_CODE_PATTERN
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Crash"),
                button -> this.setCurrentRowMessage("Not implemented xD")
        ).dimensions(10, 10, 70, 20).build());
    }

}
