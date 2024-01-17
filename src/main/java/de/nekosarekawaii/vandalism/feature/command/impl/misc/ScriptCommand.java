/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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
import de.nekosarekawaii.vandalism.feature.command.arguments.KeyNameArgumentType;
import de.nekosarekawaii.vandalism.feature.command.arguments.ScriptArgumentType;
import de.nekosarekawaii.vandalism.feature.script.Script;
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
            Vandalism.getInstance().getScriptManager().executeScript(ScriptArgumentType.get(context).getUuid());
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("reload").executes(context -> {
            ChatUtil.infoChatMessage("Loading scripts...");
            Vandalism.getInstance().getScriptManager().init();
            ChatUtil.infoChatMessage("Loaded " + Vandalism.getInstance().getScriptManager().getList().size() + " scripts.");
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("bind").then(argument("script", ScriptArgumentType.create()).then(argument("key-name", KeyNameArgumentType.create()).executes(context -> {
            final Script script = ScriptArgumentType.get(context);
            final int keyCode = KeyNameArgumentType.get(context);
            script.getKeyBind().setValue(keyCode);
            ChatUtil.infoChatMessage("Bound script " + script.getName() + " to key " + InputType.getKeyName(keyCode) + ".");
            return SINGLE_SUCCESS;
        }))));
    }

}
