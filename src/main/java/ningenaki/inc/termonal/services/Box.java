package ningenaki.inc.termonal.services;

import java.util.Arrays;

import lombok.Getter;

public class Box {
    @Getter
    private final int width;
    @Getter
    private final int height;
    private int originX = 0;
    private int originY = 0;

    private final char VAZIO = '_';

    private final char[][] box;

    public Box(int wordSize, int tries) {
        width = 4 + wordSize * 2;
        height = 4 + tries * 2;
        box = new char[height][width];

        Arrays.fill(box[0], '▄');
        box[0][width - 1] = ' ';
        for (int y = 1; y < height - 2; y++) {
            Arrays.fill(box[y], ' ');
            box[y][0] = '█';
            box[y][width - 2] = '█';
            box[y][width - 1] = '░';
        }
        Arrays.fill(box[height - 2], '▄');
        box[height - 2][0] = '█';
        box[height - 2][width - 2] = '█';
        box[height - 2][width - 1] = '░';
        Arrays.fill(box[height - 1], '░');
        box[height - 1][0] = ' ';

        for (int j = 2; j < height - 3; j += 2) {
            for (int i = 2; i < width - 3; i += 2) {
                box[j][i] = VAZIO;
            }
        }
    }

    public void setOrigin(int x, int y) {
        originX = x;
        originY = y;
    }

    public boolean isIn(int y, int x) {
        return y >= originY && y < originY + height && x >= originX
                && x < originX + width;
    }

    public int offsetX(int x) {
        return originX + innerOffsetX(x);
    }

    public int offsetY(int y) {
        return originY + innerOffsetY(y);
    }

    private int innerOffsetX(int x) {
        return 2 + x * 2;
    }

    private int innerOffsetY(int y) {
        return 2 + y * 2;
    }

    public char getChar(int x, int y) {
        return box[y - originY][x - originX];
    }

    public void setLetter(Character c, int cursorX, int cursorY) {
        if (c == null)
            box[innerOffsetY(cursorY)][innerOffsetX(cursorX)] = VAZIO;
        else
            box[innerOffsetY(cursorY)][innerOffsetX(cursorX)] = Character.toUpperCase(c);
    }
}