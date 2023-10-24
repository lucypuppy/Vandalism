package de.vandalismdevelopment.vandalism.feature.impl.script;

@FunctionalInterface
public interface ScriptExecution {

    void execute(final Script script, final int lineNumber, final String code) throws Exception;

}