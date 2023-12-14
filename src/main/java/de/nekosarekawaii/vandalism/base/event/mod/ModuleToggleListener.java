package de.nekosarekawaii.vandalism.base.event.mod;

import de.florianmichael.dietrichevents2.CancellableEvent;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;

public interface ModuleToggleListener {

    void onModuleToggle(final ModuleToggleEvent event);

    class ModuleToggleEvent extends CancellableEvent<ModuleToggleListener> {

        public static final int ID = 28;

        public final AbstractModule module;
        public final boolean wasActive, willBeActive;

        public ModuleToggleEvent(final AbstractModule module, final boolean wasActive, final boolean willBeActive) {
            this.module = module;
            this.wasActive = wasActive;
            this.willBeActive = willBeActive;
        }

        @Override
        public void call(final ModuleToggleListener listener) {
            listener.onModuleToggle(this);
        }

    }

}
