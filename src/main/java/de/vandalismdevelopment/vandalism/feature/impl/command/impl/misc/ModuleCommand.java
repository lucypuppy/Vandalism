package de.vandalismdevelopment.vandalism.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.feature.impl.command.arguments.GlfwKeyNameArgumentType;
import de.vandalismdevelopment.vandalism.feature.impl.command.arguments.ModuleArgumentType;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.util.GlfwKeyName;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.ChatUtil;
import net.minecraft.command.CommandSource;

public class ModuleCommand extends Command {

    public ModuleCommand() {
        super("Module", "Lets you toggle and bind modules.", FeatureCategory.MISC, false, "module");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("toggle").then(argument("module", ModuleArgumentType.create()).executes(context -> {
            ModuleArgumentType.get(context).toggle();
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("show-bind").then(argument("module", ModuleArgumentType.create()).executes(context -> {
            final Module module = ModuleArgumentType.get(context);
            if (module.getKeyBind() == GlfwKeyName.UNKNOWN) {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is not bound.");
            } else {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is bound to the key " + module.getKeyBind().normalName() + ".");
            }
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("unbind").then(argument("module", ModuleArgumentType.create()).executes(context -> {
            final Module module = ModuleArgumentType.get(context);
            if (module.getKeyBind() == GlfwKeyName.UNKNOWN) {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is not bound.");
                return SINGLE_SUCCESS;
            }
            module.setKeyBind(GlfwKeyName.UNKNOWN);
            ChatUtil.infoChatMessage("Unbound module " + module.getName() + ".");
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("bind").then(argument("module", ModuleArgumentType.create()).then(argument("glfwkeyname", GlfwKeyNameArgumentType.create()).executes(context -> {
            final Module module = ModuleArgumentType.get(context);
            final GlfwKeyName glfwKeyName = GlfwKeyNameArgumentType.get(context);
            if (glfwKeyName == GlfwKeyName.UNKNOWN && module.getKeyBind() == GlfwKeyName.UNKNOWN) {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is not bound.");
                return SINGLE_SUCCESS;
            }
            if (glfwKeyName == module.getKeyBind()) {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is already bound to the key " + glfwKeyName.normalName() + ".");
                return SINGLE_SUCCESS;
            }
            module.setKeyBind(glfwKeyName);
            if (glfwKeyName == GlfwKeyName.UNKNOWN) {
                ChatUtil.infoChatMessage("Unbound module " + module.getName() + ".");
                return SINGLE_SUCCESS;
            }
            ChatUtil.infoChatMessage("Bound module " + module.getName() + " to the key " + glfwKeyName.normalName() + ".");
            for (final Module mod : Vandalism.getInstance().getModuleRegistry().getModules()) {
                if (mod.getKeyBind().equals(glfwKeyName) && !mod.getName().equals(module.getName())) {
                    ChatUtil.warningChatMessage("Module " + mod.getName() + " is also bound to the key " + glfwKeyName.normalName() + ".");
                }
            }
            return SINGLE_SUCCESS;
        }))));
    }

}
