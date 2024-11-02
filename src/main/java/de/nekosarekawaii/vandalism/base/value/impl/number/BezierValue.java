/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.base.value.impl.number;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.util.math.MathUtil;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.flag.ImPlotAxisFlags;
import imgui.extension.implot.flag.ImPlotFlags;

import java.util.ArrayList;
import java.util.List;

public class BezierValue extends Value<Float> implements ValueParent {

    private final List<Value<?>> values = new ArrayList<>();

    private final FloatValue pointOne;
    private final FloatValue pointTwo;
    private final FloatValue pointThree;
    private final FloatValue pointFour;

    private final Float[] xValues = new Float[100];
    private final Float[] yValues = new Float[100];

    public BezierValue(
            ValueParent parent,
            String name, String description,
            final float pointOneDefaultValue, final float pointTwoDefaultValue,
            final float pointThreeDefaultValue, final float pointFourDefaultValue,
            final float minValue, final float maxValue
    ) {
        super(parent, name, description, 0.0f);
        this.pointOne = new FloatValue(
                this,
                "Point 1",
                "The first point of the bezier curve.",
                pointOneDefaultValue,
                minValue,
                maxValue
        ).onValueChange((oldValue, newValue) -> this.updateCurve());
        this.pointTwo = new FloatValue(
                this,
                "Point 2",
                "The second point of the bezier curve.",
                pointTwoDefaultValue,
                minValue,
                maxValue
        ).onValueChange((oldValue, newValue) -> this.updateCurve());
        this.pointThree = new FloatValue(
                this,
                "Point 3",
                "The third point of the bezier curve.",
                pointThreeDefaultValue,
                minValue,
                maxValue
        ).onValueChange((oldValue, newValue) -> this.updateCurve());
        this.pointFour = new FloatValue(
                this,
                "Point 4",
                "The fourth point of the bezier curve.",
                pointFourDefaultValue,
                minValue,
                maxValue
        ).onValueChange((oldValue, newValue) -> this.updateCurve());
        this.updateCurve();
    }

    @Override
    public void load(final JsonObject mainNode) {
        if (!mainNode.has(this.getName())) {
            return;
        }
        final JsonObject valueNode = mainNode.get(this.getName()).getAsJsonObject();
        for (final Value<?> value : this.getValues()) {
            value.load(valueNode);
        }
        this.updateCurve();
    }

    @Override
    public void save(final JsonObject mainNode) {
        final JsonObject valueNode = new JsonObject();
        for (final Value<?> value : this.getValues()) {
            value.save(valueNode);
        }
        mainNode.add(getName(), valueNode);
    }

    @Override
    public void render() {
        ImGui.newLine();
        this.renderValues();
        final int axisFlags = ImPlotAxisFlags.NoLabel | ImPlotAxisFlags.NoTickMarks | ImPlotAxisFlags.NoTickLabels;
        if (ImPlot.beginPlot(
                this.getName() + "##" + this.getParent().getName(),
                "",
                "",
                new ImVec2(0, 0),
                ImPlotFlags.CanvasOnly,
                axisFlags,
                axisFlags
        )) {
            ImPlot.plotLine(
                    this.getName() + "##" + this.getParent().getName() + "plotLine",
                    this.xValues,
                    this.yValues
            );
            ImPlot.endPlot();
        }
        ImGui.spacing();
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

    private void updateCurve() {
        for (int i = 0; i < 100; i++) {
            this.xValues[i] = (float) i;
            this.yValues[i] = getValue(i / 100.0f);
        }
    }

    public float getValue(final float percentage) {
        return MathUtil.cubicBezier(
                this.pointOne.getValue(), this.pointTwo.getValue(),
                this.pointThree.getValue(), this.pointFour.getValue(),
                percentage
        );
    }

}
