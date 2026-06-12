package org.example;

import java.util.ArrayList;
import java.util.List;

public class Frame {
    private int score;
    private boolean lastFrame;
    private IGenerateur generateur;
    private List<Roll> rolls;

    public Frame(IGenerateur generateur, boolean lastFrame) {
        this.lastFrame = lastFrame;
        this.generateur = generateur;
        this.rolls = new ArrayList<>();
        this.score = 0;
    }

    public boolean makeRoll() {
        int pins = generateur.randomPin(10);
        Roll roll = new Roll(pins);

        if (!canMakeRoll(pins)) {
            return false;
        }

        rolls.add(roll);
        score += pins;

        return true;
    }

    private boolean canMakeRoll(int pins) {
        if (lastFrame) {
            return canMakeRollInLastFrame(pins);
        } else {
            return canMakeRollInStandardFrame(pins);
        }
    }

    private boolean canMakeRollInStandardFrame(int pins) {
        if (rolls.isEmpty() && pins == 10) {
            return true;
        }

        if (rolls.size() == 1 && rolls.get(0).getPins() == 10) {
            return false;
        }

        if (rolls.size() >= 2) {
            return false;
        }

        return true;
    }

    private boolean canMakeRollInLastFrame(int pins) {
        if (rolls.size() >= 3) {
            return false;
        }

        if (rolls.size() < 2) {
            return true;
        }

        int firstRoll = rolls.get(0).getPins();
        int secondRoll = rolls.get(1).getPins();

        if (firstRoll == 10) {
            return true;
        }

        if (firstRoll + secondRoll == 10) {
            return true;
        }

        return false;
    }

    public int getScore() {
        return score;
    }

    public List<Roll> getRolls() {
        return rolls;
    }

    public boolean isStrike() {
        return !rolls.isEmpty() && rolls.get(0).getPins() == 10;
    }

    public boolean isSpare() {
        return rolls.size() >= 2 &&
               rolls.get(0).getPins() + rolls.get(1).getPins() == 10;
    }
}

