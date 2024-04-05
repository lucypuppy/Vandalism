/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BaritoneSettingParser implements ValueParent {

    private final List<Value<?>> values;

    public BaritoneSettingParser() {
        this.values = new ArrayList<>();
    }

    public void reloadSettings() {
        this.values.clear();

        for (final Settings.Setting<?> setting : Baritone.settings().allSettings) {
            settingToValue(setting);
        }
    }

    // This is a really dumb way to do it but i guess its good enough for now.
    // This value is very shitty so we should recode it in the future.
    @SuppressWarnings("unchecked")
    private void settingToValue(final Settings.Setting<?> undefinedSetting) {
        if (undefinedSetting.value instanceof Boolean) {
            final Settings.Setting<Boolean> setting = (Settings.Setting<Boolean>) undefinedSetting;

            new BooleanValue(this, setting.getName(), "", setting.value)
                    .onValueChange((oldVal, newVal) -> setting.value = newVal);
        } else if (undefinedSetting.value instanceof Color) {
            final Settings.Setting<Color> setting = (Settings.Setting<Color>) undefinedSetting;

            new ColorValue(this, setting.getName(), "", setting.value)
                    .onValueChange((oldVal, newVal) -> setting.value = newVal.getColor());
        } else if (undefinedSetting.value instanceof Float) {
            final Settings.Setting<Float> setting = (Settings.Setting<Float>) undefinedSetting;

            new FloatValue(this, setting.getName(), "", setting.value, 0.0F, 10.0F)
                    .onValueChange((oldVal, newVal) -> setting.value = newVal);
        } else if (undefinedSetting.value instanceof Double) {
            final Settings.Setting<Double> setting = (Settings.Setting<Double>) undefinedSetting;

            new DoubleValue(this, setting.getName(), "", setting.value, 0., 50.0)
                    .onValueChange((oldVal, newVal) -> setting.value = newVal);
        } else if (undefinedSetting.value instanceof Integer) {
            final Settings.Setting<Integer> setting = (Settings.Setting<Integer>) undefinedSetting;

            new IntegerValue(this, setting.getName(), "", setting.value, -5000, 5000)
                    .onValueChange((oldVal, newVal) -> setting.value = newVal);
        } else if (undefinedSetting.value instanceof Long) {
            final Settings.Setting<Long> setting = (Settings.Setting<Long>) undefinedSetting;

            new LongValue(this, setting.getName(), "", setting.value, 0L, 146008555100680L)
                    .onValueChange((oldVal, newVal) -> setting.value = newVal);
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
