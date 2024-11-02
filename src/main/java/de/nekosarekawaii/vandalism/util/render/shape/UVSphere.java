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

package de.nekosarekawaii.vandalism.util.render.shape;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.function.IntConsumer;

// See: https://www.songho.ca/opengl/gl_sphere.html
public class UVSphere {

    private static final float PI = 3.141592653589793f;

    public static void generateIndexed(int stacks, int sectors, float radius, ShapeVertexConsumer vertexConsumer, IntConsumer indexConsumer) {
        float x, y, z, xy;                              // vertex position
        float nx, ny, nz, lengthInv = 1.0f / radius;    // vertex normal
        float s, t;                                     // vertex texCoord

        float sectorStep = 2 * PI / (float) sectors;
        float stackStep = PI / (float) stacks;
        float sectorAngle, stackAngle;

        final Vector3f pos = new Vector3f();
        final Vector3f normal = new Vector3f();
        final Vector2f uv = new Vector2f();

        for(int i = 0; i <= stacks; ++i) {
            stackAngle = PI / 2 - i * stackStep;        // starting from pi/2 to -pi/2
            xy = radius * (float) Math.cos(stackAngle); // r * cos(u)
            z = radius * (float) Math.sin(stackAngle);  // r * sin(u)

            // add (sectorCount+1) vertices per stack
            // first and last vertices have same position and normal, but different tex coords
            for(int j = 0; j <= sectors; ++j) {
                sectorAngle = j * sectorStep;           // starting from 0 to 2pi

                // vertex position (x, y, z)
                x = xy * (float) Math.cos(sectorAngle);             // r * cos(u) * cos(v)
                y = xy * (float) Math.sin(sectorAngle);             // r * cos(u) * sin(v)
                pos.set(x, y, z);

                // normalized vertex normal (nx, ny, nz)
                nx = x * lengthInv;
                ny = y * lengthInv;
                nz = z * lengthInv;
                normal.set(nx, ny, nz);

                // vertex tex coord (s, t) range between [0, 1]
                s = (float)j / (float) sectors;
                t = (float)i / (float) stacks;
                uv.set(s, t);

                /*float yy = 2.0f * (float) i / (float) stacks - 1.0f;

                float r = (float) Math.sqrt(1.0f - yy * yy);
                float angle = 2.0f * PI * (float) j / (float) sectors;

                float xx = (float) (r * Math.sin(angle));
                float zz = (float) (r * Math.cos(angle));

                float u = (0.5f + ((float) (Math.atan2(zz, xx)) / (2.0f * PI) ));
                float v = (0.5f + ((float) (Math.asin(yy)) / PI ));

                uv.set(u, v);*/
                vertexConsumer.accept(pos, uv, normal);
            }
        }

        int k1, k2;
        for(int i = 0; i < stacks; ++i) {
            k1 = i * (sectors + 1);     // beginning of current stack
            k2 = k1 + sectors + 1;      // beginning of next stack

            for(int j = 0; j < sectors; ++j, ++k1, ++k2) {
                // 2 triangles per sector excluding first and last stacks
                // k1 => k2 => k1+1
                if(i != 0) {
                    indexConsumer.accept(k1);
                    indexConsumer.accept(k2);
                    indexConsumer.accept(k1 + 1);
                }

                // k1+1 => k2 => k2+1
                if(i != (stacks-1)) {
                    indexConsumer.accept(k1 + 1);
                    indexConsumer.accept(k2);
                    indexConsumer.accept(k2 + 1);
                }

                // store indices for lines
                // vertical lines for all stacks, k1 => k2
                /*lineIndices.push_back(k1);
                lineIndices.push_back(k2);
                if(i != 0)  // horizontal lines except 1st stack, k1 => k+1
                {
                    lineIndices.push_back(k1);
                    lineIndices.push_back(k1 + 1);
                }*/
            }
        }
    }
}
