/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.addonbaritone.settings;

import baritone.Baritone;
import baritone.api.Settings;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.LongValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.util.render.util.HSBColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BaritoneSettingMapper implements ValueParent {

    private final List<Value<?>> values;

    public BaritoneSettingMapper() {
        this.values = new ArrayList<>();
    }

    public void loadSettings() {
        this.values.clear();

        for (final Settings.Setting<?> setting : Baritone.settings().allSettings) {
            settingToValue(setting);
        }
    }

    // We need this method in case someone changes the settings via command.
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void updateSettings() {
        for (final Settings.Setting<?> setting : Baritone.settings().allSettings) {
            final Value value = this.byName(setting.getName());

            if (value != null) {
                if (value.getValue() instanceof HSBColor) {
                    value.setValue(new HSBColor((Color) setting.value));
                } else {
                    value.setValue(setting.value);
                }
            }
        }
    }

    // This is a really dumb way to do it but i guess its good enough for now.
    @SuppressWarnings("unchecked")
    private void settingToValue(final Settings.Setting<?> undefinedSetting) {
        if (undefinedSetting.value instanceof Boolean) {
            final Settings.Setting<Boolean> setting = (Settings.Setting<Boolean>) undefinedSetting;

            final BooleanValue booleanValue = new BooleanValue(this, setting.getName(), null, setting.defaultValue)
                    .onValueChange((oldVal, newVal) -> setting.value = newVal);

            booleanValue.setValue(setting.value);
        } else if (undefinedSetting.value instanceof Color) {
            final Settings.Setting<Color> setting = (Settings.Setting<Color>) undefinedSetting;

            final ColorValue colorValue = new ColorValue(this, setting.getName(), null, setting.defaultValue)
                    .onValueChange((oldVal, newVal) -> setting.value = newVal.getColor());

            colorValue.setValue(setting.value);
        } else if (undefinedSetting.value instanceof Float) {
            final Settings.Setting<Float> setting = (Settings.Setting<Float>) undefinedSetting;

            final FloatValue floatValue = new FloatValue(this, setting.getName(), null, setting.defaultValue, 0.0F, 10.0F)
                    .onValueChange((oldVal, newVal) -> setting.value = newVal);

            floatValue.setValue(setting.value);
        } else if (undefinedSetting.value instanceof Double) {
            final Settings.Setting<Double> setting = (Settings.Setting<Double>) undefinedSetting;

            final DoubleValue doubleValue = new DoubleValue(this, setting.getName(), null, setting.defaultValue, 0.0, 50.0)
                    .onValueChange((oldVal, newVal) -> setting.value = newVal);

            doubleValue.setValue(setting.value);
        } else if (undefinedSetting.value instanceof Integer) {
            final Settings.Setting<Integer> setting = (Settings.Setting<Integer>) undefinedSetting;

            final IntegerValue integerValue = new IntegerValue(this, setting.getName(), null, setting.defaultValue, -5000, 5000)
                    .onValueChange((oldVal, newVal) -> setting.value = newVal);

            integerValue.setValue(setting.value);
        } else if (undefinedSetting.value instanceof Long) {
            final Settings.Setting<Long> setting = (Settings.Setting<Long>) undefinedSetting;

            final LongValue longValue = new LongValue(this, setting.getName(), null, setting.defaultValue, 0L, 146008555100680L)
                    .onValueChange((oldVal, newVal) -> setting.value = newVal);

            longValue.setValue(setting.value);
        }
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

    @Override
    public String getName() {
        return "Baritone Settings";
    }

}
