package ningenaki.inc.termonal.services;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Window {
    int WIDTH = 200;
    int HEIGHT = 50;

    int BOX_WIDTH = 15;
    int BOX_HEIGHT = 17;
    int BOX_OFFSET_X = (WIDTH - BOX_WIDTH) / 2;
    int BOX_OFFSET_Y = (HEIGHT - BOX_HEIGHT) / 2;

    Terminal terminal;
    char[][] box = new char[BOX_HEIGHT][BOX_WIDTH];

    int cursor_x = 2;
    int cursor_y = 2;

    @Autowired
    private Words words;

    private MatrixStream matrixStream = new MatrixStream(WIDTH, HEIGHT);

    @PostConstruct
    private void init() {
        Arrays.fill(box[0], '▄');
        box[0][BOX_WIDTH - 1] = ' ';
        for (int y = 1; y < BOX_HEIGHT - 2; y++) {
            Arrays.fill(box[y], ' ');
            box[y][0] = '█';
            box[y][BOX_WIDTH - 2] = '█';
            box[y][BOX_WIDTH - 1] = '░';
        }
        Arrays.fill(box[BOX_HEIGHT - 2], '▄');
        box[BOX_HEIGHT - 2][0] = '█';
        box[BOX_HEIGHT - 2][BOX_WIDTH - 2] = '█';
        box[BOX_HEIGHT - 2][BOX_WIDTH - 1] = '░';
        Arrays.fill(box[BOX_HEIGHT - 1], '░');
        box[BOX_HEIGHT - 1][0] = ' ';

        for (int j = 2; j < BOX_HEIGHT - 3; j += (BOX_HEIGHT - 5) / 6) {
            for (int i = 2; i < BOX_WIDTH - 3; i += (BOX_WIDTH - 5) / 5) {
                box[j][i] = '_';
            }
        }
    }

    private char getBoxChar(int x, int y) {
        return box[y - BOX_OFFSET_Y][x - BOX_OFFSET_X];
    }

    private boolean isInBox(int y, int x) {
        return y >= BOX_OFFSET_Y && y < BOX_OFFSET_Y + BOX_HEIGHT && x >= BOX_OFFSET_X && x < BOX_OFFSET_X + BOX_WIDTH;
    }

    @Scheduled(fixedRate = 100)
    public void draw() {
        try {
            if (terminal == null)
                terminal = new DefaultTerminalFactory().setTerminalEmulatorTitle("Termonal")
                        .setInitialTerminalSize(new TerminalSize(WIDTH, HEIGHT))
                        .createTerminalEmulator();

            matrixStream.update();
            terminal.pollInput();
            terminal.clearScreen();
            terminal.setForegroundColor(TextColor.ANSI.GREEN);
            for (int y = 0; y < HEIGHT; y++) {
                terminal.setCursorPosition(0, y);
                for (int x = 0; x < WIDTH; x++) {
                    if (isInBox(y, x))
                        terminal.putCharacter(getBoxChar(x, y));
                    else
                        terminal.putCharacter(matrixStream.get(x, y));
                }
            }
            terminal.setForegroundColor(TextColor.ANSI.DEFAULT);
            terminal.setCursorPosition(BOX_OFFSET_X + cursor_x, BOX_OFFSET_Y + cursor_y);
            terminal.flush();
        } catch (IOException ex) {
            log.error("Erro ao printar no termonal", ex);
        }
    }
}