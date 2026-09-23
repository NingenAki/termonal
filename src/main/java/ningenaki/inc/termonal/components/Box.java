package ningenaki.inc.termonal.components;

import java.util.Arrays;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import lombok.Getter;
import ningenaki.inc.termonal.enums.Styles;
import ningenaki.inc.termonal.singletons.Words;
import ningenaki.inc.termonal.states.BoxState;
import ningenaki.inc.termonal.states.LetterState;

@Getter
public class Box implements Model {
    private final int maxTries;
    private final int wordSize;
    private final int width;
    private final int height;
    private int originX = 0;
    private int originY = 0;
    private int cursorX;
    private int cursorY;
    private boolean cursorVisible;

    private int LEFT_BORDER = 1;
    private int UPPER_BORDER = 2;
    private int RIGHT_BORDER = 3;
    private int BOTTOM_BORDER = 2;
    private int LETTER_X_SIZE = 2;
    private int LETTER_Y_SIZE = 2;

    private final Character EMPTY = '_';

    private final char[][] guesses;

    private Words words = Words.getInstance();

    private final LetterState[][] letterState;
    public BoxState state;

    public Box(int wordSize, int maxTries, int wordIndex) {
        this.state = new BoxState(maxTries, wordIndex);
        this.maxTries = maxTries;
        this.wordSize = wordSize;
        width = LEFT_BORDER + wordSize * LETTER_X_SIZE + RIGHT_BORDER;
        height = UPPER_BORDER + maxTries * LETTER_Y_SIZE + BOTTOM_BORDER;
        guesses = new char[maxTries][wordSize];
        for (int i = 0; i < maxTries; i++) {
            Arrays.fill(guesses[i], EMPTY);
        }
        letterState = new LetterState[maxTries][wordSize];
        for (int i = 0; i < maxTries; i++) {
            Arrays.fill(letterState[i], LetterState.NEUTRAL);
        }
    }

    public void submitWord(int cursorY) {
        String word = getWord(cursorY);
        addAccents(cursorY, words.get(word));

        if (!state.isGameOver() && word != null)
            letterState[cursorY] = state.guess(word);
    }

    public void setOrigin(int x, int y) {
        originX = x;
        originY = y;
    }

    public void setCursorPosition(int cursorX, int cursorY, boolean visible) {
        this.cursorX = cursorX;
        this.cursorY = cursorY;
        cursorVisible = visible && !state.isGameOver();
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
        builder.append(Styles.BORDER.getStyle().render("▄".repeat(width - 1)));
        builder.append(Styles.BORDER.getStyle().render(" "));
        builder.append("\n");
        for (int y = 0; y < maxTries; y++) {
            builder.append(Styles.BORDER.getStyle().render("█"));
            builder.append(" ".repeat(width - LEFT_BORDER - RIGHT_BORDER));
            builder.append(Styles.BORDER.getStyle().render(" █░"));
            builder.append("\n");
            builder.append(Styles.BORDER.getStyle().render("█"));
            for (int x = 0; x < wordSize; x++) {
                builder.append(" ");
                if (cursorX == x && cursorY == y && cursorVisible)
                    builder.append(Styles.TEXT.getStyle().render("█"));
                else
                    builder.append((switch (getLetterState(x, y)) {
                        case LetterState.WRONG -> Styles.WRONG_LETTER.getStyle();
                        case LetterState.MISPLACED -> Styles.MISPLACED_LETTER.getStyle();
                        case LetterState.RIGHT -> Styles.RIGHT_LETTER.getStyle();
                        default -> Styles.TEXT.getStyle();
                    }).render(String.valueOf(guesses[y][x])));
            }
            builder.append(Styles.BORDER.getStyle().render(" █░"));
            builder.append("\n");
        }
        builder.append(Styles.BORDER.getStyle().render("█"));
        builder.append(" ".repeat(width - LEFT_BORDER - RIGHT_BORDER));
        builder.append(Styles.BORDER.getStyle().render(" █░"));
        builder.append("\n");
        builder.append(Styles.BORDER.getStyle().render("█"));
        builder.append(Styles.BORDER.getStyle().render("▄".repeat(width - LEFT_BORDER - RIGHT_BORDER)));
        builder.append(Styles.BORDER.getStyle().render("▄█░"));
        builder.append("\n");
        builder.append(Styles.BORDER.getStyle().render(" "));
        builder.append(Styles.BORDER.getStyle().render("░".repeat(width - LEFT_BORDER)));
        builder.append("\n");
        return builder.toString();
    }

    public char getChar(int x, int y) {
        return guesses[y][x];
    }

    public LetterState getLetterState(int x, int y) {
        if (y >= maxTries || x >= wordSize)
            return LetterState.NEUTRAL;
        return letterState[y][x];
    }

    public void addAccents(int cursorY, String word) {
        if (state.isWon())
            return;
        for (int x = 0; x < wordSize; x++) {
            guesses[cursorY][x] = word.charAt(x);
        }
    }

    public void setLetter(Character c, int cursorX, int cursorY) {
        if (state.isWon())
            return;
        if (c == null)
            guesses[cursorY][cursorX] = EMPTY;
        else
            guesses[cursorY][cursorX] = Character.toUpperCase(c);
    }

    public String getWord(int cursorY) {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < wordSize; x++)
            builder.append(guesses[cursorY][x]);
        String word = builder.toString();
        return word.contains(EMPTY.toString()) ? null : word;
    }
}