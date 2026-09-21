package ningenaki.inc.termonal.components;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import com.williamcallahan.tui4j.compat.lipgloss.Style;

import ningenaki.inc.termonal.States.TabState;
import ningenaki.inc.termonal.States.Words;

public class Tab implements Model {
    private final int width;
    private final int height;

    private final int maxTries;
    private final int WORD_SIZE = 5;

    private final int GAP = 10;

    int cursorX = 0;
    int cursorY = 0;

    long lastBlink = 0;
    boolean blink = true;

    private final Box[] boxArray;
    private final Keyboard keyboard;
    private final MatrixStream matrixStream;
    public final TabState state;

    public Tab(int width, int height, int wordCount, MatrixStream matrixStream) throws Exception {
        this.width = width;
        this.height = height;
        this.matrixStream = matrixStream;
        this.state = new TabState(wordCount);
        keyboard = new Keyboard(width);
        boxArray = new Box[wordCount];
        int originX = 0;
        int originY = 0;
        switch (wordCount) {
            case 1:
                this.maxTries = TabState.triesFor(wordCount);
                Box box = new Box(WORD_SIZE, maxTries, 0);
                originX = (width - box.getWidth()) / 2;
                originY = (height - box.getHeight()) / 2;
                box.setOrigin(originX, originY);
                boxArray[0] = box;
                state.setBoxState(0, box.state);
                break;
            case 2:
                this.maxTries = TabState.triesFor(wordCount);
                Box box1 = new Box(WORD_SIZE, maxTries, 1);
                originX = width / 2 - box1.getWidth() - GAP / 2;
                originY = (height - box1.getHeight()) / 2;
                box1.setOrigin(originX, originY);
                state.setBoxState(0, box1.state);
                boxArray[0] = box1;
                Box box2 = new Box(WORD_SIZE, maxTries, 2);
                originX += box1.getWidth() + GAP;
                box2.setOrigin(originX, originY);
                state.setBoxState(1, box2.state);
                boxArray[1] = box2;
                break;
            case 4:
                this.maxTries = TabState.triesFor(wordCount);
                Box box3 = new Box(WORD_SIZE, maxTries, 3);
                originX = width / 2 - box3.getWidth() * 2 - 3 * GAP / 2;
                originY = (height - box3.getHeight()) / 2;
                box3.setOrigin(originX, originY);
                state.setBoxState(0, box3.state);
                boxArray[0] = box3;
                Box box4 = new Box(WORD_SIZE, maxTries, 4);
                originX += box3.getWidth() + GAP;
                box4.setOrigin(originX, originY);
                state.setBoxState(1, box4.state);
                boxArray[1] = box4;
                Box box5 = new Box(WORD_SIZE, maxTries, 5);
                originX += box4.getWidth() + GAP;
                box5.setOrigin(originX, originY);
                state.setBoxState(2, box5.state);
                boxArray[2] = box5;
                Box box6 = new Box(WORD_SIZE, maxTries, 6);
                originX += box5.getWidth() + GAP;
                box6.setOrigin(originX, originY);
                state.setBoxState(3, box6.state);
                boxArray[3] = box6;
                break;
            default:
                throw new Exception("Número de palavras inválido");
        }
    }

    public void moveCursor(int x) {
        cursorX = Math.max(0, Math.min(cursorX + x, WORD_SIZE - 1));
    }

    public void handleKey(String key) {
        if (state.isWon() || state.isGameOver()) {
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
        if (state.isWon() || state.isGameOver()) {
            return;
        }
        for (int i = 0; i < boxArray.length; i++) {
            boxArray[i].setLetter(character, cursorX, cursorY);
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
        if (state.isWon() || state.isGameOver()) {
            return renderResultScreen();
        }
        String matrixView = matrixStream.view();
        String[] matrixLines = matrixView.split("\\R", -1);
        String[][] matrixRows = new String[height - 4][];
        for (int y = 0; y < matrixRows.length; y++) {
            matrixRows[y] = renderedCells(renderMatrixRow(matrixLines, y, "",
                    Style.newStyle().foreground(ColorPalette.PRIMARY)));
        }
        String[][][] boxViews = new String[boxArray.length][][];
        for (int i = 0; i < boxArray.length; i++) {
            boxArray[i].setCursorPosition(cursorX, cursorY, blink);
            boxViews[i] = renderedLines(boxArray[i].view());
        }
        String[][] keyboardView = renderedLines(keyboard.view());
        StringBuilder output = new StringBuilder();
        int keyboardStart = Math.max(0, height - 5);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String renderedCharacter = " ";
                if (y < keyboardStart) {
                    renderedCharacter = cellAt(matrixRows[y], x);
                    for (Box box : boxArray) {
                        if (box.isIn(y, x)) {
                            String[][] boxLines = boxViews[indexOf(box)];
                            renderedCharacter = cellAt(boxLines[y - box.getOriginY()], x - box.getOriginX());
                            break;
                        }
                    }
                } else if (y < height - 1) {
                    int keyboardRow = y - keyboardStart;
                    if (keyboardRow < keyboardView.length) {
                        renderedCharacter = cellAt(keyboardView[keyboardRow], x);
                    }
                } else {
                    renderedCharacter = renderedCell(commandFooter(), x);
                }
                output.append(renderedCharacter);
            }
            if (y < height - 1) {
                output.append('\n');
            }
        }
        return output.toString();
    }

    public void updateCursor() {
        lastBlink++;
        if (lastBlink > 5) {
            lastBlink = 0;
            blink = !blink;
        }
    }

    private void submitCurrentWord() {
        if (state.isWon() || state.isGameOver()) {
            return;
        }
        boolean isAnyValid = false;
        for (Box box : boxArray) {
            String word = box.getWord(cursorY);
            boolean isValid = Words.getInstance().isWordValid(word);
            isAnyValid = isAnyValid || isValid;
            if (isValid) {
                keyboard.addUsedLetters(word);
                box.submitWord(cursorY);
            }
        }
        if (!isAnyValid) {
            return;
        }

        state.guess();
        if (!state.isWon() && !state.isGameOver() && cursorY + 1 < state.getMaxTries()) {
            cursorY++;
            cursorX = 0;
        }
    }

    private String renderResultScreen() {
        String title = state.isWon() ? "YOU WIN" : "GAME OVER";
        String message = state.isWon() ? "All words solved" : "No more attempts";
        String[] matrixLines = matrixStream.view().split("\\R", -1);
        StringBuilder output = new StringBuilder();
        int titleRow = Math.max(0, height / 2 - 1);
        int messageRow = Math.min(height - 2, titleRow + 2);
        for (int y = 0; y < height; y++) {
            String overlay = y == titleRow ? title
                    : y == messageRow ? message
                            : y == height - 1 ? commandFooter() : "";
            var overlayStyle = y == titleRow
                    ? Style.newStyle().foreground(state.isWon() ? ColorPalette.RIGHT_LETTER : ColorPalette.ACCENT)
                    : Style.newStyle().foreground(ColorPalette.PRIMARY);
            output.append(renderMatrixRow(matrixLines, y, overlay, overlayStyle));
            if (y < height - 1) {
                output.append('\n');
            }
        }
        return output.toString();
    }

    private String commandFooter() {
        return center("←/→ move   ENTER submit   TAB switch   ESC quit");
    }

    private String center(String string) {
        int padding = Math.max(0, (width - string.length()) / 2);
        StringBuilder centered = new StringBuilder();
        for (int i = 0; i < padding; i++) {
            centered.append(' ');
        }
        centered.append(string);
        return centered.toString();
    }

    private String renderMatrixRow(String[] matrixLines, int row, String overlay, Style overlayStyle) {
        String matrixLine = row < matrixLines.length ? matrixLines[row] : "";
        String[] matrixCells = renderedCells(matrixLine);
        int padding = Math.max(0, (width - overlay.length()) / 2);
        StringBuilder line = new StringBuilder();
        for (int x = 0; x < width; x++) {
            if (!overlay.isEmpty() && x >= padding && x < padding + overlay.length()) {
                line.append(overlayStyle.render(String.valueOf(overlay.charAt(x - padding))));
            } else {
                line.append(cellAt(matrixCells, x));
            }
        }
        return line.toString();
    }

    private int indexOf(Box box) {
        for (int i = 0; i < boxArray.length; i++) {
            if (boxArray[i] == box) {
                return i;
            }
        }
        return -1;
    }

    private String renderedCell(String line, int column) {
        StringBuilder styles = new StringBuilder();
        int visibleColumn = 0;
        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (character == '\u001B' && index + 1 < line.length() && line.charAt(index + 1) == '[') {
                int end = index + 2;
                while (end < line.length() && !Character.isLetter(line.charAt(end))) {
                    end++;
                }
                if (end < line.length()) {
                    styles.append(line, index, end + 1);
                    index = end;
                }
                continue;
            }
            if (visibleColumn++ == column) {
                return styles.append(character).append("\u001B[0m").toString();
            }
        }
        return " ";
    }

    private String[][] renderedLines(String view) {
        String[] lines = view.split("\\R", -1);
        String[][] renderedLines = new String[lines.length][];
        for (int i = 0; i < lines.length; i++) {
            renderedLines[i] = renderedCells(lines[i]);
        }
        return renderedLines;
    }

    private String[] renderedCells(String line) {
        String[] cells = new String[width];
        StringBuilder styles = new StringBuilder();
        int visibleColumn = 0;
        for (int index = 0; index < line.length() && visibleColumn < width; index++) {
            char character = line.charAt(index);
            if (character == '\u001B' && index + 1 < line.length() && line.charAt(index + 1) == '[') {
                int end = index + 2;
                while (end < line.length() && !Character.isLetter(line.charAt(end))) {
                    end++;
                }
                if (end < line.length()) {
                    styles.append(line, index, end + 1);
                    index = end;
                }
                continue;
            }
            cells[visibleColumn++] = styles + String.valueOf(character) + "\u001B[0m";
        }
        return cells;
    }

    private String cellAt(String[] cells, int column) {
        return column >= 0 && column < cells.length && cells[column] != null ? cells[column] : " ";
    }

}