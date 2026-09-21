package ningenaki.inc.termonal.components;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.IntSupplier;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import com.williamcallahan.tui4j.compat.lipgloss.Style;

public class Header implements Model {
    private static final String[] TAB_LABELS = { "OVERVIEW", "SINGLE", "DUO", "QUARTET" };
    private final IntSupplier activeTabIndex;

    public Header(IntSupplier activeTabIndex) {
        this.activeTabIndex = activeTabIndex;
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
        StringBuilder builder = new StringBuilder();
        for (String line : loadBannerLines()) {
            builder.append(line).append('\n');
        }
        builder.append(renderTabMenu());
        return builder.toString();
    }

    public String renderTabMenu() {
        StringBuilder header = new StringBuilder();
        for (int i = 0; i < TAB_LABELS.length; i++) {
            String label = TAB_LABELS[i];
            String styleLabel = i == activeTabIndex.getAsInt()
                    ? Style.newStyle().foreground(ColorPalette.ACCENT).render("[" + label + "]")
                    : Style.newStyle().foreground(ColorPalette.TERTIARY).render(" " + label + " ");
            header.append(styleLabel);
            if (i < TAB_LABELS.length - 1) {
                header.append(' ');
            }
        }
        return header.toString();
    }

    private String[] loadBannerLines() {
        try (InputStream stream = Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("banner.txt"), "banner.txt not found")) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8).split("\\R", -1);
        } catch (Exception ex) {
            return new String[] { "TERMONAL" };
        }
    }
}