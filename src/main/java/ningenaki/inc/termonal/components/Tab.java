package ningenaki.inc.termonal.components;

import java.util.HashSet;
import java.util.Set;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import ningenaki.inc.termonal.services.Words;

public class Tab implements Model {
    private final int width;
    private final int height;

    private final int tries;
    private final int TRIES_SINGLE = 6;
    private final int TRIES_DUO = 7;
    private final int TRIES_QUARTET = 9;
    private final int WORD_SIZE = 5;

    private final int GAP = 10;

    int cursorX = 0;
    int cursorY = 0;

    long lastBlink = 0;
    boolean blink = true;

    private final Box[] boxArray;
    private final Keyboard keyboard;
    private final MatrixStream matrixStream;
    private final Set<Character> usedLetters = new HashSet<>();

    public Tab(int width, int height, int wordCount, MatrixStream matrixStream) throws Exception {
        this.width = width;
        this.height = height;
        this.matrixStream = matrixStream;
        keyboard = new Keyboard(width);
        boxArray = new Box[wordCount];
        switch (wordCount) {
            case 1:
                this.tries = TRIES_SINGLE;
                boxArray[0] = new Box(WORD_SIZE, tries, 0);
                boxArray[0].setOrigin((width - boxArray[0].getWidth()) / 2, (height - boxArray[0].getHeight()) / 2);
                break;
            case 2:
                this.tries = TRIES_DUO;
                boxArray[0] = new Box(WORD_SIZE, tries, 1);
                boxArray[0].setOrigin(width / 2 - boxArray[0].getWidth() - GAP / 2,
                        (height - boxArray[0].getHeight()) / 2);
                boxArray[1] = new Box(WORD_SIZE, tries, 2);
                boxArray[1].setOrigin(width / 2 + GAP / 2, (height - boxArray[0].getHeight()) / 2);
                break;
            case 4:
                this.tries = TRIES_QUARTET;
                boxArray[0] = new Box(WORD_SIZE, tries, 3);
                boxArray[0].setOrigin(width / 2 - boxArray[0].getWidth() * 2 - 3 * GAP / 2,
                        (height - boxArray[0].getHeight()) / 2);
                boxArray[1] = new Box(WORD_SIZE, tries, 4);
                boxArray[1].setOrigin(width / 2 - boxArray[1].getWidth() - GAP / 2,
                        (height - boxArray[0].getHeight()) / 2);
                boxArray[2] = new Box(WORD_SIZE, tries, 5);
                boxArray[2].setOrigin(width / 2 + GAP / 2, (height - boxArray[0].getHeight()) / 2);
                boxArray[3] = new Box(WORD_SIZE, tries, 6);
                boxArray[3].setOrigin(width / 2 + boxArray[3].getWidth() + 3 * GAP / 2,
                        (height - boxArray[0].getHeight()) / 2);
                break;
            default:
                throw new Exception("Número de palavras inválido");
        }
    }

    public void moveCursor(int x) {
        cursorX = Math.max(0, Math.min(cursorX + x, WORD_SIZE - 1));
    }

    public void handleKey(String key) {
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
        String matrixView = matrixStream.view();
        String[] matrixLines = matrixView.split("\\R", -1);
        String[][] boxViews = new String[boxArray.length][];
        for (int i = 0; i < boxArray.length; i++) {
            boxArray[i].setCursorPosition(cursorX, cursorY, blink);
            boxViews[i] = boxArray[i].view().split("\\R", -1);
        }
        String[] keyboardView = keyboard.view().split("\\R", -1);
        StringBuilder output = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String renderedCharacter = " ";
                if (y < height - 4) {
                    renderedCharacter = renderedCell(matrixLines[y], x);
                    for (Box box : boxArray) {
                        if (box.isIn(y, x)) {
                            String[] boxLines = boxViews[indexOf(box)];
                            renderedCharacter = renderedCell(boxLines[y - box.getOriginY()], x - box.getOriginX());
                            break;
                        }
                    }
                } else {
                    int keyboardRow = y - (height - 4);
                    if (keyboardRow < keyboardView.length && x < keyboardView[keyboardRow].length()) {
                        renderedCharacter = renderedCell(keyboardView[keyboardRow], x);
                    }
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
        boolean isAnyValid = false;
        for (Box box : boxArray) {
            boolean isValid = Words.getInstance().isWordValid(box.getWord(cursorY));
            isAnyValid = isAnyValid || isValid;
            if (isValid) {
                box.submitWord(cursorY);
                String word = box.getWord(cursorY);
                if (word != null) {
                    for (char c : word.toCharArray()) {
                        char normalizedLetter = normalizeKeyboardLetter(c);
                        usedLetters.add(normalizedLetter);
                        keyboard.addUsedLetters(String.valueOf(normalizedLetter));
                    }
                }
            }
        }
        if (isAnyValid && cursorY + 1 < tries) {
            cursorY++;
            cursorX = 0;
        }
    }

    private char normalizeKeyboardLetter(char letter) {
        String normalized = Words.getInstance().normalize(String.valueOf(letter));
        return normalized.isEmpty() ? Character.toUpperCase(letter) : Character.toUpperCase(normalized.charAt(0));
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

}