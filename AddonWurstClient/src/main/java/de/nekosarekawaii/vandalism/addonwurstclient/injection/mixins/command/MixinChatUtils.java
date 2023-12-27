package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.command;

import de.florianmichael.rclasses.common.StringUtils;
import de.nekosarekawaii.vandalism.Vandalism;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.wurstclient.WurstClient;
import net.wurstclient.command.Command;
import net.wurstclient.util.ChatUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChatUtils.class, remap = false)
public abstract class MixinChatUtils {

    @Redirect(method = "message", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;literal(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"))
    private static MutableText fixWurstCommandPrefix(String input) {
        for (final Command cmd : WurstClient.INSTANCE.getCmds().getAllCmds()) {
            final String cmdName = cmd.getName();
            if (StringUtils.contains(input, cmdName)) {
                input = StringUtils.replaceAll(
                        input,
                        cmdName,
                        Vandalism.getInstance().getClientSettings().getChatSettings().commandPrefix.getValue() +
                                "wurst" + cmdName.substring(1)
                );
            }
        }
        return Text.literal(input);
    }

}
