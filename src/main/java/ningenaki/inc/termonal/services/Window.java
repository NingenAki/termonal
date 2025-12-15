package ningenaki.inc.termonal.services;

import java.util.Arrays;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class Window {
    int WIDTH = 80;
    int HEIGHT = 24;

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_COLOR_RESET = "\u001B[0m";
    public static final String ANSI_CLEAR = "\033[H\033[2J";

    char[][] matrix = new char[HEIGHT][WIDTH];

    @PostConstruct
    private void init() {
        for (int i = 0; i < HEIGHT; i++) {
            Arrays.fill(matrix[i], ' ');
        }
    }

    private void update() {
        for (int i = 0; i < HEIGHT - 1; i++) {
            for (int j = 0; j < WIDTH; j++) {
                matrix[i][j] = matrix[i + 1][j];
            }
        }
        for (int j = 0; j < WIDTH; j++) {
            switch (matrix[HEIGHT - 1][j]) {
                case 'T':
                    matrix[HEIGHT - 1][j] = 'E';
                    break;
                case 'E':
                    matrix[HEIGHT - 1][j] = 'R';
                    break;
                case 'R':
                    matrix[HEIGHT - 1][j] = 'M';
                    break;
                case 'M':
                    matrix[HEIGHT - 1][j] = 'O';
                    break;
                case 'O':
                    matrix[HEIGHT - 1][j] = 'N';
                    break;
                case 'N':
                    matrix[HEIGHT - 1][j] = 'A';
                    break;
                case 'A':
                    matrix[HEIGHT - 1][j] = 'L';
                    break;
                case 'L':
                    matrix[HEIGHT - 1][j] = ' ';
                    break;
                default:
                    matrix[HEIGHT - 1][j] = Math.random() < 0.005 ? 'T' : ' ';
                    break;
            }
        }
    }

    //@Scheduled(fixedRate = 100)
    public void draw() {
        update();

        System.out.println();
        StringBuilder builder = new StringBuilder();
        builder.append(ANSI_GREEN + ANSI_CLEAR);
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                builder.append(matrix[i][j]);
            }
            builder.append("\n");
        }
        builder.append(ANSI_COLOR_RESET);
        System.out.print(builder.toString());
    }
}