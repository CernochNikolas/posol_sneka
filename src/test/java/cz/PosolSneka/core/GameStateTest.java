package cz.PosolSneka.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    void turnStartsAtZero() {
        GameState state = new GameState();

        assertEquals(0, state.getTurn());
    }

    @Test
    void advanceTurnIncrementsTurn() {
        GameState state = new GameState();

        state.advanceTurn();

        assertEquals(1, state.getTurn());
    }

    @Test
    void snailCycleIsCorridorNestKitchenAndBackToCorridor() {
        GameState state = new GameState();

        // turn = 0
        assertEquals(GameState.SnailZone.CORRIDOR, state.getSnailZone());

        // turn = 1
        state.advanceTurn();
        assertEquals(GameState.SnailZone.NEST, state.getSnailZone());

        // turn = 2
        state.advanceTurn();
        assertEquals(GameState.SnailZone.KITCHEN, state.getSnailZone());

        // turn = 3
        state.advanceTurn();
        assertEquals(GameState.SnailZone.CORRIDOR, state.getSnailZone());
    }

    @Test
    void babySnailsCountDoesNotGoBelowZero() {
        GameState state = new GameState();

        assertEquals(3, state.getBabySnailsRemaining());

        state.killOneBabySnail(); // 2
        state.killOneBabySnail(); // 1
        state.killOneBabySnail(); // 0
        state.killOneBabySnail(); // still 0
        state.killOneBabySnail(); // still 0

        assertEquals(0, state.getBabySnailsRemaining());
    }
}