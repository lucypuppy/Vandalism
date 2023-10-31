package de.vandalismdevelopment.vandalism.gui.imgui.impl.widget.serverinfo;

import de.vandalismdevelopment.vandalism.util.EnumNameNormalizer;

public enum PluginsTableColumn implements EnumNameNormalizer {

    NAME, ACTIONS;

    private final String normalName;

    PluginsTableColumn() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

}