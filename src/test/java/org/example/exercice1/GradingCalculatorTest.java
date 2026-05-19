package org.example.exercice1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GradingCalculatorTest {

    @Test
    public void score95_att90_shouldBeA() {
        GradingCalculator g = new GradingCalculator(95, 90);
        assertEquals('A', g.getGrade());
    }

    @Test
    public void score85_att90_shouldBeB() {
        GradingCalculator g = new GradingCalculator(85, 90);
        assertEquals('B', g.getGrade());
    }

    @Test
    public void score65_att90_shouldBeC() {
        GradingCalculator g = new GradingCalculator(65, 90);
        assertEquals('C', g.getGrade());
    }

    @Test
    public void score95_att65_shouldBeB() {
        GradingCalculator g = new GradingCalculator(95, 65);
        assertEquals('B', g.getGrade());
    }

    @Test
    public void score95_att55_shouldBeF() {
        GradingCalculator g = new GradingCalculator(95, 55);
        assertEquals('F', g.getGrade());
    }

    @Test
    public void score65_att55_shouldBeF() {
        GradingCalculator g = new GradingCalculator(65, 55);
        assertEquals('F', g.getGrade());
    }

    @Test
    public void score50_att90_shouldBeF() {
        GradingCalculator g = new GradingCalculator(50, 90);
        assertEquals('F', g.getGrade());
    }
}

