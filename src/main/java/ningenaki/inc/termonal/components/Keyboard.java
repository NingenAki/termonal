package ningenaki.inc.termonal.components;

import java.util.HashSet;
import java.util.Set;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import com.williamcallahan.tui4j.compat.lipgloss.Style;

import lombok.Getter;
import ningenaki.inc.termonal.enums.ColorPalette;
import ningenaki.inc.termonal.enums.Styles;

@Getter
public class Keyboard implements Model {
    private final int width;
    private final int height = 3;
    private final Set<Character> usedLetters = new HashSet<>();

    public Keyboard(int width) {
        this.width = width;
    }

    public void addUsedLetters(CharSequence letters) {
        for (int i = 0; i < letters.length(); i++) {
            usedLetters.add(letters.charAt(i));
        }
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
        String[] keyboardRows = new String[] { "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM" };
        StringBuilder output = new StringBuilder();
        for (int rowIndex = 0; rowIndex < keyboardRows.length; rowIndex++) {
            String row = keyboardRows[rowIndex];
            int startX = Math.max(0, (width - (row.length() * 2 - 1)) / 2);
            output.append(" ".repeat(startX));
            for (int i = 0; i < row.length(); i++) {
                char letter = row.charAt(i);
                String rendered = usedLetters.contains(letter)
                        ? Styles.KEYBOARD_USED.getStyle().render(String.valueOf(letter))
                        : Styles.KEYBOARD.getStyle().render(String.valueOf(letter));
                output.append(rendered);
                if (i < row.length() - 1) {
                    output.append(' ');
                }
            }
            int renderedWidth = startX + row.length() * 2 - 1;
            if (renderedWidth < width) {
                output.append(" ".repeat(width - renderedWidth));
            }
            output.append('\n');
        }
        return output.toString();
    }

}