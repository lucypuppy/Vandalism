package de.vandalismdevelopment.vandalism.gui.impl.widget.impl.serverinfo;

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