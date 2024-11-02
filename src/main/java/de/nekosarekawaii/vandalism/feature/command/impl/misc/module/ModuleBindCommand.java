/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.feature.command.impl.misc.module;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.command.Command;
import de.nekosarekawaii.vandalism.feature.command.arguments.KeyBindArgumentType;
import de.nekosarekawaii.vandalism.feature.command.arguments.ModuleArgumentType;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.render.util.InputType;
import net.minecraft.command.CommandSource;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ModuleBindCommand extends Command {

    public ModuleBindCommand() {
        super("Lets you bind modules.", Category.MISC, "modulebind", "bind");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", ModuleArgumentType.create()).then(argument("key-bind", KeyBindArgumentType.create()).executes(context -> {
            final Module module = ModuleArgumentType.get(context);
            final int code = KeyBindArgumentType.get(context);
            if (code == GLFW.GLFW_KEY_UNKNOWN && !module.getKeyBind().isValid()) {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is not bound.");
                return SINGLE_SUCCESS;
            }
            if (module.getKeyBind().getValue() == code) {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is already bound to " + InputType.getName(code) + ".");
                return SINGLE_SUCCESS;
            }
            final List<String> boundModules = new ArrayList<>();
            for (final Module mod : Vandalism.getInstance().getModuleManager().getList()) {
                if (mod.getKeyBind().getValue() == code) {
                    boundModules.add(mod.getName());
                }
            }
            module.getKeyBind().setValue(code);
            if (code == GLFW.GLFW_KEY_UNKNOWN) {
                ChatUtil.infoChatMessage("Unbound module " + module.getName() + ".");
                return SINGLE_SUCCESS;
            }
            ChatUtil.infoChatMessage("Bound module " + module.getName() + " to " + InputType.getName(code) + ".");
            if (!boundModules.isEmpty()) {
                ChatUtil.warningChatMessage("The following modules are already bound to " + InputType.getName(code) + ":");
                for (final String boundModule : boundModules) {
                    ChatUtil.warningChatMessage(" - " + boundModule);
                }
            }
            return SINGLE_SUCCESS;
        })));
    }

}
