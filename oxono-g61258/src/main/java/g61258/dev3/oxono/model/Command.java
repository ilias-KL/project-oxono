package g61258.dev3.oxono.model;

/**
 * The {@code Command} interface defines the contract for commands
 * in the Oxono game. It supports the Command design pattern by
 * providing methods to execute and undo actions.
 */
public interface Command {
    /**
     * Executes the command. This method applies the desired action
     * or change to the game state.
     */
    void execute();

    /**
     * Reverses the execution of the command. This method undoes the
     * previously applied action or change to restore the game state
     * to its previous condition.
     */
    void unexecute();

}
