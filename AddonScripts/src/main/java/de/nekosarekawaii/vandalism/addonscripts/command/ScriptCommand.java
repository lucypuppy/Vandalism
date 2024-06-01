/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.addonscripts.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.addonscripts.AddonScripts;
import de.nekosarekawaii.vandalism.addonscripts.base.Script;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.feature.command.arguments.KeyBindArgumentType;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import de.nekosarekawaii.vandalism.util.render.InputType;
import net.minecraft.command.CommandSource;

public class ScriptCommand extends AbstractCommand {

    public ScriptCommand() {
        super("Lets you execute, reload and bind scripts.", Category.MISC, "script");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("execute").then(argument("script", ScriptArgumentType.create()).executes(context -> {
            AddonScripts.getInstance().getScriptManager().executeScript(ScriptArgumentType.get(context).getUuid());
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("reload").executes(context -> {
            ChatUtil.infoChatMessage("Loading scripts...");
            AddonScripts.getInstance().getScriptManager().init();
            ChatUtil.infoChatMessage("Loaded " + AddonScripts.getInstance().getScriptManager().getList().size() + " scripts.");
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("bind").then(argument("script", ScriptArgumentType.create()).then(argument("key-bind", KeyBindArgumentType.create()).executes(context -> {
            final Script script = ScriptArgumentType.get(context);
            final int code = KeyBindArgumentType.get(context);
            script.getKeyBind().setValue(code);
            ChatUtil.infoChatMessage("Bound script " + script.getName() + " to " + InputType.getName(code) + ".");
            return SINGLE_SUCCESS;
        }))));
    }

}
