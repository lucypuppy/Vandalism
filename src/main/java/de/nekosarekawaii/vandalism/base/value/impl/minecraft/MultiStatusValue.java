package de.nekosarekawaii.vandalism.base.value.impl.minecraft;

import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.selection.MultiModeValue;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MultiStatusValue extends MultiModeValue {

    public MultiStatusValue(ValueParent parent, String name, String description, final StatusEffect... options) {
        this(parent, name, description, Collections.emptyList(), options);
    }

    public MultiStatusValue(ValueParent parent, String name, String description, final List<StatusEffect> defaultValue, final StatusEffect... options) {
        super(parent, name, description,
                defaultValue.stream().map(effect -> Registries.STATUS_EFFECT.getId(effect).toShortTranslationKey()).toList(),
                Arrays.stream(options).map(effect -> Registries.STATUS_EFFECT.getId(effect).toShortTranslationKey()).toArray(String[]::new)
        );
    }

    public boolean isSelected(final Identifier value) {
        return this.getValue().contains(value.toShortTranslationKey());
    }

    public boolean isSelected(final StatusEffect value) {
        return this.getValue().contains(Registries.STATUS_EFFECT.getId(value).toShortTranslationKey());
    }

}
