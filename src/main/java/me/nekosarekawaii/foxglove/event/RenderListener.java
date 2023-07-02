package me.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.core.AbstractEvent;
import de.florianmichael.dietrichevents2.core.Listener;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

/**
 * The RenderListener interface represents a listener for rendering events in the Foxglove mod.
 * Classes implementing this interface can listen for rendering events and define corresponding event handler methods.
 */
public interface RenderListener extends Listener {

    /**
     * Called when a 2D rendering is performed in the in-game context.
     *
     * @param context The DrawContext object associated with the rendering.
     * @param delta   The time elapsed since the last frame.
     * @param window  The GLFW Window object associated with the rendering.
     */
    default void onRender2DInGame(final DrawContext context, final float delta, final Window window) {
    }

    /**
     * Called when a 2D rendering is performed in the out-game context.
     *
     * @param context The DrawContext object associated with the rendering.
     * @param mouseX  The x-coordinate of the mouse cursor.
     * @param mouseY  The y-coordinate of the mouse cursor.
     * @param delta   The time elapsed since the last frame.
     */
    default void onRender2D(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
    }

    /**
     * The types of 2D rendering events.
     */
    enum Render2DEventType {
        IN_GAME, OUT_GAME
    }

    /**
     * The Render2DEvent class represents a 2D rendering event.
     * It encapsulates the type of the event, the associated GuiGraphics object, mouse coordinates, delta time, and window (in the case of in-game rendering).
     */
    class Render2DEvent extends AbstractEvent<RenderListener> {

        public final static int ID = 3;

        private final Render2DEventType type;
        private final DrawContext context;
        private final int mouseX, mouseY;
        private final float delta;
        private final Window window;

        /**
         * Constructs a new Render2DEvent for out-game rendering with the specified GuiGraphics, mouse coordinates, and delta time.
         *
         * @param context The DrawContext object associated with the rendering.
         * @param mouseX  The x-coordinate of the mouse cursor.
         * @param mouseY  The y-coordinate of the mouse cursor.
         * @param delta   The time elapsed since the last frame.
         */
        public Render2DEvent(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
            this.type = Render2DEventType.OUT_GAME;
            this.context = context;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.delta = delta;
            this.window = null;
        }

        /**
         * Constructs a new Render2DEvent for in-game rendering with the specified GuiGraphics, delta time, and window.
         *
         * @param context The DrawContext object associated with the rendering.
         * @param delta   The time elapsed since the last frame.
         * @param window  The GLFW Window object associated with the rendering.
         */
        public Render2DEvent(final DrawContext context, final float delta, final Window window) {
            this.type = Render2DEventType.IN_GAME;
            this.context = context;
            this.delta = delta;
            this.window = window;
            this.mouseX = 0;
            this.mouseY = 0;
        }

        /**
         * Calls the appropriate event handler method on the listener based on the event type.
         *
         * @param listener The listener to call the event handler on.
         */
        @Override
        public void call(final RenderListener listener) {
            if (this.type == Render2DEventType.IN_GAME) {
                listener.onRender2DInGame(this.context, this.delta, this.window);
            } else {
                listener.onRender2D(this.context, this.mouseX, this.mouseY, this.delta);
            }
        }

    }

}
