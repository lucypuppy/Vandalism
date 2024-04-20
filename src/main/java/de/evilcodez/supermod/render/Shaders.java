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

package de.evilcodez.supermod.render;

import de.evilcodez.supermod.render.gl.shader.Shader;
import de.evilcodez.supermod.render.gl.shader.ShaderProgram;
import de.evilcodez.supermod.render.gl.shader.ShaderType;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import lombok.Getter;
import org.lwjgl.opengl.GL45C;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Shaders {

    private static boolean initialized;

    private static final List<ShaderProgram> shaders = new ArrayList<>();

    // General purpose shaders
    @Getter private static ShaderProgram positionShader;
    @Getter private static ShaderProgram instPositionShader;
    @Getter private static ShaderProgram positionColorShader;
    @Getter private static ShaderProgram instPositionColorShader;
    @Getter private static ShaderProgram positionTexShader;
    @Getter private static ShaderProgram instPositionTexShader;
    @Getter private static ShaderProgram positionTexColorShader;
    @Getter private static ShaderProgram instPositionTexColorShader;


    public static void loadAll() {
        if (initialized) return;
        initialized = true;

        shaders.add(positionShader = create("position", load(ShaderType.VERTEX, "general_purpose/position"), load(ShaderType.FRAGMENT, "general_purpose/position")));
        shaders.add(instPositionShader = create("inst_position", load(ShaderType.VERTEX, "general_purpose/inst_position"), load(ShaderType.FRAGMENT, "general_purpose/position")));
        shaders.add(positionColorShader = create("position_color", load(ShaderType.VERTEX, "general_purpose/position_color"), load(ShaderType.FRAGMENT, "general_purpose/position_color")));
        shaders.add(instPositionColorShader = create("inst_position_color", load(ShaderType.VERTEX, "general_purpose/inst_position_color"), load(ShaderType.FRAGMENT, "general_purpose/position_color")));
        shaders.add(positionTexShader = create("position_tex", load(ShaderType.VERTEX, "general_purpose/position_tex"), load(ShaderType.FRAGMENT, "general_purpose/position_tex")));
        shaders.add(instPositionTexShader = create("inst_position_tex", load(ShaderType.VERTEX, "general_purpose/inst_position_tex"), load(ShaderType.FRAGMENT, "general_purpose/position_tex")));
        shaders.add(positionTexColorShader = create("position_tex_color", load(ShaderType.VERTEX, "general_purpose/position_tex_color"), load(ShaderType.FRAGMENT, "general_purpose/position_tex_color")));
        shaders.add(instPositionTexColorShader = create("inst_position_tex_color", load(ShaderType.VERTEX, "general_purpose/inst_position_tex_color"), load(ShaderType.FRAGMENT, "general_purpose/position_tex_color")));
    }

    public static void unloadAll() {
        if (!initialized) return;
        initialized = false;
        for (ShaderProgram shader : shaders) {
            shader.close();
        }
        shaders.clear();
    }

    public static Shader load(ShaderType type, String name) {
        try {
            return Shader.compile(type, getShaderSource(type, name));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load shader " + name, e);
        }
    }

    public static ShaderProgram create(String name, Shader... shaders) {
        try {
            return ShaderProgram.compose(shaders);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create shader program " + name, e);
        }
    }

    public static String getShaderSource(ShaderType type, String name) throws IOException {
        final String extension = switch (type) {
            case VERTEX -> "vert";
            case FRAGMENT -> "frag";
            case GEOMETRY -> "geom";
            case TESS_CONTROL -> "tesc";
            case TESS_EVALUATION -> "tese";
            case COMPUTE -> "comp";
        };
        try (final InputStream input = Shaders.class.getResourceAsStream("/assets/" + FabricBootstrap.MOD_ID + "/shader/" + name + "." + extension)) {
            if (input == null) throw new IOException("Shader " + name + " not found");
            return new String(input.readAllBytes());
        }
    }
}
