package ningenaki.inc.termonal.components;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.springframework.stereotype.Component;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.KeyPressMessage;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.QuitMessage;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import com.williamcallahan.tui4j.compat.bubbletea.WindowSizeMessage;
import com.williamcallahan.tui4j.compat.lipgloss.Style;
import ningenaki.inc.termonal.services.MatrixStream;
import ningenaki.inc.termonal.services.Tab;

@Component
public class MainViewModel implements Model {

    private static final int DEFAULT_WIDTH = 80;
    private static final int DEFAULT_HEIGHT = 24;
    private static final int HEADER_HEIGHT = 8;
    private static final Duration ANIMATION_INTERVAL = Duration.ofMillis(100);

    private MatrixStream matrixStream;
    private Tab[] tabs;
    private int tabIndex;
    private int width = DEFAULT_WIDTH;
    private int height = DEFAULT_HEIGHT;

    @Override
    public Command init() {
        resize(width, height);
        return Command.batch(animationCommand(), Command.checkWindowSize());
    }

    @Override
    public UpdateResult<? extends Model> update(Message msg) {
        if (msg instanceof WindowSizeMessage windowSizeMessage) {
            return handleResize(windowSizeMessage);
        }
        if (msg instanceof AnimationTick) {
            return handleAnimationTick();
        }
        if (msg instanceof KeyPressMessage keyPressMessage) {
            return handleInput(keyPressMessage);
        }
        return UpdateResult.from(this);
    }

    private UpdateResult<? extends Model> handleResize(WindowSizeMessage windowSizeMessage) {
        resize(windowSizeMessage.width(), windowSizeMessage.height());
        return UpdateResult.from(this);
    }

    private UpdateResult<? extends Model> handleAnimationTick() {
        matrixStream.update();
        tabs[tabIndex].updateCursor();
        return UpdateResult.from(this, animationCommand());
    }

    private UpdateResult<? extends Model> handleInput(KeyPressMessage keyPressMessage) {
        return switch (keyPressMessage.key()) {
            case "left" -> UpdateResult.from(this.handleKey("left"));
            case "right" -> UpdateResult.from(this.handleKey("right"));
            case "backspace" -> UpdateResult.from(this.handleKey("backspace"));
            case "delete" -> UpdateResult.from(this.handleKey("delete"));
            case "enter" -> UpdateResult.from(this.handleKey("enter"));
            case "tab" -> UpdateResult.from(this.nextTab());
            case "shift+tab" -> UpdateResult.from(this.previousTab());
            case "esc" -> UpdateResult.from(this, QuitMessage::new);
            default -> UpdateResult.from(this.handleCharacter(keyPressMessage));
        };
    }

    private Model previousTab() {
        tabIndex = tabIndex == 0 ? tabs.length - 1 : tabIndex - 1;
        return this;
    }

    private Model nextTab() {
        tabIndex = (tabIndex + 1) % tabs.length;
        return this;
    }

    private Model handleKey(String key) {
        tabs[tabIndex].handleKey(key);
        return this;
    }

    private Model handleCharacter(KeyPressMessage message) {
        char[] runes = message.runes();
        if (runes.length > 0) {
            tabs[tabIndex].handleCharacter(runes[0], 1);
        }
        return this;
    }

    private Command animationCommand() {
        return Command.every(ANIMATION_INTERVAL, ignored -> new AnimationTick());
    }

    private void resize(int newWidth, int newHeight) {
        width = Math.max(1, newWidth);
        height = Math.max(1, newHeight);
        matrixStream = new MatrixStream(width, height);
        try {
            int boardHeight = Math.max(1, height - HEADER_HEIGHT);
            tabs = new Tab[] {
                    new Tab(width, boardHeight, 1),
                    new Tab(width, boardHeight, 2),
                    new Tab(width, boardHeight, 4)
            };
            tabIndex = Math.min(tabIndex, tabs.length - 1);
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível redimensionar as abas", exception);
        }
    }

    @Override
    public String view() {
        StringBuilder viewBuilder = new StringBuilder();
        viewBuilder.append(renderTitleBanner()).append('\n');
        viewBuilder.append(renderTabHeader()).append('\n');
        viewBuilder.append(tabs[tabIndex].view(matrixStream));
        return viewBuilder.toString();
    }

    private String renderTitleBanner() {
        String[] titleLines = loadBannerLines();
        StringBuilder banner = new StringBuilder();
        for (String line : titleLines) {
            banner.append(line).append('\n');
        }
        return banner.toString();
    }

    private String[] loadBannerLines() {
        try {
            Path bannerPath = Path.of("src/main/resources/banner.txt");
            return Files.readAllLines(bannerPath, StandardCharsets.UTF_8).toArray(new String[0]);
        } catch (Exception ex) {
            return new String[] {
                    "TERMONAL"
            };
        }
    }

    private String renderTabHeader() {
        String[] tabLabels = { "SINGLE", "DUO", "QUARTET" };
        StringBuilder header = new StringBuilder();

        for (int i = 0; i < tabLabels.length; i++) {
            String label = tabLabels[i];
            String styleLabel = i == tabIndex
                    ? Style.newStyle().foreground(ColorPalette.ACCENT).render("[" + label + "]")
                    : Style.newStyle().foreground(ColorPalette.TERTIARY).render(" " + label + " ");

            header.append(styleLabel);
            if (i < tabLabels.length - 1) {
                header.append(' ');
            }
        }

        return header.toString();
    }

    private record AnimationTick() implements Message {
    }
}