package de.vandalismdevelopment.vandalism.account_v2;

/**
 * Creates a new account and displays the interface for it.
 */
public interface AccountFactory {

    /**
     * Should be used to draw a ImGui based interface for the account.
     */
    void displayFactory();

    /**
     * Creates a new account based on the input from the interface.
     *
     * @return The created account.
     */
    AbstractAccount make();

}
