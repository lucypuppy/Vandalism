package me.nekosarekawaii.foxglove.feature.impl.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.wrapper.MinecraftWrapper;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;

/**
 * The CommandHandler class is responsible for handling and executing commands in the Foxglove mod.
 * It utilizes the CommandDispatcher from the Brigadier library to register and dispatch commands.
 */
public final class CommandHandler implements MinecraftWrapper {

    private CommandDispatcher<CommandSource> commandDispatcher;
    private CommandSource commandSource;

    /**
     * Constructs a new CommandHandler object and registers the commands.
     */
    public CommandHandler() {
        this.register();
    }

    /**
     * Registers the commands by creating a CommandDispatcher, initializing a CommandSource,
     * and registering the commands with their aliases.
     */
    public void register() {
        this.commandDispatcher = new CommandDispatcher<>();
        this.commandSource = new ClientCommandSource(null, mc());
        for (final Command command : Foxglove.getInstance().getFeatures().getCommands()) {
            for (final String alias : command.getAliases()) {
                final LiteralArgumentBuilder<CommandSource> builder = LiteralArgumentBuilder.literal(alias);
                command.build(builder);
                this.commandDispatcher.register(builder);
            }
        }
    }

    /**
     * Executes a command by dispatching it to the CommandDispatcher.
     *
     * @param message The command string to be executed.
     * @throws CommandSyntaxException If an error occurs during command execution.
     */
    public void commandDispatch(final String message) throws CommandSyntaxException {
        this.commandDispatcher.execute(message, this.commandSource);
    }

    /**
     * Returns the CommandDispatcher used for registering and dispatching commands.
     *
     * @return The CommandDispatcher used for commands.
     */
    public CommandDispatcher<CommandSource> getCommandDispatcher() {
        return this.commandDispatcher;
    }

    /**
     * Returns the CommandSource used for executing commands.
     *
     * @return The CommandSource used for command execution.
     */
    public CommandSource getCommandSource() {
        return this.commandSource;
    }

}
