package de.vandalismdevelopment.vandalism.gui.impl.widget.impl.serverinfo;

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