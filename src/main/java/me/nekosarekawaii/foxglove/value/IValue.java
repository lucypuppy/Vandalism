package me.nekosarekawaii.foxglove.value;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.config.Config;

public interface IValue {

    ObjectArrayList<Value<?>> getValues();

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
