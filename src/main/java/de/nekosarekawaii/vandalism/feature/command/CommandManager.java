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

package de.nekosarekawaii.vandalism.feature.command;

import com.mojang.brigadier.CommandDispatcher;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.feature.command.impl.exploit.*;
import de.nekosarekawaii.vandalism.feature.command.impl.exploit.plugin.MultiverseCoreOptimizerCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.exploit.plugin.PluginsCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.exploit.plugin.SkriptDupeCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.misc.*;
import de.nekosarekawaii.vandalism.feature.command.impl.misc.copy.*;
import de.nekosarekawaii.vandalism.feature.command.impl.misc.module.ModuleBindCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.misc.module.ModuleShowBindCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.misc.module.ModuleToggleCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.misc.module.ModuleUnbindCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.movement.FlipCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.movement.clip.ClipCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.movement.clip.HClipCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.movement.clip.VClipCommand;
import de.nekosarekawaii.vandalism.feature.command.impl.render.ClientsideInventoryClearCommand;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import lombok.Getter;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;

import java.util.UUID;

@Getter
public class CommandManager extends Storage<AbstractCommand> implements MinecraftWrapper {

    public static final String COMMAND_SECRET = UUID.randomUUID().toString();

    private final CommandDispatcher<CommandSource> commandDispatcher = new CommandDispatcher<>();

    public CommandManager() {
        this.setAddConsumer(command -> command.publish(this.commandDispatcher));
    }

    @Override
    public void init() {
        if (FabricBootstrap.IS_DEV_ENVIRONMENT) {
            this.add(new TestCommand());
        }
        this.add(
                new ModuleToggleCommand(),
                new ModuleBindCommand(),
                new ModuleUnbindCommand(),
                new ModuleShowBindCommand(),
                new ChatClearCommand(),
                new FriendsCommand(),
                new ConfigCommand(),
                new SayCommand(),
                new NbtCommand(),
                new PluginsCommand(),
                new GiveCommand(),
                new CommandBlockStateCommand(),
                new CopyServerIPCommand(),
                new CopyServerBrandCommand(),
                new CopyServerVersionCommand(),
                new CopyPositionCommand(),
                new TeleportEntitySpawnEggCommand(),
                new SoundEntitySpawnEggCommand(),
                new SoundHeadCommand(),
                new ClipCommand(),
                new VClipCommand(),
                new HClipCommand(),
                new SkriptDupeCommand(),
                new NavigateXCommand(),
                new ArmorCarryCommand(),
                new InventoryClearCommand(),
                new CopyInvisibleCharCommand(),
                new UsernameCommand(),
                new EnchantCommand(),
                new FlipCommand(),
                new ClientsideInventoryClearCommand(),
                new DifficultyCommand(),
                //new ExploitBookCommand(), TODO: Fix Json
                new ModuleToggleCommand(),
                new MultiverseCoreOptimizerCommand(),
                new SkillSpamCommand(),
                new SpamCommand(),
                new CreateEntitySpawnEggCommand(),
                new CrashSkullCommand(),
                new KaboomKickCommand()
        );

        // This should always be the last command
        this.add(new HelpCommand());
    }

    public ClickEvent generateClickEvent(final String command) {
        return new ClickEvent(ClickEvent.Action.RUN_COMMAND, COMMAND_SECRET + command);
    }

}
