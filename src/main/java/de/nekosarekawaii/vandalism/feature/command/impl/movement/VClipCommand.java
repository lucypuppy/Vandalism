package de.nekosarekawaii.vandalism.feature.command.impl.movement;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.util.minecraft.MovementUtil;
import net.minecraft.command.CommandSource;

public class VClipCommand extends AbstractCommand {

    public VClipCommand() {
        super("Allows you to teleport yourself by vertical offset.", Category.MOVEMENT, "vclip", "vtp", "verticalteleport");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("vertical-offset", DoubleArgumentType.doubleArg(-10.0, 10.0)).executes(context -> {
            if (this.mc.player != null)
                MovementUtil.clip(
                        DoubleArgumentType.getDouble(context, "vertical-offset"),
                        0.0
                );

            return SINGLE_SUCCESS;
        }));
    }

}
