package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.script;

import de.vandalismdevelopment.vandalism.util.EnumNameNormalizer;

public enum TableColumn implements EnumNameNormalizer {
    NAME, VERSION, AUTHOR, DESCRIPTION, CATEGORY, EXPERIMENTAL, MODIFICATION_DATE, ACTIONS;

    private final String normalName;

    TableColumn() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

}