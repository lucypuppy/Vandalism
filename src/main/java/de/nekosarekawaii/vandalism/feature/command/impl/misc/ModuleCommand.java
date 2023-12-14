package de.nekosarekawaii.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.feature.command.arguments.KeyNameArgumentType;
import de.nekosarekawaii.vandalism.feature.command.arguments.ModuleArgumentType;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.minecraft.ChatUtil;
import de.nekosarekawaii.vandalism.util.render.InputType;
import net.minecraft.command.CommandSource;
import org.lwjgl.glfw.GLFW;

public class ModuleCommand extends AbstractCommand {

    public ModuleCommand() {
        super("Lets you toggle and bind modules.", Category.MISC, "module");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("toggle").then(argument("module", ModuleArgumentType.create()).executes(context -> {
            ModuleArgumentType.get(context).toggle();
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("show-bind").then(argument("module", ModuleArgumentType.create()).executes(context -> {
            final AbstractModule module = ModuleArgumentType.get(context);
            if (module.getKeyBind().isValid()) {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is bound to the key " + module.getKeyBind() + ".");
            } else {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is not bound.");
            }

            return SINGLE_SUCCESS;
        })));

        builder.then(literal("unbind").then(argument("module", ModuleArgumentType.create()).executes(context -> {
            final AbstractModule module = ModuleArgumentType.get(context);
            if (module.getKeyBind().isValid()) {
                module.getKeyBind().resetValue();
                ChatUtil.infoChatMessage("Unbound module " + module.getName() + ".");
            } else {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is not bound.");
            }

            return SINGLE_SUCCESS;
        })));

        builder.then(literal("bind").then(argument("module", ModuleArgumentType.create()).then(argument("key-name", KeyNameArgumentType.create()).executes(context -> {
            final AbstractModule module = ModuleArgumentType.get(context);
            final Integer keyCode = KeyNameArgumentType.get(context);
            if (keyCode == GLFW.GLFW_KEY_UNKNOWN && !module.getKeyBind().isValid()) {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is not bound.");
                return SINGLE_SUCCESS;
            }

            if (keyCode.equals(module.getKeyBind().getValue())) {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is already bound to the key " + InputType.getKeyName(keyCode) + ".");
                return SINGLE_SUCCESS;
            }

            module.getKeyBind().setValue(keyCode);
            if (keyCode == GLFW.GLFW_KEY_UNKNOWN) {
                ChatUtil.infoChatMessage("Unbound module " + module.getName() + ".");
                return SINGLE_SUCCESS;
            }

            ChatUtil.infoChatMessage("Bound module " + module.getName() + " to the key " + InputType.getKeyName(keyCode) + ".");
            for (final AbstractModule mod : Vandalism.getInstance().getModuleManager().getList()) {
                if (mod.getKeyBind().getValue().equals(keyCode) && !mod.getName().equals(module.getName())) {
                    ChatUtil.warningChatMessage("Module " + mod.getName() + " is also bound to the key " + InputType.getKeyName(keyCode) + ".");
                }
            }

            return SINGLE_SUCCESS;
        }))));
    }

}
