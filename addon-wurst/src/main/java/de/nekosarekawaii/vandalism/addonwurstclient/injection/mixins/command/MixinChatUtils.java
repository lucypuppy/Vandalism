/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.command;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.util.StringUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.wurstclient.WurstClient;
import net.wurstclient.command.Command;
import net.wurstclient.util.ChatUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChatUtils.class)
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
                                "wurst " + cmdName.substring(1)
                );
            }
        }
        return Text.literal(input);
    }

}
