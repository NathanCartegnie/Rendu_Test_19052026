package org.example;

public class DiceScore {

    private Ide de;

    public DiceScore(Ide deMock) {
        this.de = deMock;
    }

    public int getScore (){
        int scoreFirst = de.getRoll();
        int scoreSecond = de.getRoll();

        if(scoreFirst == scoreSecond){
            if(scoreFirst == 6){
                return 30;
            }
            return scoreFirst*2 + 10;
        }else{
            return scoreFirst < scoreSecond ? scoreSecond : scoreFirst;
        }
    }
}
