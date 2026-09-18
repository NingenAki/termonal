package ningenaki.inc.termonal.components;

import com.williamcallahan.tui4j.compat.lipgloss.color.AdaptiveColor;

public class ColorPalette {
    // ═══════════════════════════════════════════════════════════════════════════
    // PULSE COLOR PALETTE - Futuristic, high-energy aesthetic
    // ═══════════════════════════════════════════════════════════════════════════

    // Primary brand colors
    public static final AdaptiveColor PULSE_PINK = new AdaptiveColor("#FF5F87", "#FF5F87");
    public static final AdaptiveColor PULSE_PURPLE = new AdaptiveColor("#AF5FFF", "#AF5FFF");
    public static final AdaptiveColor PULSE_VIOLET = new AdaptiveColor("#875FFF", "#875FFF");

    // UI colors
    public static final AdaptiveColor BG_DARK = new AdaptiveColor("#1A1A2E", "#1A1A2E");
    public static final AdaptiveColor BG_PANEL = new AdaptiveColor("#16213E", "#16213E");
    public static final AdaptiveColor BORDER_DIM = new AdaptiveColor("#4A4A6A", "#4A4A6A");
    public static final AdaptiveColor BORDER_GLOW = new AdaptiveColor("#AF5FFF", "#AF5FFF");
    public static final AdaptiveColor TEXT_PRIMARY = new AdaptiveColor("#E4E4E7", "#E4E4E7");
    public static final AdaptiveColor TEXT_DIM = new AdaptiveColor("#71717A", "#71717A");
    public static final AdaptiveColor TEXT_MUTED = new AdaptiveColor("#52525B", "#52525B");

    // Syntax/Diff colors
    public static final AdaptiveColor DIFF_ADD = new AdaptiveColor("#22C55E", "#22C55E");
    public static final AdaptiveColor DIFF_DEL = new AdaptiveColor("#EF4444", "#EF4444");
    public static final AdaptiveColor SYNTAX_KEYWORD = new AdaptiveColor("#C084FC", "#C084FC");
    public static final AdaptiveColor SYNTAX_STRING = new AdaptiveColor("#FCD34D", "#FCD34D");
    public static final AdaptiveColor SYNTAX_COMMENT = new AdaptiveColor("#6B7280", "#6B7280");

    // File status colors
    public static final AdaptiveColor FILE_MODIFIED = new AdaptiveColor("#FBBF24", "#FBBF24");
    public static final AdaptiveColor FILE_ADDED = new AdaptiveColor("#34D399", "#34D399");
    public static final AdaptiveColor FILE_DELETED = new AdaptiveColor("#F87171", "#F87171");
}