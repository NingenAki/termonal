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

    private final int WORD_SIZE = 5;
    private final int TRIES_SINGLE = 6;
    private final int TRIES_DUO = 7;
    private final int TRIES_QUARTET = 9;

    int cursorX = 0;
    int cursorY = 0;

    long lastBlink = 0;
    boolean blink = true;


    private Box box = new Box(WORD_SIZE, TRIES_SINGLE, 0);

    public Tab(int width, int height, int wordCount) {
        this.width = width;
        this.height = height;
        box.setOrigin((width - box.getWidth()) / 2, (height - box.getHeight()) / 2);
    }

    public void handleKeyStroke(KeyStroke keyStroke) {
        switch (keyStroke.getKeyType()) {
            case KeyType.Character:
                box.setLetter(keyStroke.getCharacter(), cursorX, cursorY);
            case KeyType.ArrowRight:
                if (cursorX + 1 < WORD_SIZE)
                    cursorX++;
                break;
            case KeyType.Backspace:
                box.setLetter(null, cursorX, cursorY);
            case KeyType.ArrowLeft:
                if (cursorX > 0)
                    cursorX--;
                break;
            case KeyType.Enter:
                if(box.submitWord(cursorY)) {
                    cursorY ++;
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
                if (box.isIn(y, x)) {
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
                } else
                    terminal.putCharacter(matrixStream.get(x, y));
            }
        }
        terminal.flush();
    }
}