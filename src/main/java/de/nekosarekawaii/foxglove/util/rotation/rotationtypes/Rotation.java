package de.nekosarekawaii.foxglove.util.rotation.rotationtypes;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Rotation {

    private float yaw, pitch;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getPitch() {
        return this.pitch;
    }

    public Vec3d getVector() {
        final float f = pitch * (float) (Math.PI / 180.0);
        final float g = -yaw * (float) (Math.PI / 180.0);
        final float h = MathHelper.cos(g);
        final float i = MathHelper.sin(g);
        final float j = MathHelper.cos(f);
        final float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

}
