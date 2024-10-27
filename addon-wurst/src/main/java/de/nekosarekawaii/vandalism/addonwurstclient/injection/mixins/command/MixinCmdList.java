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

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.Feature;
import de.nekosarekawaii.vandalism.feature.command.Command;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import net.minecraft.command.CommandSource;
import net.wurstclient.WurstClient;
import net.wurstclient.command.CmdList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.TreeMap;

@Mixin(value = CmdList.class)
public abstract class MixinCmdList {

    @Shadow
    @Final
    private TreeMap<String, net.wurstclient.command.Command> cmds;

    @Unique
    private void vandalism$runWurstCommand(final String input) {
        if (WurstClient.INSTANCE.isEnabled()) {
            WurstClient.INSTANCE.getCmdProcessor().process(input.replaceFirst("wurst ", ""));
        } else {
            ChatUtil.errorChatMessage("Wurst Client is not enabled!");
        }
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"), remap = false)
    private void registerWurstCommands(final CallbackInfo ci) {
        Vandalism.getInstance().getCommandManager().add(new Command("Wurst Client command implementation.", Feature.Category.MISC, "wurst") {

            @Override
            public void build(final LiteralArgumentBuilder<CommandSource> builder) {
                for (final net.wurstclient.command.Command command : cmds.values()) {
                    final String commandName = command.getName().substring(1);
                    builder.then(literal(commandName).then(argument("input", StringArgumentType.greedyString()).executes(context -> {
                        vandalism$runWurstCommand(commandName + " " + StringArgumentType.getString(context, "input"));
                        return SINGLE_SUCCESS;
                    })));
                }
            }

        });
    }

}
