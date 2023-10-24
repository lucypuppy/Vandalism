package de.vandalismdevelopment.vandalism.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.feature.impl.command.arguments.ScriptArgumentType;
import de.vandalismdevelopment.vandalism.util.ChatUtils;
import net.minecraft.command.CommandSource;

public class ScriptCommand extends Command {

    public ScriptCommand() {
        super(
                "Script",
                "Lets you execute and reload scripts.",
                FeatureCategory.MISC,
                false,
                "script"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("execute").then(argument("script", ScriptArgumentType.create())
                .executes(context -> {
                    Vandalism.getInstance().getScriptRegistry().executeScript(ScriptArgumentType.get(context));
                    return SINGLE_SUCCESS;
                })
        ));
        builder.then(literal("reload").executes(context -> {
            Vandalism.getInstance().getScriptRegistry().load();
            ChatUtils.infoChatMessage("Loaded " + Vandalism.getInstance().getScriptRegistry().getScripts().size() + " script/s.");
            return SINGLE_SUCCESS;
        }));
    }

}
