package ningenaki.inc.termonal.services;

import java.util.Arrays;

import com.googlecode.lanterna.TextColor;

import lombok.Getter;
import lombok.Setter;

public class Box {
    @Getter
    @Setter
    private boolean won;
    private final int tries;
    private final int wordSize;
    @Getter
    private final int width;
    @Getter
    private final int height;
    private int originX = 0;
    private int originY = 0;

    private int LEFT_BORDER = 2;
    private int UPPER_BORDER = 2;
    private int RIGHT_BORDER = 2;
    private int BOTTOM_BORDER = 2;
    private int LETTER_X_SIZE = 2;
    private int LETTER_Y_SIZE = 2;

    private final Character EMPTY = '_';

    private final char[][] box;

    public static enum State {
        NEUTRAL(null),
        WRONG(TextColor.ANSI.BLACK_BRIGHT),
        ELSEWHERE(TextColor.ANSI.YELLOW_BRIGHT),
        RIGHT(TextColor.ANSI.GREEN_BRIGHT);

        @Getter
        private TextColor color;

        State(TextColor color) {
            this.color = color;
        }
    }

    private final State[][] letterState;

    public Box(int wordSize, int tries) {
        this.tries = tries;
        this.wordSize = wordSize;
        width = LEFT_BORDER + wordSize * LETTER_X_SIZE + RIGHT_BORDER;
        height = UPPER_BORDER + tries * LETTER_Y_SIZE + BOTTOM_BORDER;
        box = new char[height][width];
        letterState = new State[tries][wordSize];

        fillBoxWithBorders();
        initLetters();
    }

    private void initLetters() {
        for (int j = UPPER_BORDER; j < height - BOTTOM_BORDER; j += LETTER_Y_SIZE) {
            for (int i = LEFT_BORDER; i < width - RIGHT_BORDER; i += LETTER_X_SIZE) {
                box[j][i] = EMPTY;
            }
        }
        for (int j = 0; j < tries; j++) {
            for (int i = 0; i < wordSize; i++) {
                letterState[j][i] = State.NEUTRAL;
            }
        }
    }

    private void fillBoxWithBorders() {

        Arrays.fill(box[0], '▄');
        box[0][width - 1] = ' ';
        for (int y = 1; y < height - BOTTOM_BORDER; y++) {
            Arrays.fill(box[y], ' ');
            box[y][0] = '█';
            box[y][width - 2] = '█';
            box[y][width - 1] = '░';
        }
        Arrays.fill(box[height - BOTTOM_BORDER], '▄');
        box[height - 2][0] = '█';
        box[height - 2][width - 2] = '█';
        box[height - 2][width - 1] = '░';
        Arrays.fill(box[height - 1], '░');
        box[height - 1][0] = ' ';
    }

    public void setOrigin(int x, int y) {
        originX = x;
        originY = y;
    }

    public boolean isIn(int y, int x) {
        return y >= originY && y < originY + height && x >= originX
                && x < originX + width;
    }

    public int offsetX(int x) {
        return originX + innerOffsetX(x);
    }

    public int offsetY(int y) {
        return originY + innerOffsetY(y);
    }

    private int innerOffsetX(int x) {
        return LEFT_BORDER + x * LETTER_X_SIZE;
    }

    private int innerOffsetY(int y) {
        return UPPER_BORDER + y * LETTER_Y_SIZE;
    }

    public char getChar(int x, int y) {
        return box[y - originY][x - originX];
    }

    public State getLetterState(int x, int y) {
        return isLetter(x, y)
                ? letterState[outterToLetterIndexY(y)][outterToLetterIndexX(x)]
                : State.NEUTRAL;
    }

    private int outterToLetterIndexX(int x) {
        return (x - originX - LEFT_BORDER) / LETTER_X_SIZE;
    }

    private int outterToLetterIndexY(int y) {
        return (y - originY - UPPER_BORDER) / LETTER_Y_SIZE;
    }

    private boolean isLetter(int x, int y) {
        return x - originX - LEFT_BORDER >= 0 && x < originX + width - RIGHT_BORDER
                && (x - originX - LEFT_BORDER) % LETTER_X_SIZE == 0
                && y - originY - UPPER_BORDER >= 0 && y < originY + height - BOTTOM_BORDER
                && (y - originY - UPPER_BORDER) % LETTER_Y_SIZE == 0;
    }

    public boolean validateWord(String answer, String word, int cursorY) {
        if (won)
            return true;
        char[] letters = answer.toCharArray();
        boolean valid = true;
        for (int x = 0; x < wordSize; x++) {
            if (answer.charAt(x) == word.charAt(x)) {
                letterState[cursorY][x] = State.RIGHT;
                letters[x] = EMPTY;
            } else {
                valid = false;
            }
        }
        for (int x = 0; x < wordSize; x++) {
            boolean found = letterState[cursorY][x] != State.NEUTRAL;
            if (!found) {
                for (int _x = 0; _x < wordSize; _x++) {
                    if (letters[_x] == word.charAt(x)) {
                        letterState[cursorY][x] = State.ELSEWHERE;
                        letters[_x] = EMPTY;
                        found = true;
                        break;
                    }
                }
            }
            if (!found)
                letterState[cursorY][x] = State.WRONG;
        }
        return valid;
    }

    public void addAccents(int cursorY, String word) {
        if (won)
            return;
        for (int x = 0; x < wordSize; x++) {
            box[innerOffsetY(cursorY)][innerOffsetX(x)] = word.charAt(x);
        }
    }

    public void setLetter(Character c, int cursorX, int cursorY) {
        if (won)
            return;
        if (c == null)
            box[innerOffsetY(cursorY)][innerOffsetX(cursorX)] = EMPTY;
        else
            box[innerOffsetY(cursorY)][innerOffsetX(cursorX)] = Character.toUpperCase(c);
    }

    public String getWord(int cursorY) {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < wordSize; x++)
            builder.append(box[innerOffsetY(cursorY)][innerOffsetX(x)]);
        String word = builder.toString();
        return word.contains(EMPTY.toString()) ? null : word;
    }
}