package me.nekosarekawaii.foxglove.util;

import net.minecraft.client.MinecraftClient;

import java.util.concurrent.Executors;

/**
 * The RunnableScheduleUtils class provides utility methods for scheduling and executing Runnable tasks with a delay.
 */
public final class RunnableScheduleUtils {

    /**
     * Schedules a Runnable task to be executed after a specified delay.
     *
     * @param runnable The Runnable task to be executed.
     * @param delay    The delay in milliseconds before executing the task.
     */
    public static void scheduleRunnableWithDelay(final Runnable runnable, final long delay) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Schedules a Runnable task to be executed on the Minecraft client thread after a specified delay.
     *
     * @param runnable The Runnable task to be executed.
     * @param delay    The delay in milliseconds before executing the task.
     */
    public static void scheduleRunnableWithDelayForMinecraftThread(final Runnable runnable, final long delay) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(delay);
                MinecraftClient.getInstance().execute(runnable);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
