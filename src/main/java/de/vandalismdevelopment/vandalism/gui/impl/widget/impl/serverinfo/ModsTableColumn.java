package de.vandalismdevelopment.vandalism.gui.imgui.impl.widget.impl.serverinfo;

import de.vandalismdevelopment.vandalism.util.EnumNameNormalizer;

public enum ModsTableColumn implements EnumNameNormalizer {

    MOD_ID, MOD_VERSION, ACTIONS;

    private final String normalName;

    ModsTableColumn() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

}