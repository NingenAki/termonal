package ningenaki.inc.termonal.enums;

import com.williamcallahan.tui4j.compat.lipgloss.Style;

import lombok.Getter;

public enum Styles {
    TEXT(Style.newStyle().foreground(ColorPalette.PRIMARY.getColor())),
    BORDER(Style.newStyle().foreground(ColorPalette.SECONDARY.getColor())),
    MATRIX(Style.newStyle().foreground(ColorPalette.TERTIARY.getColor())),
    TAB(Style.newStyle().foreground(ColorPalette.TERTIARY.getColor())),
    TAB_SELECTED(Style.newStyle().foreground(ColorPalette.ACCENT.getColor())),
    KEYBOARD(Style.newStyle().foreground(ColorPalette.DIM.getColor())),
    KEYBOARD_USED(Style.newStyle().foreground(ColorPalette.MUTED.getColor())),
    GAME_OVER(Style.newStyle().foreground(ColorPalette.ACCENT.getColor())),
    WIN(Style.newStyle().foreground(ColorPalette.RIGHT_LETTER.getColor())),
    WRONG_LETTER(Style.newStyle().foreground(ColorPalette.WRONG_LETTER.getColor())),
    MISPLACED_LETTER(Style.newStyle().foreground(ColorPalette.MISPLACED_LETTER.getColor())),
    RIGHT_LETTER(Style.newStyle().foreground(ColorPalette.RIGHT_LETTER.getColor()));

    @Getter
    private final Style style;

    Styles(Style style) {
        this.style = style;
    }
}