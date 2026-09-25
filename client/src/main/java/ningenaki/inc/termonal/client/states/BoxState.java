package ningenaki.inc.termonal.client.states;

import java.util.Arrays;

import lombok.Getter;
import ningenaki.inc.termonal.client.singletons.Words;

@Getter
public class BoxState {
    private boolean won;
    private int guesses;
    private int maxTries;
    private boolean gameOver;
    private String word;
    private int wordSize;

    public BoxState(int maxTries, int seed) {
        this.won = false;
        this.guesses = 0;
        this.maxTries = maxTries;
        this.gameOver = false;
        this.word = Words.getInstance().getWordOfDay(seed, true);
        this.wordSize = this.word.length();
    }

    public void win() {
        this.won = true;
        this.gameOver = true;
    }

    public void lose() {
        this.won = false;
        this.gameOver = true;
    }

    public LetterState[] guess(String guess) {
        char EMPTY = '_';
        char[] letters = this.word.toCharArray();
        this.guesses++;
        LetterState[] letterStates = new LetterState[wordSize];
        Arrays.fill(letterStates, LetterState.WRONG);
        for (int x = 0; x < wordSize; x++) {
            if (word.charAt(x) == guess.charAt(x)) {
                letterStates[x] = LetterState.RIGHT;
                letters[x] = EMPTY;
            }
        }
        for (int x = 0; x < wordSize; x++) {
            if (letterStates[x] != LetterState.RIGHT) {
                for (int _x = 0; _x < wordSize; _x++) {
                    if (letters[_x] == guess.charAt(x)) {
                        letterStates[x] = LetterState.MISPLACED;
                        letters[_x] = EMPTY;
                        break;
                    }
                }
            }
        }
        if (letterStates.length == 0
                || java.util.Arrays.stream(letterStates).allMatch(state -> state == LetterState.RIGHT)) {
            this.win();
        } else if (this.guesses >= this.maxTries) {
            this.lose();
        }
        return letterStates;
    }
}