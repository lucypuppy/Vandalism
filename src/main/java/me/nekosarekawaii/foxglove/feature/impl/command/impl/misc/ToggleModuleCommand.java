package me.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.nekosarekawaii.foxglove.feature.impl.command.Command;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import me.nekosarekawaii.foxglove.feature.impl.command.arguments.ModuleArgumentType;
import net.minecraft.command.CommandSource;

@CommandInfo(name = "Toggle Module", aliases = {"togglemodule", "toggle", "t"}, description = "This Command enables/disables modules.")
public class ToggleModuleCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder
                .then(argument("module", ModuleArgumentType.create())
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
