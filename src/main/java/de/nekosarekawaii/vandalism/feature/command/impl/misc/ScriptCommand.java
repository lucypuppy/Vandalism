package de.nekosarekawaii.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.feature.command.arguments.KeyNameArgumentType;
import de.nekosarekawaii.vandalism.feature.command.arguments.ScriptArgumentType;
import de.nekosarekawaii.vandalism.feature.script.Script;
import de.nekosarekawaii.vandalism.util.minecraft.ChatUtil;
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
