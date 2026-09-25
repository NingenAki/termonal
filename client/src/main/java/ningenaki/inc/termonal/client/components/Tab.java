package ningenaki.inc.termonal.client.components;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import com.williamcallahan.tui4j.compat.lipgloss.Style;

import ningenaki.inc.termonal.client.enums.Styles;
import ningenaki.inc.termonal.client.singletons.Words;
import ningenaki.inc.termonal.client.states.TabState;
import ningenaki.inc.termonal.client.utils.StringUtils;

public class Tab implements Model {
    private final int width;
    private final int height;
    private final int footer_height;

    private final int maxTries;
    private final int WORD_SIZE = 5;

    private final int GAP = 10;

    int cursorX = 0;
    int cursorY = 0;

    long lastBlink = 0;
    boolean blink = true;

    private final Box[] boxes;
    private final Keyboard keyboard;
    private final MatrixStream matrixStream;
    public final TabState state;

    public Tab(int width, int height, int wordCount, MatrixStream matrixStream) throws Exception {
        this.matrixStream = matrixStream;
        this.state = new TabState(wordCount);
        keyboard = new Keyboard(width);
        footer_height = keyboard.getHeight() + 2;
        this.width = width;
        this.height = height - footer_height;
        boxes = new Box[wordCount];
        int originX = 0;
        int originY = 0;
        switch (wordCount) {
            case 1:
                this.maxTries = TabState.triesFor(wordCount);
                Box box = new Box(WORD_SIZE, maxTries, 0);
                originX = (this.width - box.getWidth()) / 2;
                originY = (this.height - box.getHeight()) / 2;
                box.setOrigin(originX, originY);
                boxes[0] = box;
                state.setBoxState(0, box.state);
                break;
            case 2:
                this.maxTries = TabState.triesFor(wordCount);
                Box box1 = new Box(WORD_SIZE, maxTries, 1);
                originX = this.width / 2 - box1.getWidth() - GAP / 2;
                originY = (this.height - box1.getHeight()) / 2;
                box1.setOrigin(originX, originY);
                state.setBoxState(0, box1.state);
                boxes[0] = box1;
                Box box2 = new Box(WORD_SIZE, maxTries, 2);
                originX += box1.getWidth() + GAP;
                box2.setOrigin(originX, originY);
                state.setBoxState(1, box2.state);
                boxes[1] = box2;
                break;
            case 4:
                this.maxTries = TabState.triesFor(wordCount);
                Box box3 = new Box(WORD_SIZE, maxTries, 3);
                originX = this.width / 2 - box3.getWidth() * 2 - 3 * GAP / 2;
                originY = (this.height - box3.getHeight()) / 2;
                box3.setOrigin(originX, originY);
                state.setBoxState(0, box3.state);
                boxes[0] = box3;
                Box box4 = new Box(WORD_SIZE, maxTries, 4);
                originX += box3.getWidth() + GAP;
                box4.setOrigin(originX, originY);
                state.setBoxState(1, box4.state);
                boxes[1] = box4;
                Box box5 = new Box(WORD_SIZE, maxTries, 5);
                originX += box4.getWidth() + GAP;
                box5.setOrigin(originX, originY);
                state.setBoxState(2, box5.state);
                boxes[2] = box5;
                Box box6 = new Box(WORD_SIZE, maxTries, 6);
                originX += box5.getWidth() + GAP;
                box6.setOrigin(originX, originY);
                state.setBoxState(3, box6.state);
                boxes[3] = box6;
                break;
            default:
                throw new Exception("Número de palavras inválido");
        }
    }

    public void moveCursor(int x) {
        cursorX = Math.max(0, Math.min(cursorX + x, WORD_SIZE - 1));
    }

    public void handleKey(String key) {
        if (state.isGameOver()) {
            return;
        }
        switch (key) {
            case "right":
                moveCursor(1);
                break;
            case "left":
                moveCursor(-1);
                break;
            case "backspace":
                handleCharacter(null, -1);
                break;
            case "delete":
                handleCharacter(null, 1);
                break;
            case "enter":
                submitCurrentWord();
                break;
            default:
                break;
        }
    }

    public void handleCharacter(Character character, int move) {
        if (state.isGameOver()) {
            return;
        }
        for (int i = 0; i < boxes.length; i++) {
            boxes[i].setLetter(character, cursorX, cursorY);
        }
        moveCursor(move);
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
        return state.isGameOver()
                ? renderResultScreen()
                : width >= boxes.length * (boxes[0].getWidth() + GAP) - GAP
                        ? renderBoard()
                        : renderError("Window is too small for this mode");
    }

    public void updateCursor() {
        lastBlink++;
        if (lastBlink > 5) {
            lastBlink = 0;
            blink = !blink;
        }
    }

    private String renderError(String msg) {
        StringBuilder result = new StringBuilder();
        for (int y = 0; y < height / 2; y++) {
            result.append(" ".repeat(width));
            result.append('\n');
        }
        result.append(StringUtils.center(msg, width, ' '));
        result.append('\n');
        for (int y = height / 2 + 1; y < height; y++) {
            result.append(" ".repeat(width));
            result.append('\n');
        }
        return result.toString();
    }

    private String renderBoard() {
        StringBuilder result = new StringBuilder();
        String[] matrixLines = matrixStream.view().split("\n");
        String[][] boxLines = new String[boxes.length][boxes[0].getHeight()];
        for (int i = 0; i < boxes.length; i++) {
            boxes[i].setCursorPosition(cursorX, cursorY, blink);
            boxLines[i] = boxes[i].view().split("\n");
        }

        int boxesYStart = boxes[0].getOriginY();
        int boxesYEnd = boxes[0].getOriginY() + boxes[0].getHeight();
        for (int y = 0; y < boxesYStart; y++) {
            result.append(Styles.MATRIX.getStyle().render(matrixLines[y]));
            result.append('\n');
        }
        for (int y = boxesYStart; y < boxesYEnd; y++) {
            int lastBoxXEnd = 0;
            for (int i = 0; i < boxes.length; i++) {
                result.append(
                        Styles.MATRIX.getStyle().render(matrixLines[y].substring(lastBoxXEnd, boxes[i].getOriginX())));
                result.append(boxLines[i][y - boxesYStart]);
                lastBoxXEnd = boxes[i].getOriginX() + boxes[i].getWidth();
            }
            result.append(Styles.MATRIX.getStyle().render(matrixLines[y].substring(lastBoxXEnd)));
            result.append('\n');
        }
        for (int y = boxesYEnd; y < height; y++) {
            result.append(Styles.MATRIX.getStyle().render(matrixLines[y]));
            result.append('\n');
        }
        result.append(footer());
        return result.toString();
    }

    private void submitCurrentWord() {
        if (state.isGameOver())
            return;
        boolean isAnyValid = false;
        for (Box box : boxes) {
            String word = box.getWord(cursorY);
            boolean isValid = Words.getInstance().isWordValid(word);
            isAnyValid = isAnyValid || isValid;
            if (isValid) {
                keyboard.addUsedLetters(word);
                box.submitWord(cursorY);
            }
        }
        if (isAnyValid) {
            state.guess();
            if (!state.isGameOver() && cursorY + 1 < state.getMaxTries()) {
                cursorY++;
                cursorX = 0;
            }
        }
    }

    private String winningMsg() {
        StringBuilder result = new StringBuilder();
        result.append("YOU WIN\n");
        result.append('\n');
        result.append("All words solved\n");
        return result.toString();
    }

    private String gameOverMsg() {
        StringBuilder result = new StringBuilder();
        result.append("GAME OVER\n");
        result.append('\n');
        result.append("No more attempts\n");
        return result.toString();
    }

    private String renderResultScreen() {
        Style title = (state.isWon() ? Styles.WIN : Styles.GAME_OVER).getStyle();

        String[] matrixLines = matrixStream.view().split("\n");
        String[] lines = (state.isWon() ? winningMsg() : gameOverMsg()).split("\n");
        StringBuilder result = new StringBuilder();

        int _height = lines.length;
        int _width = 0;
        for (String line : lines) {
            _width = Integer.max(line.length(), _width);
        }
        int originX = Math.max(0, (width - _width) / 2);
        int originY = Math.max(0, (height - _height) / 2);
        for (int y = 0; y < originY; y++) {
            result.append(Styles.MATRIX.getStyle().render(matrixLines[y]));
            result.append('\n');
        }
        for (int y = originY; y < originY + _height; y++) {
            int padding = (_width - lines[y - originY].length()) / 2;
            result.append(Styles.MATRIX.getStyle().render(matrixLines[y].substring(0, originX)));
            result.append(" ".repeat(padding));
            if (y == originY)
                result.append(title.render(lines[0]));
            else
                result.append(Styles.TEXT.getStyle().render(lines[y - originY]));
            result.append(" ".repeat(_width - lines[y - originY].length() - padding));
            result.append(Styles.MATRIX.getStyle().render(matrixLines[y].substring(originX + _width)));
            result.append('\n');
        }
        for (int y = originY + _height; y < height; y++) {
            result.append(Styles.MATRIX.getStyle().render(matrixLines[y]));
            result.append('\n');
        }
        result.append(footer());
        return result.toString();
    }

    private String footer() {
        StringBuilder result = new StringBuilder();
        if (!state.isGameOver())
            result.append(keyboard.view());
        else
            for (int y = 0; y < keyboard.getHeight(); y++) {
                result.append(" ".repeat(width));
                result.append('\n');
            }
        result.append(Styles.TEXT.getStyle().render(command()));
        return result.toString();
    }

    private String command() {
        String comands = state.isGameOver()
                ? "TAB switch   ESC quit"
                : "←/→ move   ENTER submit   TAB switch   ESC quit";

        int padding = Math.max(0, (width - comands.length()) / 2);
        StringBuilder result = new StringBuilder();
        result.append(" ".repeat(width));
        result.append('\n');
        result.append(" ".repeat(padding));
        result.append(comands);
        result.append(" ".repeat(width - comands.length() - padding));
        result.append('\n');
        return result.toString();
    }
}