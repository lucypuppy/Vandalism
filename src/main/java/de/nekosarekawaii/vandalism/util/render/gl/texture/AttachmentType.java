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

package de.nekosarekawaii.vandalism.util.render.gl.texture;

import lombok.Getter;
import org.lwjgl.opengl.GL45C;

@Getter
public enum AttachmentType {

    COLOR_ATTACHMENT0(GL45C.GL_COLOR_ATTACHMENT0, 0),
    COLOR_ATTACHMENT1(GL45C.GL_COLOR_ATTACHMENT1, 1),
    COLOR_ATTACHMENT2(GL45C.GL_COLOR_ATTACHMENT2, 2),
    COLOR_ATTACHMENT3(GL45C.GL_COLOR_ATTACHMENT3, 3),
    COLOR_ATTACHMENT4(GL45C.GL_COLOR_ATTACHMENT4, 4),
    COLOR_ATTACHMENT5(GL45C.GL_COLOR_ATTACHMENT5, 5),
    COLOR_ATTACHMENT6(GL45C.GL_COLOR_ATTACHMENT6, 6),
    COLOR_ATTACHMENT7(GL45C.GL_COLOR_ATTACHMENT7, 7),
    COLOR_ATTACHMENT8(GL45C.GL_COLOR_ATTACHMENT8, 8),
    COLOR_ATTACHMENT9(GL45C.GL_COLOR_ATTACHMENT9, 9),
    COLOR_ATTACHMENT10(GL45C.GL_COLOR_ATTACHMENT10, 10),
    COLOR_ATTACHMENT11(GL45C.GL_COLOR_ATTACHMENT11, 11),
    COLOR_ATTACHMENT12(GL45C.GL_COLOR_ATTACHMENT12, 12),
    COLOR_ATTACHMENT13(GL45C.GL_COLOR_ATTACHMENT13, 13),
    COLOR_ATTACHMENT14(GL45C.GL_COLOR_ATTACHMENT14, 14),
    COLOR_ATTACHMENT15(GL45C.GL_COLOR_ATTACHMENT15, 15),
    COLOR_ATTACHMENT16(GL45C.GL_COLOR_ATTACHMENT16, 16),
    COLOR_ATTACHMENT17(GL45C.GL_COLOR_ATTACHMENT17, 17),
    COLOR_ATTACHMENT18(GL45C.GL_COLOR_ATTACHMENT18, 18),
    COLOR_ATTACHMENT19(GL45C.GL_COLOR_ATTACHMENT19, 19),
    COLOR_ATTACHMENT20(GL45C.GL_COLOR_ATTACHMENT20, 20),
    COLOR_ATTACHMENT21(GL45C.GL_COLOR_ATTACHMENT21, 21),
    COLOR_ATTACHMENT22(GL45C.GL_COLOR_ATTACHMENT22, 22),
    COLOR_ATTACHMENT23(GL45C.GL_COLOR_ATTACHMENT23, 23),
    COLOR_ATTACHMENT24(GL45C.GL_COLOR_ATTACHMENT24, 24),
    COLOR_ATTACHMENT25(GL45C.GL_COLOR_ATTACHMENT25, 25),
    COLOR_ATTACHMENT26(GL45C.GL_COLOR_ATTACHMENT26, 26),
    COLOR_ATTACHMENT27(GL45C.GL_COLOR_ATTACHMENT27, 27),
    COLOR_ATTACHMENT28(GL45C.GL_COLOR_ATTACHMENT28, 28),
    COLOR_ATTACHMENT29(GL45C.GL_COLOR_ATTACHMENT29, 29),
    COLOR_ATTACHMENT30(GL45C.GL_COLOR_ATTACHMENT30, 30),
    COLOR_ATTACHMENT31(GL45C.GL_COLOR_ATTACHMENT31, 31),
    DEPTH_ATTACHMENT(GL45C.GL_DEPTH_ATTACHMENT),
    STENCIL_ATTACHMENT(GL45C.GL_STENCIL_ATTACHMENT),
    DEPTH_STENCIL_ATTACHMENT(GL45C.GL_DEPTH_STENCIL_ATTACHMENT);

    private final int glType;
    private final boolean colorAttachment;
    private final int colorAttachmentIndex;

    AttachmentType(int glType, int colorAttachmentIndex) {
        this.glType = glType;
        this.colorAttachment = true;
        this.colorAttachmentIndex = colorAttachmentIndex;
    }

    AttachmentType(int glType) {
        this.glType = glType;
        this.colorAttachment = false;
        this.colorAttachmentIndex = -1;
    }

    public static AttachmentType byGlType(int glType) {
        for (AttachmentType value : values()) {
            if (value.glType == glType) {
                return value;
            }
        }
        return null;
    }
}
