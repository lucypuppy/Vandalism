package de.nekosarekawaii.vandalism.base.event.internal;

import de.florianmichael.dietrichevents2.AbstractEvent;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;

public interface ModuleToggleListener {

    void onModuleToggle(final ModuleToggleEvent event);

    class ModuleToggleEvent extends AbstractEvent<ModuleToggleListener> {

        public static final int ID = 28;

        public final AbstractModule module;
        public boolean active;

        public ModuleToggleEvent(final AbstractModule module, final boolean active) {
            this.module = module;
            this.active = active;
        }

        public boolean wasActive() {
            return !active;
        }

        @Override
        public void call(final ModuleToggleListener listener) {
            listener.onModuleToggle(this);
        }

    }

}
