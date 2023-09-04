package de.nekosarekawaii.foxglove.creativetab;

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
    private final Text displayName;
    private ItemGroup itemGroup;

    public CreativeTab(final Text displayName, final ItemStack icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public Text getDisplayName() {
        return this.displayName;
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
        final NbtCompound base = stack.getOrCreateNbt();
        base.put(CreativeTabRegistry.CLIENTSIDE_NAME, new NbtCompound());
        if (glint) base.put(CreativeTabRegistry.CLIENTSIDE_GLINT, new NbtCompound());
        stack.setCustomName(name);
        if (description != null || author != null) {
            final NbtList lore = new NbtList();
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
        final NbtCompound effect = new NbtCompound();
        effect.putInt("Id", id);
        effect.putByte("ShowParticles", showParticles ? (byte) 1 : (byte) 0);
        effect.putInt("Duration", duration);
        effect.putByte("Amplifier", (byte) amplifier);
        return effect;
    }

}
