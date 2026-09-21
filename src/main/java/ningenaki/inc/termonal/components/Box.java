package ningenaki.inc.termonal.components;

import java.util.Arrays;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import com.williamcallahan.tui4j.compat.lipgloss.Style;
import lombok.Getter;
import lombok.Setter;
import ningenaki.inc.termonal.States.BoxState;
import ningenaki.inc.termonal.States.Words;

public class Box implements Model {
    @Getter
    @Setter
    private boolean won;
    private final int tries;
    private final int wordSize;
    private final int wordIndex;
    @Getter
    private final int width;
    @Getter
    private final int height;
    private int originX = 0;
    private int originY = 0;
    private int cursorX;
    private int cursorY;
    private boolean cursorVisible;

    private int LEFT_BORDER = 2;
    private int UPPER_BORDER = 2;
    private int RIGHT_BORDER = 2;
    private int BOTTOM_BORDER = 2;
    private int LETTER_X_SIZE = 2;
    private int LETTER_Y_SIZE = 2;

    private final Character EMPTY = '_';

    private final char[][] box;

    private Words words = Words.getInstance();

    public static enum LetterState {
        NEUTRAL,
        WRONG,
        MISPLACED,
        RIGHT
    }

    private final LetterState[][] letterState;
    public BoxState state;

    public Box(int wordSize, int maxTries, int wordIndex) {
        this.state = new BoxState(maxTries, wordIndex);
        this.tries = maxTries;
        this.wordSize = wordSize;
        this.wordIndex = wordIndex;
        width = LEFT_BORDER + wordSize * LETTER_X_SIZE + RIGHT_BORDER;
        height = UPPER_BORDER + maxTries * LETTER_Y_SIZE + BOTTOM_BORDER;
        box = new char[height][width];
        letterState = new LetterState[maxTries][wordSize];

        fillBoxWithBorders();
        initLetters();
    }

    public void submitWord(int cursorY) {
        String word = getWord(cursorY);
        addAccents(cursorY, words.get(word));
        if(validateWord(word, cursorY)) {
            setWon(true);
        }
    }

    private void initLetters() {
        for (int j = UPPER_BORDER; j < height - BOTTOM_BORDER; j += LETTER_Y_SIZE) {
            for (int i = LEFT_BORDER; i < width - RIGHT_BORDER; i += LETTER_X_SIZE) {
                box[j][i] = EMPTY;
            }
        }
        for (int j = 0; j < tries; j++) {
            for (int i = 0; i < wordSize; i++) {
                letterState[j][i] = LetterState.NEUTRAL;
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

    public int getOriginX() {
        return originX;
    }

    public int getOriginY() {
        return originY;
    }

    public void setCursorPosition(int cursorX, int cursorY, boolean visible) {
        this.cursorX = cursorX;
        this.cursorY = cursorY;
        cursorVisible = visible;
    }

    @Override
    public Command init() {
        return Command.none();
    }

    @Override
    public UpdateResult<? extends Model> update(Message msg) {
        return UpdateResult.from(this);
    }

    @Override
    public String view() {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                builder.append(renderAt(originX + x, originY + y));
            }
            if (y < height - 1) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    public boolean isIn(int y, int x) {
        return y >= originY && y < originY + height && x >= originX
                && x < originX + width;
    }

    public boolean isBorder(int x, int y) {
        int localX = x - originX;
        int localY = y - originY;
        return localX == 0 || localX == width - 1 || localY == 0 || localY == height - 1
                || localX == width - 2 || localY == height - 2;
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

    public String renderAt(int x, int y) {
        if (cursorVisible && !won && x == offsetX(cursorX) && y == offsetY(cursorY)) {
            return "█";
        }
        String character = String.valueOf(getChar(x, y));
        if (isBorder(x, y) || character.equals(" ")) {
            return isBorder(x, y)
                    ? Style.newStyle().foreground(ColorPalette.SECONDARY).render(character)
                    : character;
        }

        LetterState state = getLetterState(x, y);
        if (state == LetterState.NEUTRAL) {
            return character;
        }
        return Style.newStyle().foreground(switch (state) {
            case WRONG -> ColorPalette.WRONG_LETTER;
            case MISPLACED -> ColorPalette.ELSEWHERE_LETTER;
            case RIGHT -> ColorPalette.RIGHT_LETTER;
            case NEUTRAL -> ColorPalette.PRIMARY;
        }).render(character);
    }

    public LetterState getLetterState(int x, int y) {
        return isLetter(x, y)
                ? letterState[outterToLetterIndexY(y)][outterToLetterIndexX(x)]
                : LetterState.NEUTRAL;
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

    public boolean validateWord(String word, int cursorY) {
        if (won)
            return true;
        letterState[cursorY] = state.guess(word);
        return state.isWon();
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