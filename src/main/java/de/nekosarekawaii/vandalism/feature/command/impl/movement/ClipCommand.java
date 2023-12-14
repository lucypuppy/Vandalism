package de.nekosarekawaii.vandalism.feature.command.impl.movement;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.util.minecraft.MovementUtil;
import net.minecraft.command.CommandSource;

public class ClipCommand extends AbstractCommand {

    public ClipCommand() {
        super("Allows you to teleport yourself by vertical and horizontal offset.", Category.MOVEMENT, "clip", "tp", "teleport");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.
                then(argument("vertical-offset", DoubleArgumentType.doubleArg(-10.0, 10.0)).
                        then(argument("horizontal-offset", DoubleArgumentType.doubleArg(-10.0, 10.0)).
                                executes(context -> {
                                    if (this.mc.player != null)
                                        MovementUtil.clip(
                                                DoubleArgumentType.getDouble(context, "vertical-offset"),
                                                DoubleArgumentType.getDouble(context, "horizontal-offset")
                                        );

                                    return SINGLE_SUCCESS;
                                })));
    }

}
