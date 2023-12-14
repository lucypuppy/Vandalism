package de.nekosarekawaii.vandalism.base.account;

import java.util.concurrent.CompletableFuture;

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
    CompletableFuture<AbstractAccount> make();

}
