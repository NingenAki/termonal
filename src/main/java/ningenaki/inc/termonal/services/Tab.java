package ningenaki.inc.termonal.services;

import java.io.IOException;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
                boxArray[0].setOrigin(width / 2 - boxArray[0].getWidth() - GAP / 2, (height - boxArray[0].getHeight()) / 2);
                boxArray[1] = new Box(WORD_SIZE, tries, 2);
                boxArray[1].setOrigin(width / 2 + GAP / 2, (height - boxArray[0].getHeight()) / 2);
                break;
            case 4:
                this.tries = TRIES_QUARTET;
                boxArray[0] = new Box(WORD_SIZE, tries, 3);
                boxArray[0].setOrigin(width / 2 - boxArray[0].getWidth() * 2 - 3 * GAP / 2,
                        (height - boxArray[0].getHeight()) / 2);
                boxArray[1] = new Box(WORD_SIZE, tries, 4);
                boxArray[1].setOrigin(width / 2 - boxArray[1].getWidth() - GAP / 2, (height - boxArray[0].getHeight()) / 2);
                boxArray[2] = new Box(WORD_SIZE, tries, 5);
                boxArray[2].setOrigin(width / 2 + GAP / 2, (height - boxArray[0].getHeight()) / 2);
                boxArray[3] = new Box(WORD_SIZE, tries, 6);
                boxArray[3].setOrigin(width / 2 + boxArray[3].getWidth() + 3 * GAP / 2, (height - boxArray[0].getHeight()) / 2);
                break;
            default:
                throw new Exception("Número de palavras inválido");
        }
    }

    public void handleKeyStroke(KeyStroke keyStroke) {
        switch (keyStroke.getKeyType()) {
            case KeyType.Character:
                for (int i = 0; i < boxArray.length; i++) {
                    boxArray[i].setLetter(keyStroke.getCharacter(), cursorX, cursorY);
                }
            case KeyType.ArrowRight:
                if (cursorX + 1 < WORD_SIZE)
                    cursorX++;
                break;
            case KeyType.Backspace:
                for (int i = 0; i < boxArray.length; i++) {
                    boxArray[i].setLetter(null, cursorX, cursorY);
                }
            case KeyType.ArrowLeft:
                if (cursorX > 0)
                    cursorX--;
                break;
            case KeyType.Enter:
                boolean isAnyValid = false;
                for (Box box : boxArray) {
                    boolean isValid = Words.getInstance().isWordValid(box.getWord(cursorY));
                    isAnyValid = isAnyValid || isValid;
                    if (isValid)
                        box.submitWord(cursorY);
                }
                if (isAnyValid && cursorY + 1 < tries) {
                    cursorY++;
                    cursorX = 0;
                }
                break;
            default:
                break;
        }

    }

    public void draw(Terminal terminal, MatrixStream matrixStream) throws IOException {
        if (System.currentTimeMillis() - lastBlink > 500) {
            lastBlink = System.currentTimeMillis();
            blink = !blink;
        }
        terminal.clearScreen();
        terminal.setBackgroundColor(TextColor.ANSI.BLACK);
        terminal.setForegroundColor(TextColor.ANSI.GREEN);
        for (int y = 0; y < height; y++) {
            terminal.setCursorPosition(0, y);
            for (int x = 0; x < width; x++) {
                boolean isInABox = false;
                for (Box box : boxArray) {
                    if (box.isIn(y, x)) {
                        isInABox = true;
                        if (blink && !box.isWon() && box.offsetX(cursorX) == x && box.offsetY(cursorY) == y) {
                            terminal.setBackgroundColor(TextColor.ANSI.GREEN);
                            terminal.setForegroundColor(TextColor.ANSI.BLACK);
                            terminal.putCharacter(box.getChar(x, y));
                            terminal.setBackgroundColor(TextColor.ANSI.BLACK);
                            terminal.setForegroundColor(TextColor.ANSI.GREEN);
                        } else {
                            TextColor color = box.getLetterState(x, y).getColor();
                            if (color != null)
                                terminal.setForegroundColor(color);
                            terminal.putCharacter(box.getChar(x, y));
                            if (color != null)
                                terminal.setForegroundColor(TextColor.ANSI.GREEN);
                        }
                    }
                }
                if (!isInABox)
                    terminal.putCharacter(matrixStream.get(x, y));
            }
        }
        terminal.flush();
    }
}