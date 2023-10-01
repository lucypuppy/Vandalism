package de.vandalismdevelopment.vandalism.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.feature.impl.command.arguments.ModuleArgumentType;
import net.minecraft.command.CommandSource;

public class ToggleModuleCommand extends Command {

    public ToggleModuleCommand() {
        super(
                "Toggle Module",
                "Toggle a module by its name.",
                FeatureCategory.MISC,
                false,
                "togglemodule",
                "toggle",
                "tm",
                "t"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", ModuleArgumentType.create())
                .executes(context -> {
                    ModuleArgumentType.get(context).toggle();
                    return SINGLE_SUCCESS;
                })
                .then(literal("on")
                        .executes(context -> {
                            ModuleArgumentType.get(context).enable();
                            return SINGLE_SUCCESS;
                        }))
                .then(literal("off")
                        .executes(context -> {
                            ModuleArgumentType.get(context).disable();
                            return SINGLE_SUCCESS;
                        })
                )
        );
    }

}
