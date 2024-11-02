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

package de.nekosarekawaii.vandalism.util.render.effect.other;

import de.nekosarekawaii.vandalism.util.render.effect.PostProcessEffect;
import lombok.Getter;
import lombok.Setter;

public class DepthFilterEffect extends PostProcessEffect {

    @Getter
    @Setter
    private int depthTextureId;

    public DepthFilterEffect() {
        super("DepthFilter");
        this.addPass(ctx -> {
            ctx.setShader("postprocess/other/depth_filter");
            ctx.setTextureBinding("tex", this.maskFramebuffer());
            ctx.setTextureBinding("depth_tex1", () -> this.maskFramebuffer().get().getDepthAttachment());
            ctx.setTextureBinding("depth_tex2", () -> this.depthTextureId);
        });
    }
}
