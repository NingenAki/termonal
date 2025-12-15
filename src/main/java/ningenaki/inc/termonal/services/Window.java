package ningenaki.inc.termonal.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    char[][] matrix = new char[HEIGHT][WIDTH];
    char[][] box = new char[BOX_HEIGHT][BOX_WIDTH];
    String[] words = new String[WIDTH];

    int cursor_x = 2;
    int cursor_y = 2;

    ObjectMapper mapper = new ObjectMapper();
    File file = new File("src/main/resources/palavras5Letras.json");
    List<String> palavras5Letras;

    @PostConstruct
    private void init() {
        for (int y = 0; y < HEIGHT; y++) {
            Arrays.fill(matrix[y], ' ');
        }
        Arrays.fill(box[0], '█');
        box[0][BOX_WIDTH - 1] = ' ';
        for (int y = 1; y < BOX_HEIGHT - 2; y++) {
            Arrays.fill(box[y], ' ');
            box[y][0] = '█';
            box[y][BOX_WIDTH - 2] = '█';
            box[y][BOX_WIDTH - 1] = '░';
        }
        Arrays.fill(box[BOX_HEIGHT - 2], '█');
        box[BOX_HEIGHT - 2][BOX_WIDTH - 1] = '░';
        Arrays.fill(box[BOX_HEIGHT - 1], '░');
        box[BOX_HEIGHT - 1][0] = ' ';

        for (int j = 2; j < BOX_HEIGHT - 3; j += (BOX_HEIGHT - 5) / 6) {
            for (int i = 2; i < BOX_WIDTH - 3; i += (BOX_WIDTH - 5) / 5) {
                box[j][i] = '_';
            }
        }

        Arrays.fill(words, "");
    }

    private void update() {
        for (int y = 0; y < HEIGHT - 1; y++) {
            for (int x = 0; x < WIDTH; x++) {
                matrix[y][x] = matrix[y + 1][x];
            }
        }
        for (int x = 0; x < WIDTH; x++) {
            if (matrix[HEIGHT - 1][x] != ' ') {
                if (words[x].isEmpty())
                    matrix[HEIGHT - 1][x] = ' ';
                else {
                    char c = words[x].charAt(0);
                    words[x] = words[x].substring(1);
                    matrix[HEIGHT - 1][x] = c;
                }
            } else if (shouldStreamNewWord(x)) {
                String word = getRandomWord();
                matrix[HEIGHT - 1][x] = word.charAt(0);
                words[x] = word.substring(1);
            } else {
                matrix[HEIGHT - 1][x] = ' ';
            }
        }
    }

    private String getRandomWord() {
        try {
            if (palavras5Letras == null) {
                palavras5Letras = mapper.readValue(file, new TypeReference<List<String>>() {
                });
            }
            int r = (int) (Math.random() * 1000);
            return palavras5Letras.get(r).toUpperCase();
        } catch (Exception ex) {
            log.error("Erro gerando palavras", ex);
            return "termo";
        }
    }

    private boolean shouldStreamNewWord(int x) {
        if (x > 1 && matrix[HEIGHT - 1][x - 2] != ' ' && matrix[HEIGHT - 2][x - 2] != ' ')
            return false;
        if (x > 0 && matrix[HEIGHT - 1][x - 1] != ' ' && matrix[HEIGHT - 2][x - 1] != ' ')
            return false;
        if (x < WIDTH - 1 && matrix[HEIGHT - 1][x + 1] != ' ')
            return false;
        if (x < WIDTH - 2 && matrix[HEIGHT - 1][x + 2] != ' ')
            return false;
        return Math.random() < 0.005;
    }

    private char getBoxChar(int y, int x) {
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

            update();
            terminal.clearScreen();
            terminal.setForegroundColor(TextColor.ANSI.GREEN);
            for (int y = 0; y < HEIGHT; y++) {
                terminal.setCursorPosition(0, y);
                for (int x = 0; x < WIDTH; x++) {
                    if (isInBox(y, x))
                        terminal.putCharacter(getBoxChar(y, x));
                    else
                        terminal.putCharacter(matrix[y][x]);
                }
            }
            terminal.setForegroundColor(TextColor.ANSI.DEFAULT);
            terminal.flush();
            terminal.setCursorPosition(BOX_OFFSET_X + cursor_x, BOX_OFFSET_Y + cursor_y);
        } catch (IOException ex) {
            log.error("Erro ao printar no termonal", ex);
        }
    }
}