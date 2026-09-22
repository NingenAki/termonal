package ningenaki.inc.termonal.enums;

import com.williamcallahan.tui4j.compat.lipgloss.color.AdaptiveColor;

import lombok.Getter;

public enum ColorPalette {
    PRIMARY(new AdaptiveColor("#E4E4E7", "#E4E4E7")),
    SECONDARY(new AdaptiveColor("#AF5FFF", "#AF5FFF")),
    TERTIARY(new AdaptiveColor("#875FFF", "#875FFF")),
    ACCENT(new AdaptiveColor("#FF5F87", "#FF5F87")),
    DIM(new AdaptiveColor("#71717A", "#71717A")),
    MUTED(new AdaptiveColor("#52525B", "#52525B")),

    // Letter status colors
    WRONG_LETTER(new AdaptiveColor("#6B7280", "#6B7280")),
    MISPLACED_LETTER(new AdaptiveColor("#FCD34D", "#FCD34D")),
    RIGHT_LETTER(new AdaptiveColor("#22C55E", "#22C55E"));

    @Getter
    private final AdaptiveColor color;

    ColorPalette(AdaptiveColor color) {
        this.color = color;
    }
}