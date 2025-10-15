package g61258.dev3.oxono.model;

/**
 * Enum representing the different states of the game.
 * Each state corresponds to a specific phase of the game where different actions can be performed.
 */
public enum GameStat {
    MOVE, // Lorsque l'on peut déplacer un totem
    INSERT, // Lorsque l'on peut placer un token
    CHOICE // Lorsque l'on a placé un token

}
