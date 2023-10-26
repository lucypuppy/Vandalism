package de.vandalismdevelopment.vandalism.feature.impl.script.info;

public interface IScriptInfo<T> {

    String tag();

    T parse(final String line) throws Exception;

    T defaultValue();

}
