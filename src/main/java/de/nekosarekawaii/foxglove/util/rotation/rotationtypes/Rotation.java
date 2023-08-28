package de.nekosarekawaii.foxglove.util.rotation.rotationtypes;

import de.nekosarekawaii.foxglove.util.rotation.RotationPriority;

public class Rotation {

    private float yaw, pitch;
    private RotationPriority priority;

    public Rotation(float yaw, float pitch, RotationPriority priority) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.priority = priority;
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

    public void setPriority(RotationPriority priority) {
        this.priority = priority;
    }

    public RotationPriority getPriority() {
        return priority;
    }

}
