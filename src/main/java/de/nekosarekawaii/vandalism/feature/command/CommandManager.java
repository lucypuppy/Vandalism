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

package de.nekosarekawaii.vandalism.feature.command;

import com.mojang.brigadier.CommandDispatcher;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.nekosarekawaii.vandalism.feature.command.impl.development.TestCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.exploit.*;
import de.nekosarekawaii.vandalism.feature.command.impl.misc.*;
import de.nekosarekawaii.vandalism.feature.command.impl.movement.ClipCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.movement.FlipCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.movement.HClipCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.movement.VClipCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.render.ClientsideGameModeCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.render.ClientsideInventoryClearCommand;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;

import java.util.UUID;

public class CommandManager extends Storage<AbstractCommand> implements MinecraftWrapper {

    public static final String COMMAND_SECRET = UUID.randomUUID().toString();

    private final CommandDispatcher<CommandSource> commandDispatcher = new CommandDispatcher<>();

    public CommandManager() {
        setAddConsumer(command -> command.publish(this.commandDispatcher));
    }

    @Override
    public void init() {
        add(
                new TestCommand(),
                new ChatClearCommand(),
                new ModuleCommand(),
                new ClientsideGameModeCommand(),
                new SayCommand(),
                new NbtCommand(),
                new CommandBlockStateCommand(),
                new PluginsCommand(),
                new GiveCommand(),
                new CopyServerIPCommand(),
                new CopyPositionCommand(),
                new TeleportEntitySpawnEggCommand(),
                new SoundEntitySpawnEggCommand(),
                new SoundHeadCommand(),
                new ClipCommand(),
                new VClipCommand(),
                new HClipCommand(),
                new ServerBrandCommand(),
                new SkriptDupeCommand(),
                new NavigateXCommand(),
                new ScriptCommand(),
                new ArmorCarryCommand(),
                new InventoryClearCommand(),
                new CopyInvisibleCharCommand(),
                new CopyUsernameCommand(),
                new CreativeItemControlCommand(),
                new EnchantCommand(),
                new FlipCommand(),
                new ClientsideInventoryClearCommand()
        );
    }

    public CommandDispatcher<CommandSource> getCommandDispatcher() {
        return commandDispatcher;
    }

    public ClickEvent generateClickEvent(final String command) {
        return new ClickEvent(ClickEvent.Action.RUN_COMMAND, COMMAND_SECRET + command);
    }

}
