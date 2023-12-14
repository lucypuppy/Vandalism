package de.nekosarekawaii.vandalism.feature.command.impl.development;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.util.minecraft.ChatUtil;
import net.minecraft.command.CommandSource;

public class TestCommand extends AbstractCommand {

    public TestCommand() {
        super("Just for development purposes.", Category.DEVELOPMENT, "test");
        setExperimental(true);
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            ChatUtil.infoChatMessage("Executed Test Command.");
            return SINGLE_SUCCESS;
        });
    }

}
