package ningenaki.inc.termonal.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Window {
    int WIDTH = 200;
    int HEIGHT = 50;

    Terminal terminal;

    int cursor_x = 0;
    int cursor_y = 0;

    int WORD_SIZE = 5;
    int TRIES_SINGLE = 6;
    int TRIES_DUO = 7;
    int TRIES_QUARTET = 9;

    long lastBlink = 0;
    boolean blink = true;

    @Autowired
    private Words words;

    private MatrixStream matrixStream = new MatrixStream(WIDTH, HEIGHT);
    private Box box = new Box(WORD_SIZE, TRIES_SINGLE);

    public Window() {
        box.setOrigin((WIDTH - box.getWidth()) / 2, (HEIGHT - box.getHeight()) / 2);
        terminal = new DefaultTerminalFactory().setTerminalEmulatorTitle("Termonal")
                .setInitialTerminalSize(new TerminalSize(WIDTH, HEIGHT))
                .createTerminalEmulator();
    }

    public void getInput() throws IOException {
        KeyStroke keyStroke = terminal.pollInput();
        if (keyStroke != null) {
            log.info(keyStroke.toString());
            switch (keyStroke.getKeyType()) {
                case KeyType.EOF:
                    System.exit(0);
                    break;
                case KeyType.Character:
                    box.setLetter(keyStroke.getCharacter(), cursor_x, cursor_y);
                case KeyType.ArrowRight:
                    if (cursor_x + 1 < WORD_SIZE)
                        cursor_x++;
                    break;
                case KeyType.Backspace:
                    box.setLetter(null, cursor_x, cursor_y);
                case KeyType.ArrowLeft:
                    if (cursor_x > 0)
                        cursor_x--;
                    break;
                case KeyType.Enter:
                    String word = box.getWord(cursor_y);
                    if (words.isWordValid(word) && cursor_y + 1 < TRIES_SINGLE) {
                        box.addAccents(cursor_y, words.get(word));
                        if (!box.validateWord(words.getWordOfDay(0, true), word, cursor_y)) {
                            cursor_y++;
                            cursor_x = 0;
                        } else {
                            box.setWon(true);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void draw() throws IOException {
        if (System.currentTimeMillis() - lastBlink > 500) {
            lastBlink = System.currentTimeMillis();
            blink = !blink;
        }
        terminal.clearScreen();
        terminal.setBackgroundColor(TextColor.ANSI.BLACK);
        terminal.setForegroundColor(TextColor.ANSI.GREEN);
        for (int y = 0; y < HEIGHT; y++) {
            terminal.setCursorPosition(0, y);
            for (int x = 0; x < WIDTH; x++) {
                if (box.isIn(y, x)) {
                    if (blink && !box.isWon() && box.offsetX(cursor_x) == x && box.offsetY(cursor_y) == y) {
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

    @Scheduled(fixedRate = 100)
    private void update() {
        try {
            matrixStream.update();
            getInput();
            draw();
        } catch (IOException ex) {
            log.error("Erro ao printar no termonal", ex);
        }
    }
}