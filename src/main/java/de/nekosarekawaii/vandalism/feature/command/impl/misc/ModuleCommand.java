/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.feature.command.arguments.KeyBindArgumentType;
import de.nekosarekawaii.vandalism.feature.command.arguments.ModuleArgumentType;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import de.nekosarekawaii.vandalism.util.render.InputType;
import net.minecraft.command.CommandSource;
import org.lwjgl.glfw.GLFW;

public class ModuleCommand extends AbstractCommand {

    public ModuleCommand() {
        super("Lets you toggle and bind modules.", Category.MISC, "module");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("toggle").then(argument("module", ModuleArgumentType.create()).executes(context -> {
            ModuleArgumentType.get(context).toggle();
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("show-bind").then(argument("module", ModuleArgumentType.create()).executes(context -> {
            final AbstractModule module = ModuleArgumentType.get(context);
            if (module.getKeyBind().isValid()) {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is bound to " + InputType.getName(module.getKeyBind().getValue()) + ".");
            } else {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is not bound.");
            }
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("unbind").then(argument("module", ModuleArgumentType.create()).executes(context -> {
            final AbstractModule module = ModuleArgumentType.get(context);
            if (module.getKeyBind().isValid()) {
                module.getKeyBind().resetValue();
                ChatUtil.infoChatMessage("Unbound module " + module.getName() + ".");
            } else {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is not bound.");
            }

            return SINGLE_SUCCESS;
        })));
        builder.then(literal("bind").then(argument("module", ModuleArgumentType.create()).then(argument("key-bind", KeyBindArgumentType.create()).executes(context -> {
            final AbstractModule module = ModuleArgumentType.get(context);
            final Integer code = KeyBindArgumentType.get(context);
            if (code == GLFW.GLFW_KEY_UNKNOWN && !module.getKeyBind().isValid()) {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is not bound.");
                return SINGLE_SUCCESS;
            }
            if (code.equals(module.getKeyBind().getValue())) {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is already bound to " + InputType.getName(code) + ".");
                return SINGLE_SUCCESS;
            }
            module.getKeyBind().setValue(code);
            if (code == GLFW.GLFW_KEY_UNKNOWN) {
                ChatUtil.infoChatMessage("Unbound module " + module.getName() + ".");
                return SINGLE_SUCCESS;
            }
            ChatUtil.infoChatMessage("Bound module " + module.getName() + " to " + InputType.getName(code) + ".");
            for (final AbstractModule mod : Vandalism.getInstance().getModuleManager().getList()) {
                if (mod.getKeyBind().getValue().equals(code) && !mod.getName().equals(module.getName())) {
                    ChatUtil.warningChatMessage("Module " + mod.getName() + " is also bound to " + InputType.getName(code) + ".");
                }
            }
            return SINGLE_SUCCESS;
        }))));
    }

}
