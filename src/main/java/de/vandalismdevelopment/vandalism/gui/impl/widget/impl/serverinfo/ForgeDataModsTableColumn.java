package de.vandalismdevelopment.vandalism.gui.impl.widget.impl.serverinfo;

import de.vandalismdevelopment.vandalism.util.EnumNameNormalizer;

public enum ForgeDataModsTableColumn implements EnumNameNormalizer {

    MOD_ID, MOD_MARKER, ACTIONS;

    private final String normalName;

    ForgeDataModsTableColumn() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

}