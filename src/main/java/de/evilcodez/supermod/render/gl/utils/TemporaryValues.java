package de.evilcodez.supermod.render.gl.utils;

import net.lenni0451.reflect.JavaBypass;
import org.joml.*;
import sun.misc.Unsafe;

public class TemporaryValues {

    public static final Unsafe UNSAFE = JavaBypass.getUnsafe();
    public static final Vector2f VECTOR2F = new Vector2f();
    public static final Vector3f VECTOR3F = new Vector3f();
    public static final Vector4f VECTOR4F = new Vector4f();
    public static final Vector3d VECTOR3D = new Vector3d();
    public static final Vector4d VECTOR4D = new Vector4d();
    public static final Matrix4f IDENTITY_MATRIX4F = new Matrix4f().identity();
}
