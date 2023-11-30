package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.namehistory;

import de.vandalismdevelopment.vandalism.util.interfaces.EnumNameNormalizer;

public enum NamesTableColumn implements EnumNameNormalizer {

    USERNAME, DATE, ACCURATE, ACTIONS;

    private final String normalName;

    NamesTableColumn() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

}