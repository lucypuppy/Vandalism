package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.namehistory;

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