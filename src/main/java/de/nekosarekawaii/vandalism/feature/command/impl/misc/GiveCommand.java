package de.nekosarekawaii.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.util.minecraft.ItemStackUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ItemStackArgumentType;

public class GiveCommand extends AbstractCommand {

    public GiveCommand() {
        super("Gives you items when you are in creative mode.", Category.MISC, "give", "giveitem", "itemgive", "getitem", "itemget", "i");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("item", ItemStackArgumentType.itemStack(REGISTRY_ACCESS)).executes(context -> {
            ItemStackUtil.giveItemStack(ItemStackArgumentType.getItemStackArgument(context, "item").createStack(1, false));
            return SINGLE_SUCCESS;
        }).then(argument("number", IntegerArgumentType.integer()).executes(context -> {
            ItemStackUtil.giveItemStack(ItemStackArgumentType.getItemStackArgument(context, "item").createStack(IntegerArgumentType.getInteger(context, "number"), false));
            return SINGLE_SUCCESS;
        })));
    }

}