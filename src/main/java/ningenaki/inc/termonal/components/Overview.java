package ningenaki.inc.termonal.components;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import com.williamcallahan.tui4j.compat.lipgloss.Style;

import ningenaki.inc.termonal.enums.Styles;

public class Overview implements Model {
    private final int width;
    private final int height;
    private final Tab[] tabs;
    private final MatrixStream matrixStream;

    public Overview(int width, int height, Tab[] tabs, MatrixStream matrixStream) {
        this.width = width;
        this.height = height;
        this.tabs = tabs;
        this.matrixStream = matrixStream;
    }

    @Override
    public Command init() {
        return Command.none();
    }

    @Override
    public UpdateResult<? extends Model> update(Message msg) {
        return UpdateResult.from(this);
    }

    @Override
    public String view() {
        String[] lines = new String[] {
                "GAME OVERVIEW",
                "",
                row("SINGLE", tabs[0]),
                row("DUO", tabs[1]),
                row("QUARTET", tabs[2]),
                ""
        };
        String[] matrixLines = matrixStream.view().split("\\R", -1);
        StringBuilder output = new StringBuilder();
        int startRow = Math.max(0, (height - lines.length) / 2);
        for (int y = 0; y < height; y++) {
            int lineIndex = y - startRow;
            String overlay = y == height - 1 ? commandFooter()
                    : lineIndex >= 0 && lineIndex < lines.length ? lines[lineIndex] : "";
            Style style = (lineIndex == 0 ? Styles.TAB_SELECTED : Styles.TEXT).getStyle();
            output.append(renderMatrixRow(matrixLines, y, overlay, style));
            if (y < height - 1) {
                output.append('\n');
            }
        }
        return output.toString();
    }

    private String commandFooter() {
        return center("TAB switch   ESC quit");
    }

    private String renderMatrixRow(String[] matrixLines, int row, String overlay, Style style) {
        String matrixLine = row < matrixLines.length ? matrixLines[row] : "";
        int padding = Math.max(0, (width - overlay.length()) / 2);
        StringBuilder line = new StringBuilder();
        for (int x = 0; x < width; x++) {
            if (!overlay.isEmpty() && x >= padding && x < padding + overlay.length()) {
                line.append(style.render(String.valueOf(overlay.charAt(x - padding))));
            } else {
                line.append(renderedCell(matrixLine, x));
            }
        }
        return line.toString();
    }

    private String renderedCell(String line, int column) {
        StringBuilder styles = new StringBuilder();
        int visibleColumn = 0;
        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (character == '\u001B' && index + 1 < line.length() && line.charAt(index + 1) == '[') {
                int end = index + 2;
                while (end < line.length() && !Character.isLetter(line.charAt(end))) {
                    end++;
                }
                if (end < line.length()) {
                    styles.append(line, index, end + 1);
                    index = end;
                }
                continue;
            }
            if (visibleColumn++ == column) {
                return styles.append(character).append("\u001B[0m").toString();
            }
        }
        return " ";
    }

    private String row(String name, Tab tab) {
        String state = tab.state.isWon() ? "WON" : tab.state.isGameOver() ? "GAME OVER" : "IN PROGRESS";
        return String.format("%-8s  %-10s  %d/%d guesses  %d/%d solved",
                name, state, tab.state.getGuesses(), tab.state.getMaxTries(), tab.state.getSolvedCount(),
                tab.state.getWordCount());
    }

    private String center(String text) {
        return " ".repeat(Math.max(0, (width - text.length()) / 2)) + text;
    }
}