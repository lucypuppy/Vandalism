package de.nekosarekawaii.vandalism.injection.mixins.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.feature.command.CommandManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Screen.class, priority = 9999)
public abstract class MixinScreen {

    @Inject(method = "handleTextClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/SharedConstants;stripInvalidChars(Ljava/lang/String;)Ljava/lang/String;", ordinal = 1), cancellable = true)
    private void vandalism$executeModCommands(final Style style, final CallbackInfoReturnable<Boolean> cir) {
        final ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent != null) {
            if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                final String value = clickEvent.getValue(), secret = CommandManager.COMMAND_SECRET;
                if (value.startsWith(secret)) {
                    try {
                        Vandalism.getInstance().getCommandManager().getCommandDispatcher().execute(value.replaceFirst(secret, ""), AbstractCommand.COMMAND_SOURCE);
                        cir.setReturnValue(true);
                    } catch (final CommandSyntaxException e) {
                        Vandalism.getInstance().getLogger().error("Failed to run command.", e);
                    }
                }
            }
        }
    }

}