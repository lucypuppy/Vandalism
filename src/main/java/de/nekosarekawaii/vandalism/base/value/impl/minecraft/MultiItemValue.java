package de.nekosarekawaii.vandalism.base.value.impl.minecraft;

import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.selection.MultiModeValue;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MultiItemValue extends MultiModeValue {

    public MultiItemValue(ValueParent parent, String name, String description, final Item... options) {
        this(parent, name, description, Collections.emptyList(), options);
    }

    public MultiItemValue(ValueParent parent, String name, String description, final List<Item> defaultValue, final Item... options) {
        super(
                parent,
                name,
                description,
                defaultValue.stream().map(item -> Registries.ITEM.getId(item).toString()).toList(),
                Arrays.stream(options).map(item -> Registries.ITEM.getId(item).toString()).toArray(String[]::new)
        );
    }

    public boolean isSelected(final Identifier value) {
        return this.getValue().contains(value.toString());
    }

    public boolean isSelected(final Item value) {
        return this.getValue().contains(Registries.ITEM.getId(value).toString());
    }

}
