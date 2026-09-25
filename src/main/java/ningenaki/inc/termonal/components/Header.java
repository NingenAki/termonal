package ningenaki.inc.termonal.components;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.IntSupplier;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;

import lombok.Getter;
import ningenaki.inc.termonal.enums.Styles;
import ningenaki.inc.termonal.utils.StringUtils;

@Getter
public class Header implements Model {
    private static final String[] TAB_LABELS = {
            "OVERVIEW", "SINGLE", "DUO", "QUARTET" };
    private final int width;
    private final int height;
    private final IntSupplier activeTabIndex;

    public Header(int width, int height, IntSupplier activeTabIndex) {
        this.width = width;
        this.height = height;
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
        int emptyLines = height >= 7 ? height - 7 : height - 2;
        for (int y = 0; y < emptyLines; y++) {
            builder.append(Styles.HEADER.getStyle()
                    .render(" ".repeat(width)))
                    .append('\n');
        }
        for (String line : loadBannerLines()) {
            builder.append(Styles.HEADER.getStyle()
                    .render(StringUtils.center(line, width, ' ')))
                    .append('\n');
        }
        builder.append(renderTabMenu());
        return builder.toString();
    }

    public String renderTabMenu() {
        int menuWidth = 0;
        StringBuilder header = new StringBuilder();
        for (int i = 0; i < TAB_LABELS.length; i++) {
            header.append(Styles.TAB_MENU.getStyle().render(" "));
            String label = TAB_LABELS[i];
            String styleLabel = i == activeTabIndex.getAsInt()
                    ? Styles.TAB_SELECTED.getStyle().render(" " + label + " ")
                    : Styles.TAB.getStyle().render(" " + label + " ");
            header.append(styleLabel);
            menuWidth += label.length() + 1;
        }
        header.append(Styles.TAB_MENU.getStyle()
            .render(" ".repeat(Math.max(0, width - menuWidth))));
        return header.toString();
    }

    private String[] loadBannerLines() {
        if (height >= 7) {
            try (InputStream stream = Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream("banner.txt"),
                    "banner.txt not found")) {
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8).split("\\R", -1);
            } catch (Exception ex) {
                return new String[] { "TERMONAL" };
            }
        } else {
            return new String[] { "TERMONAL" };
        }
    }
}