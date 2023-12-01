package de.vandalismdevelopment.vandalism.feature.impl.command.impl.misc;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.feature.impl.command.arguments.EnchantmentArgumentType;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.ChatUtil;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.ItemStackUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

public class EnchantCommand extends Command {

    public EnchantCommand() {
        super(
                "Enchant",
                "Enchants the item in your main hand with the specified enchantment.",
                FeatureCategory.MISC,
                false,
                "enchant",
                "enchantitem",
                "itemenchant"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("enchantment", EnchantmentArgumentType.create())
                .then(argument("level", IntegerArgumentType.integer(Short.MIN_VALUE, Short.MAX_VALUE))
                        .executes(context -> {
                                    this.enchantItem(EnchantmentArgumentType.get(context), IntegerArgumentType.getInteger(context, "level"));
                                    return SINGLE_SUCCESS;
                                }
                        )
                )
        );
    }

    private void enchantItem(final Enchantment enchantment, final int level) {
        final ItemStack stack = this.player().getInventory().getMainHandStack();
        if (stack != null && !stack.isEmpty()) {
            if (ItemStackUtil.giveItemStack(ItemStackUtil.appendEnchantmentToItemStack(stack, enchantment, level), false)) {
                ChatUtil.infoChatMessage("Enchanted the item in your main hand.");
            } else ChatUtil.errorChatMessage("Failed to enchant the item in your main hand.");
        } else ChatUtil.errorChatMessage("You must hold an item in your main hand.");
    }

}
