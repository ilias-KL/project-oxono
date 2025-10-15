package g61258.dev3.oxono.model;

import java.util.Stack;

/**
 * The {@code CommandManager} class is responsible for managing the execution,
 * undo, and redo of commands in the Oxono game. It uses stacks to maintain
 * the history of commands for undo and redo functionality.
 */
public class CommandManager {
    private Stack<Command> undoStack;
    private Stack<Command> redoStack;

    /**
     * Constructs a new {@code CommandManager}.
     * Initializes the undo and redo stacks.
     */
    public CommandManager() {
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    /**
     * Executes a command and adds it to the undo stack.
     * Clears the redo stack when a new command is executed.
     * @param command the command to execute
     */
    public void executeCommand(Command command) {
        command.execute();  // Exécute la commande
        undoStack.push(command);  // Ajoute à la pile d'annulation
        redoStack.clear();  // Vide la pile de réexécution
    }

    /**
     * Undoes the last executed command by calling its {@code unexecute()} method.
     * Moves the undone command to the redo stack.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();  // Récupère la dernière commande
            command.unexecute();  // Annule la commande
            redoStack.push(command);  // Ajoute à la pile de réexécution
        } else {
            System.out.println("Aucune commande à annuler.");
        }
    }

    /**
     * Redoes the last undone command by calling its {@code execute()} method.
     * Moves the redone command back to the undo stack.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();// Récupère la dernière commande annulée
            command.execute();  // Refait la commande
            undoStack.push(command);  // Réajoute à la pile d'annulation
        } else {
            System.out.println("Aucune commande à refaire.");
        }
    }


    /**
     * Checks if there are any actions that can be undone.
     * @return  true if the undo stack is not empty and an undo action is available,
     *         otherwise false.
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Checks if there are any actions that can be redone.
     * @return true if the redo stack is not empty and a redo action is available,
     *         otherwise false.
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }


}

