package ningenaki.inc.termonal.states;

import lombok.Getter;

@Getter 
public class TabState {
    private static final int TRIES_SINGLE = 6;
    private static final int TRIES_DUO = 7;
    private static final int TRIES_QUARTET = 9;

    private final int maxTries;
    private final BoxState[] boxes;
    private int guesses;
    private boolean won;
    private boolean gameOver;

    public TabState(int wordCount) throws Exception {
        this.maxTries = triesFor(wordCount);
        this.boxes = new BoxState[wordCount];
    }

    public void setBoxState(int index, BoxState boxState) {
        boxes[index] = boxState;
    }

    public static int triesFor(int wordCount) throws Exception {
        return switch (wordCount) {
            case 1 -> TRIES_SINGLE;
            case 2 -> TRIES_DUO;
            case 4 -> TRIES_QUARTET;
            default -> throw new Exception("Número de palavras inválido");
        };
    }
    
    private boolean allBoxesWon() {
        for (BoxState box : boxes)
            if (!box.isWon())
                return false;
        return true;
    }

    public int getWordCount() {
        return boxes.length;
    }

    public int getSolvedCount() {
        int solved = 0;
        for (BoxState box : boxes)
            if (box.isWon())
                solved++;
        return solved;
    }

    public void guess() {
        guesses++;
        won = allBoxesWon();
        gameOver = won || guesses >= maxTries;
    }
}
