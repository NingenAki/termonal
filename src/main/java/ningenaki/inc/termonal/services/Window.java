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

import jakarta.annotation.PostConstruct;
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
    int TRIES = 6;

    @Autowired
    private Words words;

    private MatrixStream matrixStream = new MatrixStream(WIDTH, HEIGHT);
    private Box box = new Box(WORD_SIZE, TRIES);

    @PostConstruct
    private void init() {
        box.setOrigin((WIDTH - box.getWidth()) / 2, (HEIGHT - box.getHeight()) / 2);
    }

    @Scheduled(fixedRate = 100)
    public void draw() {
        try {
            if (terminal == null)
                terminal = new DefaultTerminalFactory().setTerminalEmulatorTitle("Termonal")
                        .setInitialTerminalSize(new TerminalSize(WIDTH, HEIGHT))
                        .createTerminalEmulator();

            matrixStream.update();
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
                    default:
                        break;
                }
            }
            terminal.clearScreen();
            terminal.setBackgroundColor(TextColor.ANSI.BLACK);
            terminal.setForegroundColor(TextColor.ANSI.GREEN);
            for (int y = 0; y < HEIGHT; y++) {
                terminal.setCursorPosition(0, y);
                for (int x = 0; x < WIDTH; x++) {
                    if (box.isIn(y, x)) {
                        if (box.offsetX(cursor_x) == x && box.offsetY(cursor_y) == y) {
                            terminal.setBackgroundColor(TextColor.ANSI.GREEN);
                            terminal.setForegroundColor(TextColor.ANSI.BLACK);
                            terminal.putCharacter(box.getChar(x, y));
                            terminal.setBackgroundColor(TextColor.ANSI.BLACK);
                            terminal.setForegroundColor(TextColor.ANSI.GREEN);
                        } else {
                            terminal.putCharacter(box.getChar(x, y));
                        }
                    } else
                        terminal.putCharacter(matrixStream.get(x, y));
                }
            }
            terminal.flush();
        } catch (IOException ex) {
            log.error("Erro ao printar no termonal", ex);
        }
    }
}