package me.nekosarekawaii.foxglove.feature.impl.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.FeatureList;
import me.nekosarekawaii.foxglove.feature.impl.command.impl.development.ReloadCommand;
import me.nekosarekawaii.foxglove.feature.impl.command.impl.development.TestCommand;
import me.nekosarekawaii.foxglove.feature.impl.command.impl.misc.ChatClearCommand;
import me.nekosarekawaii.foxglove.feature.impl.command.impl.misc.FeaturesCommand;
import me.nekosarekawaii.foxglove.feature.impl.command.impl.misc.ToggleModuleCommand;
import me.nekosarekawaii.foxglove.wrapper.MinecraftWrapper;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;

public class CommandRegistry implements MinecraftWrapper {

    private CommandDispatcher<CommandSource> commandDispatcher;
    private CommandSource commandSource;

    private final FeatureList<Command> commands;

    public CommandRegistry() {
        this.commands = new FeatureList<>();
        this.register();
    }

    public void reload() {
        this.commands.clear();
        this.register();
    }

    private void register() {
        this.commandDispatcher = new CommandDispatcher<>();
        this.commandSource = new ClientCommandSource(null, mc());
        this.registerCommands(
                new TestCommand(),
                new ReloadCommand(),
                new FeaturesCommand(),
                new ChatClearCommand(),
                new ToggleModuleCommand()
        );
    }

    private void registerCommands(final Command... commands) {
        for (final Command command : commands) {
            if (command.getClass().isAnnotationPresent(CommandInfo.class)) {
                if (!this.commands.contains(command)) {
                    for (final String alias : command.getAliases()) {
                        final LiteralArgumentBuilder<CommandSource> builder = LiteralArgumentBuilder.literal(alias);
                        command.build(builder);
                        this.commandDispatcher.register(builder);
                    }
                    this.commands.add(command);
                    Foxglove.getInstance().getLogger().info("Command '" + command + "' has been registered.");
                } else {
                    Foxglove.getInstance().getLogger().error("Duplicated Command found: " + command);
                }
            } else {
                Foxglove.getInstance().getLogger().error("Command '" + command + "' is not annotated with Command Info!");
            }
        }
        final int commandListSize = this.commands.size();
        if (commandListSize < 1) Foxglove.getInstance().getLogger().info("No Commands found!");
        else Foxglove.getInstance().getLogger().info("Registered " + commandListSize + " Command/s.");
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

}
