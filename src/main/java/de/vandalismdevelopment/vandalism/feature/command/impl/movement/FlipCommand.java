package de.vandalismdevelopment.vandalism.feature.command.impl.movement;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.command.AbstractCommand;
import net.minecraft.command.CommandSource;

public class FlipCommand extends AbstractCommand {

    public FlipCommand() {
        super(
                "Flip",
                "Flips you.",
                FeatureCategory.MISC,
                false,
                "flip",
                "selfflip"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            this.mc.player.setYaw(this.mc.player.getYaw() + 180);
            return SINGLE_SUCCESS;
        });
    }

}
