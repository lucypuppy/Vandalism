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

package de.nekosarekawaii.vandalism.util.render.util;

import org.joml.Vector2i;
import org.lwjgl.stb.STBRPContext;
import org.lwjgl.stb.STBRPNode;
import org.lwjgl.stb.STBRPRect;
import org.lwjgl.stb.STBRectPack;

public class RectPacker {

    private final STBRPContext context;
    private final STBRPRect.Buffer singleRect;
    private STBRPNode.Buffer nodes;
    private int nodesCapacity;

    public RectPacker() {
        this.context = STBRPContext.create();
        this.singleRect = STBRPRect.create(1);
    }

    public void init(int width, int height) {
        if (this.nodes == null || width > this.nodesCapacity) {
            this.nodes = STBRPNode.create(width);
            this.nodesCapacity = width;
        }
        STBRectPack.stbrp_init_target(this.context, width, height, this.nodes);
    }

    public boolean packSingleRect(int rectWidth, int rectHeight, Vector2i outPosition) {
        this.singleRect.id(0);
        this.singleRect.w(rectWidth);
        this.singleRect.h(rectHeight);
        this.singleRect.x(0);
        this.singleRect.y(0);
        STBRectPack.stbrp_pack_rects(this.context, this.singleRect);
        if (this.singleRect.was_packed()) {
            outPosition.x = this.singleRect.x();
            outPosition.y = this.singleRect.y();
            return true;
        }
        return false;
    }
}
