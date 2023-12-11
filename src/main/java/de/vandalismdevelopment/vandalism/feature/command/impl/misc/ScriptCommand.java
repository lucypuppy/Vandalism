package de.vandalismdevelopment.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.command.AbstractCommand;
import de.vandalismdevelopment.vandalism.feature.command.arguments.ScriptArgumentType;
import de.vandalismdevelopment.vandalism.feature.script.Script;
import de.vandalismdevelopment.vandalism.util.minecraft.ChatUtil;
import net.minecraft.command.CommandSource;

public class ScriptCommand extends AbstractCommand {

    public ScriptCommand() {
        super(
                "Script",
                "Lets you execute, reload and bind scripts.",
                FeatureCategory.MISC,
                false,
                "script"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("execute").then(argument("script", ScriptArgumentType.create())
                .executes(context -> {
                    Vandalism.getInstance().getScriptRegistry().executeScriptByScriptFile(ScriptArgumentType.get(context).getFile());
                    return SINGLE_SUCCESS;
                })
        ));

        builder.then(literal("reload").executes(context -> {
            ChatUtil.infoChatMessage("Loading scripts...");
            Vandalism.getInstance().getScriptRegistry().load();
            ChatUtil.infoChatMessage("Loaded " + Vandalism.getInstance().getScriptRegistry().getScripts().size() + " scripts.");
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("bind").then(argument("script", ScriptArgumentType.create())
                .then(argument("glfwkeyname", GlfwKeyNameArgumentType.create())
                        .executes(context -> {
                            final Script script = ScriptArgumentType.get(context);
                            final GlfwKeyName glfwKeyName = GlfwKeyNameArgumentType.get(context);
                            script.setKeyBind(glfwKeyName);
                            ChatUtil.infoChatMessage(
                                    "Bound script " + script.getName() + " to key " + glfwKeyName.normalName() + "."
                            );
                            return SINGLE_SUCCESS;
                        })
                )));
    }

}
