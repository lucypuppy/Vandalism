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

package de.nekosarekawaii.vandalism.util;

import de.nekosarekawaii.vandalism.util.render.Buffers;
import de.nekosarekawaii.vandalism.util.render.gl.mem.BufferPool;
import de.nekosarekawaii.vandalism.util.render.gl.mem.VertexArrayPool;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.opengl.EXTDebugMarker;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL45C;

import java.util.List;

public class DebugHelper {

    public static final boolean DEBUG_MARKERS_SUPPORTED = GL.getCapabilities().GL_EXT_debug_marker;

    public static void pushMarker(CharSequence marker) {
        if (!DEBUG_MARKERS_SUPPORTED) return;
        EXTDebugMarker.glPushGroupMarkerEXT(marker);
    }

    public static void popMarker() {
        if (!DEBUG_MARKERS_SUPPORTED) return;
        EXTDebugMarker.glPopGroupMarkerEXT();
    }

    public static void setObjectLabel(int obj, int type, String label) {
        GL45C.glObjectLabel(type, obj, label);
    }

    public static String getObjectLabel(int obj, int type) {
        return GL45C.glGetObjectLabel(type, obj);
    }

    public static boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static void addValuesToSystemInformationDebugOverlay(List<String> list) { // TODO: call this method in F3 debug screen
        final VertexArrayPool vao = Buffers.getVertexArrayPool();
        final BufferPool imm = Buffers.getImmediateBufferPool();
        final BufferPool per = Buffers.getPersistentBufferPool();
        list.add("");
        list.add("§aSuperGL");
        list.add("VertexArrayPool: " + vao.getBufferCount());
        list.add("Immediate-BufferPool: " + imm.getBufferCount() + " / " + imm.getBufferCacheLimit());
        list.add("Persistent-BufferPool: " + per.getBufferCount() + " / " + per.getBufferCacheLimit());
    }
}
