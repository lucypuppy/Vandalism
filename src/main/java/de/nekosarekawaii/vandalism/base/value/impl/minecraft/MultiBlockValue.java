package de.nekosarekawaii.vandalism.base.value.impl.minecraft;

import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.selection.MultiModeValue;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MultiBlockValue extends MultiModeValue {

    public MultiBlockValue(ValueParent parent, String name, String description, final Block... options) {
        this(parent, name, description, Collections.emptyList(), options);
    }

    public MultiBlockValue(ValueParent parent, String name, String description, final List<Block> defaultValue, final Block... options) {
        super(parent, name, description, defaultValue.stream().map(block -> Registries.BLOCK.getId(block).toString()).toList(), Arrays.stream(options).map(block -> Registries.BLOCK.getId(block).toString()).toArray(String[]::new));
    }

    public boolean isSelected(final Identifier value) {
        return this.getValue().contains(value.toString());
    }

    public boolean isSelected(final Block value) {
        return this.getValue().contains(Registries.BLOCK.getId(value).toString());
    }

}
