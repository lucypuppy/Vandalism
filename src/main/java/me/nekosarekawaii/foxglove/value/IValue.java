package me.nekosarekawaii.foxglove.value;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public interface IValue {

    ObjectArrayList<Value<?>> getValues();

    default Value<?> getValue(final String name) {
        for (final Value<?> value : this.getValues()) {
            if (value != null) {
                if (value.getHashIdent().equalsIgnoreCase(name)) {
                    return value;
                }
            }
        }
        return null;
    }

}
