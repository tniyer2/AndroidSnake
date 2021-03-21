package edu.moravian.csci299.gravitysnake;

import androidx.lifecycle.ViewModel;

/**
 * A ViewModel that represents the current state of the Game.
 * Stores the current level selected for the game and the
 * current high scores for each level in the game.
 */
public class GameModel extends ViewModel {
    public final int NUM_LEVELS = 5;

    public int currentLevel = 0;
    public final int[] scores = new int[NUM_LEVELS];

    /**
     * Getter for currentLevel.
     * @return the current selected level.
     */
    public int getCurrentLevel()
    {
        return currentLevel;
    }

    /**
     * Setter for currentLevel.
     * @param value the new value for the current selected level.
     */
    public void setCurrentLevel(int value)
    {
        checkIfIndexIsInBounds(value);
        this.currentLevel = value;
    }

    /**
     * Returns the high score associated with a certain level.
     * @param level the index of the level which you want to get the score of.
     * @return Score for a certain level.
     */
    public int getHighScore(int level)
    {
        checkIfIndexIsInBounds(level);
        return scores[level];
    }

    /**
     * Sets the high score for a level.
     * @param level the level you want to change the score of.
     * @param value the value you want to be the new score.
     */
    public void setHighScore(int level, int value)
    {
        checkIfIndexIsInBounds(level);
        scores[level] = value;
    }

    /**
     * Checks if the level is in between 0 (inclusive) and NUM_LEVELS (exclusive),
     * throws an IndexOutOfBoundsException if otherwise.
     * @param level the level to check.
     */
    private void checkIfIndexIsInBounds(int level) {
        if (level >= NUM_LEVELS || level < 0)
            throw new IndexOutOfBoundsException("Argument level is out of bounds: " + level +
                    ". There are only " + NUM_LEVELS + " levels.");
    }
}
