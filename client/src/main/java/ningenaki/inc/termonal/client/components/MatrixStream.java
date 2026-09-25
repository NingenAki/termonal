package ningenaki.inc.termonal.client.components;

import java.util.Arrays;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;

import lombok.Getter;
import ningenaki.inc.termonal.client.singletons.Words;

@Getter
public class MatrixStream implements Model {
    private final int width;
    private final int height;
    private final char[][] matrix;
    private final String[] wordPool;
    private final Words words = Words.getInstance();

    public MatrixStream(int width, int height) {
        this.width = width;
        this.height = height;
        matrix = new char[height][width];
        wordPool = new String[width];
        for (char[] row : matrix) {
            Arrays.fill(row, ' ');
        }
        Arrays.fill(wordPool, "");
    }

    public char get(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return ' ';
        }
        return matrix[y][x];
    }

    @Override
    public Command init() {
        return Command.none();
    }

    @Override
    public UpdateResult<? extends Model> update(Message msg) {
        for (int y = 0; y < height - 1; y++) {
            System.arraycopy(matrix[y + 1], 0, matrix[y], 0, width);
        }
        for (int x = 0; x < width; x++) {
            if (!wordPool[x].isEmpty()) {
                matrix[height - 1][x] = wordPool[x].charAt(0);
                wordPool[x] = wordPool[x].substring(1);
            } else if (shouldStreamNewWord(x)) {
                String word = words.getRandomWord();
                matrix[height - 1][x] = word.charAt(0);
                wordPool[x] = word.substring(1);
            } else {
                matrix[height - 1][x] = ' ';
            }
        }
        return UpdateResult.from(this);
    }

    @Override
    public String view() {
        StringBuilder output = new StringBuilder();
        for (int y = 0; y < height; y++) {
            output.append(new String(matrix[y]));
            if (y < height - 1) {
                output.append('\n');
            }
        }
        return output.toString();
    }

    private boolean shouldStreamNewWord(int x) {
        if (x > 1 && hasCharacter(x - 2)) {
            return false;
        }
        if (x > 0 && hasCharacter(x - 1)) {
            return false;
        }
        if (x < width - 1 && matrix[height - 1][x + 1] != ' ') {
            return false;
        }
        if (x < width - 2 && matrix[height - 1][x + 2] != ' ') {
            return false;
        }
        return Math.random() < 0.005;
    }

    private boolean hasCharacter(int x) {
        return matrix[height - 1][x] != ' ' || matrix[height - 2][x] != ' ';
    }
}
