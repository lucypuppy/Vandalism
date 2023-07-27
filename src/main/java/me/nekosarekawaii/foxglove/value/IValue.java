package me.nekosarekawaii.foxglove.value;

import me.nekosarekawaii.foxglove.config.Config;

import java.util.List;

public interface IValue {

    List<Value<?>> getValues();

    Config getConfig();

    default Value<?> getValue(final String name) {
        for (final Value<?> value : this.getValues()) {
            if (value.getHashIdent().equals(name)) {
                return value;
            }
        }

        return null;
    }

}
