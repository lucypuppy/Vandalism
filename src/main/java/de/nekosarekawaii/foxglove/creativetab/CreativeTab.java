package de.nekosarekawaii.foxglove.creativetab;

import de.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class CreativeTab {

    private final ItemStack icon;
    private ItemGroup itemGroup;

    public CreativeTab(final ItemStack icon) {
        this.icon = icon;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public void setItemGroup(final ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

    public ItemGroup getItemGroup() {
        return this.itemGroup;
    }

    public Collection<ItemStack> entries() {
        return ItemStackSet.create();
    }

    public ItemStack createItem(final ItemStack stack, final Text name, @Nullable final Text... description) {
        return createItem(stack, name, false, null, description);
    }

    public ItemStack createItem(final ItemStack stack, final Text name, @Nullable final String author, @Nullable final Text... description) {
        return createItem(stack, name, false, author, description);
    }

    public ItemStack createItem(final ItemStack stack, final Text name, final boolean glint) {
        return createItem(stack, name, glint, null);
    }

    public ItemStack createItem(final ItemStack stack, final Text name, final boolean glint, @Nullable final String author, @Nullable final Text... description) {
        final var registry = Foxglove.getInstance().getCreativeTabRegistry();
        final var base = stack.getOrCreateNbt();
        base.put(registry.getClientsideName(), new NbtCompound());
        if (glint) base.put(registry.getClientsideGlint(), new NbtCompound());
        stack.setCustomName(name);
        if (description != null || author != null) {
            final var lore = new NbtList();
            if (author != null) {
                lore.add(NbtString.of(Text.Serializer.toJson(Text.of(Formatting.AQUA + Formatting.BOLD.toString() + "Author" + Formatting.DARK_GRAY + ": " + Formatting.GOLD + Formatting.BOLD + author))));
            }
            if (description != null) {
                for (final Text text : description) {
                    if (text != null) lore.add(NbtString.of(Text.Serializer.toJson(text)));
                }
            }
            stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.LORE_KEY, lore);
        }
        return stack;
    }

    public NbtCompound createEffect(final int id, final int duration, final int amplifier, final boolean showParticles) {
        final var effect = new NbtCompound();
        effect.putInt("Id", id);
        effect.putByte("ShowParticles", showParticles ? (byte) 1 : (byte) 0);
        effect.putInt("Duration", duration);
        effect.putByte("Amplifier", (byte) amplifier);
        return effect;
    }

}
