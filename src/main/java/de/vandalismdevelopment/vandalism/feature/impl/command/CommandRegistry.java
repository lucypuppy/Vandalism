package de.vandalismdevelopment.vandalism.feature.impl.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.FeatureList;
import de.vandalismdevelopment.vandalism.feature.impl.command.impl.development.TestCommand;
import de.vandalismdevelopment.vandalism.feature.impl.command.impl.exploit.*;
import de.vandalismdevelopment.vandalism.feature.impl.command.impl.misc.*;
import de.vandalismdevelopment.vandalism.feature.impl.command.impl.movement.ClipCommand;
import de.vandalismdevelopment.vandalism.feature.impl.command.impl.movement.HClipCommand;
import de.vandalismdevelopment.vandalism.feature.impl.command.impl.movement.VClipCommand;
import de.vandalismdevelopment.vandalism.feature.impl.command.impl.render.ClientsideGameModeCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;

import java.util.UUID;

public class CommandRegistry {

    public final static String COMMAND_SECRET = UUID.randomUUID().toString();

    private final CommandDispatcher<CommandSource> commandDispatcher;
    private final CommandSource commandSource;

    private final FeatureList<Command> commands;

    public CommandRegistry() {
        this.commands = new FeatureList<>();
        this.commandDispatcher = new CommandDispatcher<>();
        this.commandSource = new ClientCommandSource(null, MinecraftClient.getInstance());
        this.register();
    }

    private void register() {
        this.registerCommands(
                new TestCommand(),
                new FeaturesCommand(),
                new ChatClearCommand(),
                new ToggleModuleCommand(),
                new ClientsideGameModeCommand(),
                new SayCommand(),
                new NBTCommand(),
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
                new NavigateXCommand()
        );
    }

    private void registerCommands(final Command... commands) {
        Vandalism.getInstance().getLogger().info("Registering commands...");
        for (final Command command : commands) {
            if (!this.commands.contains(command)) {
                for (final String alias : command.getAliases()) {
                    final LiteralArgumentBuilder<CommandSource> builder = LiteralArgumentBuilder.literal(alias);
                    command.build(builder);
                    this.commandDispatcher.register(builder);
                }
                this.commands.add(command);
                Vandalism.getInstance().getLogger().info("Command '" + command + "' has been registered.");
            } else {
                Vandalism.getInstance().getLogger().error("Duplicated command found: " + command);
            }
        }
        final int commandListSize = this.commands.size();
        if (commandListSize < 1) Vandalism.getInstance().getLogger().info("No commands found!");
        else Vandalism.getInstance().getLogger().info("Registered " + commandListSize + " command/s.");
    }

    public void commandDispatch(final String message) throws CommandSyntaxException {
        this.commandDispatcher.execute(message, this.commandSource);
    }

    public CommandDispatcher<CommandSource> getCommandDispatcher() {
        return this.commandDispatcher;
    }

    public CommandSource getCommandSource() {
        return this.commandSource;
    }

    public FeatureList<Command> getCommands() {
        return this.commands;
    }

    public ClickEvent generateClickEvent(final String command) {
        return new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                COMMAND_SECRET + command
        );
    }

}
