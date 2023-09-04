package de.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.FeatureList;
import de.nekosarekawaii.foxglove.feature.impl.command.Command;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.util.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;

public class FeaturesCommand extends Command {

    public FeaturesCommand() {
        super(
                "Features",
                "Shows you the features of this mod and all their description.",
                FeatureCategory.MISC,
                false,
                "features",
                "featureslist",
                "help"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder
                .executes(context -> {
                    this.displayModules();
                    this.displayCommands();
                    return singleSuccess;
                })
                .then(literal("all")
                        .executes(context -> {
                            this.displayModules();
                            this.displayCommands();
                            return singleSuccess;
                        })
                )
                .then(literal("modules")
                        .executes(context -> {
                            this.displayModules();
                            return singleSuccess;
                        })
                )
                .then(literal("commands")
                        .executes(context -> {
                            this.displayCommands();
                            return singleSuccess;
                        })
                );
    }

    private void displayModules() {
        final FeatureList<Module> modules = Foxglove.getInstance().getModuleRegistry().getModules();
        if (modules.isEmpty()) {
            ChatUtils.errorChatMessage("No modules are registered!");
            return;
        }
        final int moduleSize = modules.size();
        final StringBuilder moduleStringBuilder = new StringBuilder()
                .append(Formatting.DARK_AQUA)
                .append(Formatting.UNDERLINE)
                .append("Modules (")
                .append(moduleSize)
                .append(')')
                .append(Formatting.RESET)
                .append('\n');
        for (int i = 0; i < moduleSize; i++) {
            final int number = i + 1;
            final Module module = modules.get(i);
            moduleStringBuilder
                    .append('\n')
                    .append(Formatting.GRAY)
                    .append(number < 10 ? "0" : "")
                    .append(number)
                    .append(Formatting.DARK_GRAY)
                    .append(". ")
                    .append(Formatting.RESET)
                    .append(module.isExperimental() ? "Experimental > " : "")
                    .append(module.getCategory().normalName())
                    .append(" > ")
                    .append(Formatting.WHITE)
                    .append(module.getName())
                    .append(Formatting.DARK_GRAY)
                    .append(" | ")
                    .append(Formatting.LIGHT_PURPLE)
                    .append(module.getDescription())
                    .append(Formatting.RESET);
        }
        ChatUtils.emptyChatMessage();
        ChatUtils.infoChatMessage(moduleStringBuilder.toString());
    }

    private void displayCommands() {
        final FeatureList<Command> commands = Foxglove.getInstance().getCommandRegistry().getCommands();
        final int commandSize = commands.size();
        if (commandSize - 1 < 1) {
            ChatUtils.errorChatMessage("No commands are registered!");
            return;
        }
        final StringBuilder commandStringBuilder = new StringBuilder()
                .append(Formatting.DARK_AQUA)
                .append(Formatting.UNDERLINE)
                .append("Commands (")
                .append(commandSize)
                .append(')')
                .append(Formatting.RESET)
                .append('\n');

        for (int i = 0; i < commandSize; i++) {
            final int number = i + 1;
            final Command command = commands.get(i);
            commandStringBuilder
                    .append('\n')
                    .append(Formatting.GRAY)
                    .append(number < 10 ? "0" : "")
                    .append(number)
                    .append(Formatting.DARK_GRAY)
                    .append(". ")
                    .append(Formatting.RESET)
                    .append(command.isExperimental() ? "Experimental > " : "")
                    .append(command.getCategory().normalName())
                    .append(" > ")
                    .append(Formatting.WHITE)
                    .append(command.getName())
                    .append(Formatting.DARK_GRAY)
                    .append(" | ")
                    .append(Formatting.LIGHT_PURPLE)
                    .append(command.getDescription())
                    .append(' ')
                    .append(
                            command.getAliasesString()
                                    .replace("[", Formatting.DARK_GRAY + "[" + Formatting.YELLOW + Foxglove.getInstance().getConfigManager().getMainConfig().commandPrefix.getValue() + Formatting.GOLD)
                                    .replace("]", Formatting.DARK_GRAY + "]")
                                    .replace(", ", Formatting.DARK_GRAY + ", " + Formatting.YELLOW + Foxglove.getInstance().getConfigManager().getMainConfig().commandPrefix.getValue() + Formatting.GOLD)
                    )
                    .append(Formatting.RESET);
        }
        ChatUtils.emptyChatMessage();
        ChatUtils.infoChatMessage(commandStringBuilder.toString());
    }

}
