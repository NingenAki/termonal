package ningenaki.inc.termonal.services;

import java.util.HashSet;
import java.util.Set;

import com.williamcallahan.tui4j.compat.lipgloss.Style;

import ningenaki.inc.termonal.components.ColorPalette;

public class Tab {
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
    private final Set<Character> usedLetters = new HashSet<>();

    public Tab(int width, int height, int wordCount) throws Exception {
        this.width = width;
        this.height = height;
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
                        usedLetters.add(normalizeKeyboardLetter(c));
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

    public String view(MatrixStream matrixStream) {
        StringBuilder output = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String renderedCharacter = " ";
                if (y < height - 4) {
                    renderedCharacter = String.valueOf(matrixStream.get(x, y));
                    boolean renderedFromMatrix = true;
                    for (Box box : boxArray) {
                        if (box.isIn(y, x)) {
                            renderedCharacter = String.valueOf(box.getChar(x, y));
                            renderedFromMatrix = false;
                            if (box.isBorder(x, y)) {
                                renderedCharacter = renderBorder(box, x, y, renderedCharacter);
                            } else if (blink && !box.isWon() && box.offsetX(cursorX) == x && box.offsetY(cursorY) == y) {
                                renderedCharacter = "█";
                            } else {
                                renderedCharacter = renderLetter(box, x, y, renderedCharacter);
                            }
                            break;
                        }
                    }
                    if (renderedFromMatrix && !renderedCharacter.equals(" ")) {
                        renderedCharacter = Style.newStyle().foreground(ColorPalette.TERTIARY)
                                .render(renderedCharacter);
                    }
                } else {
                    renderedCharacter = renderKeyboardCell(x, y, renderedCharacter);
                }
                output.append(renderedCharacter);
            }
            if (y < height - 1) {
                output.append('\n');
            }
        }
        return output.toString();
    }

    private String renderKeyboardCell(int x, int y, String fallback) {
        String[] keyboardRows = new String[] { "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM" };
        int keyboardStartLine = height - 4;
        int rowIndex = y - keyboardStartLine;
        if (rowIndex < 0 || rowIndex >= keyboardRows.length) {
            return fallback;
        }

        String row = keyboardRows[rowIndex];
        int startX = Math.max(0, (width - (row.length() * 2 - 1)) / 2);
        for (int i = 0; i < row.length(); i++) {
            int keyX = startX + i * 2;
            if (x == keyX) {
                char letter = row.charAt(i);
                if (usedLetters.contains(letter)) {
                    return Style.newStyle().foreground(ColorPalette.MUTED).render(String.valueOf(letter));
                }
                return Style.newStyle().foreground(ColorPalette.DIM).render(String.valueOf(letter));
            }
            if (x == keyX + 1) {
                return " ";
            }
        }
        return fallback;
    }

    private String renderBorder(Box box, int x, int y, String character) {
        if (character.equals(" ")) {
            return character;
        }
        return Style.newStyle().foreground(ColorPalette.SECONDARY).render(character);
    }

    private String renderLetter(Box box, int x, int y, String character) {
        Box.State state = box.getLetterState(x, y);
        if (state == Box.State.NEUTRAL || character.equals(" ")) {
            return character;
        }
        return Style.newStyle().foreground(switch (state) {
            case WRONG -> ColorPalette.WRONG_LETTER;
            case ELSEWHERE -> ColorPalette.ELSEWHERE_LETTER;
            case RIGHT -> ColorPalette.RIGHT_LETTER;
            case NEUTRAL -> ColorPalette.PRIMARY;
        }).render(character);
    }

}