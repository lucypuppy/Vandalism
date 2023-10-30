package de.vandalismdevelopment.vandalism.util.render;

import org.lwjgl.glfw.GLFW;

public class RenderUtil {

    private static double fps, prevGLTime = Double.NaN;

    public static void drawFrame() {
        if (Double.isNaN(prevGLTime)) {
            prevGLTime = GLFW.glfwGetTime();
            return;
        }
        double
                time = GLFW.glfwGetTime(),
                delta = time - prevGLTime;
        fps = 1.0 / delta;
        prevGLTime = time;
    }

    public static double getFps() {
        return fps;
    }

}
