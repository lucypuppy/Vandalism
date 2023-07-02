package me.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.core.AbstractEvent;
import de.florianmichael.dietrichevents2.core.Listener;

/**
 * The KeyboardListener interface represents a listener for keyboard events in the Foxglove mod.
 * Classes implementing this interface can listen for keyboard events and define corresponding event handler methods.
 */
public interface KeyboardListener extends Listener {

    /**
     * Called when a character is input.
     *
     * @param window    The window receiving the event.
     * @param codePoint The Unicode code point of the character.
     * @param modifiers The modifier key flags.
     */
    default void onChar(final long window, final int codePoint, final int modifiers) {
    }

    /**
     * Called when a key is pressed, released, or repeated.
     *
     * @param window    The window receiving the event.
     * @param key       The keyboard key that was pressed or released.
     * @param scanCode  The system-specific scancode of the key.
     * @param action    The action (GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT) taken on the key.
     * @param modifiers The modifier key flags.
     */
    default void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
    }

    /**
     * The types of keyboard events.
     */
    enum KeyboardEventType {
        KEY, CHAR
    }

    /**
     * The KeyboardEvent class represents a keyboard event.
     * It encapsulates the type of the event and provides the necessary data to handle the event.
     */
    class KeyboardEvent extends AbstractEvent<KeyboardListener> {

        public final static int ID = 1;

        private final KeyboardEventType type;

        public final long window;
        public final int key, codePoint, scanCode, action, modifiers;

        /**
         * Constructs a new KeyboardEvent with the specified event type and event data.
         *
         * @param type      The type of the event.
         * @param window    The window receiving the event.
         * @param key       The keyboard key that was pressed or released.
         * @param codePoint The Unicode code point of the character.
         * @param scanCode  The system-specific scancode of the key.
         * @param action    The action (GLFW_PRESS, GLFW_RELEASE, GLFW_REPEAT) taken on the key.
         * @param modifiers The modifier key flags.
         */
        public KeyboardEvent(final KeyboardEventType type, final long window, final int key, final int codePoint, final int scanCode, final int action, final int modifiers) {
            this.type = type;
            this.window = window;
            this.key = key;
            this.codePoint = codePoint;
            this.scanCode = scanCode;
            this.action = action;
            this.modifiers = modifiers;
        }

        /**
         * Calls the appropriate event handler method on the listener based on the event type.
         *
         * @param listener The listener to call the event handler on.
         */
        @Override
        public void call(final KeyboardListener listener) {
            if (this.type == KeyboardEventType.KEY) {
                listener.onKey(this.window, this.key, this.scanCode, this.action, this.modifiers);
            } else {
                listener.onChar(this.window, this.codePoint, this.modifiers);
            }
        }

    }

}
