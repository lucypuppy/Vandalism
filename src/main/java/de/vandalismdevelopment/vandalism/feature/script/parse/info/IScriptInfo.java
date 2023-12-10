package de.vandalismdevelopment.vandalism.feature.script.parse.info;

public interface IScriptInfo<T> {

    String tag();

    T parse(final String line) throws Exception;

    T defaultValue();

}
