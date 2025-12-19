package ningenaki.inc.termonal.services;

import java.util.Arrays;

public class MatrixStream {
    private final int WIDTH;
    private final int HEIGHT;

    private final char[][] matrix;
    private final String[] wordPool;

    private Words words = new Words();

    public MatrixStream(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
        matrix = new char[HEIGHT][WIDTH];
        wordPool = new String[WIDTH];
        for (int y = 0; y < HEIGHT; y++) {
            Arrays.fill(matrix[y], ' ');
        }
        Arrays.fill(wordPool, "");
    }

    public char get(int x, int y) {
        if (x > 0 && x < WIDTH && y > 0 && y < HEIGHT)
            return matrix[y][x];
        return ' ';
    }

    public void update() {
        for (int y = 0; y < HEIGHT - 1; y++) {
            for (int x = 0; x < WIDTH; x++) {
                matrix[y][x] = matrix[y + 1][x];
            }
        }
        for (int x = 0; x < WIDTH; x++) {
            if (matrix[HEIGHT - 1][x] != ' ') {
                if (wordPool[x].isEmpty())
                    matrix[HEIGHT - 1][x] = ' ';
                else {
                    char c = wordPool[x].charAt(0);
                    wordPool[x] = wordPool[x].substring(1);
                    matrix[HEIGHT - 1][x] = c;
                }
            } else if (shouldStreamNewWord(x)) {
                String word = words.getRandomWord();
                matrix[HEIGHT - 1][x] = word.charAt(0);
                wordPool[x] = word.substring(1);
            } else {
                matrix[HEIGHT - 1][x] = ' ';
            }
        }
    }

    private boolean shouldStreamNewWord(int x) {
        if (x > 1 && (matrix[HEIGHT - 1][x - 2] != ' ' || matrix[HEIGHT - 2][x - 2] != ' '))
            return false;
        if (x > 0 && (matrix[HEIGHT - 1][x - 1] != ' ' || matrix[HEIGHT - 2][x - 1] != ' '))
            return false;
        if (x < WIDTH - 1 && matrix[HEIGHT - 1][x + 1] != ' ')
            return false;
        if (x < WIDTH - 2 && matrix[HEIGHT - 1][x + 2] != ' ')
            return false;
        return Math.random() < 0.005;
    }
}