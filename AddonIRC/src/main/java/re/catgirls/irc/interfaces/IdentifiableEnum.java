package re.catgirls.irc.interfaces;

public interface IdentifiableEnum {

    int getId();

    static IdentifiableEnum getById(final int id, Class<? extends IdentifiableEnum> enumType) {
        for (final IdentifiableEnum result : enumType.getEnumConstants()) {
            if (result.getId() == id)
                return result;
        }

        throw new NullPointerException("Unknown enum constant for ID: " + id);
    }

}
