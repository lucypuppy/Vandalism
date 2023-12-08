package de.vandalismdevelopment.vandalism.gui.imgui.impl.widget.impl.serverinfo;

import de.vandalismdevelopment.vandalism.util.EnumNameNormalizer;

public enum PlayersTableColumn implements EnumNameNormalizer {

    USERNAME, UUID, ACTIONS;

    private final String normalName;

    PlayersTableColumn() {
        this.normalName = this.normalizeName(this.name());
    }

    @Override
    public String normalName() {
        return this.normalName;
    }

}