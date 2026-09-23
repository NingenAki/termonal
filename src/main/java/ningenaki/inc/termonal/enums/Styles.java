package ningenaki.inc.termonal.enums;

import com.williamcallahan.tui4j.compat.lipgloss.Style;

import lombok.Getter;

public enum Styles {
        HEADER(Style.newStyle()
                        .bold(true)
                        .background(ColorPalette.TERTIARY.getColor())
                        .foreground(ColorPalette.PRIMARY.getColor())),
        HEADER_BORDER(Style.newStyle()
                        .background(ColorPalette.TERTIARY.getColor())
                        .foreground(ColorPalette.SECONDARY.getColor())),
        TEXT(Style.newStyle()
                        .foreground(ColorPalette.PRIMARY.getColor())),
        BORDER(Style.newStyle()
                        .foreground(ColorPalette.SECONDARY.getColor())),
        MATRIX(Style.newStyle()
                        .foreground(ColorPalette.TERTIARY.getColor())),
        TAB_MENU(Style.newStyle()
                        .background(ColorPalette.TERTIARY.getColor())),
        TAB(Style.newStyle()
                        .bold(true)
                        .background(ColorPalette.TERTIARY.getColor())
                        .foreground(ColorPalette.FADED.getColor())),
        TAB_SELECTED(Style.newStyle()
                        .bold(true)
                        .background(ColorPalette.TERTIARY_DIM.getColor())
                        .foreground(ColorPalette.ACCENT.getColor())),
        KEYBOARD(Style.newStyle()
                        .foreground(ColorPalette.DIM.getColor())),
        KEYBOARD_USED(Style.newStyle()
                        .foreground(ColorPalette.MUTED.getColor())),
        GAME_OVER(Style.newStyle()
                        .foreground(ColorPalette.ACCENT.getColor())),
        WIN(Style.newStyle()
                        .foreground(ColorPalette.RIGHT_LETTER.getColor())),
        WRONG_LETTER(Style.newStyle()
                        .foreground(ColorPalette.WRONG_LETTER.getColor())),
        MISPLACED_LETTER(Style.newStyle()
                        .foreground(ColorPalette.MISPLACED_LETTER.getColor())),
        RIGHT_LETTER(Style.newStyle()
                        .foreground(ColorPalette.RIGHT_LETTER.getColor()));

        @Getter
        private final Style style;

        Styles(Style style) {
                this.style = style;
        }
}