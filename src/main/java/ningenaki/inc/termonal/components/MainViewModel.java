package ningenaki.inc.termonal.components;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.KeyPressMessage;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.QuitMessage;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import com.williamcallahan.tui4j.compat.bubbletea.WindowSizeMessage;

@Component
public class MainViewModel implements Model {

    private static final int DEFAULT_WIDTH = 80;
    private static final int DEFAULT_HEIGHT = 24;
    private static final int HEADER_HEIGHT = 8;
    private static final Duration ANIMATION_INTERVAL = Duration.ofMillis(100);

    private MatrixStream matrixStream;
    private Tab[] tabs;
    private Header header;
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
        header = new Header(() -> tabIndex);
        try {
            int boardHeight = Math.max(1, height - HEADER_HEIGHT);
            tabs = new Tab[] {
                    new Tab(width, boardHeight, 1, matrixStream),
                    new Tab(width, boardHeight, 2, matrixStream),
                    new Tab(width, boardHeight, 4, matrixStream)
            };
            tabIndex = Math.min(tabIndex, tabs.length - 1);
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível redimensionar as abas", exception);
        }
    }

    @Override
    public String view() {
        StringBuilder viewBuilder = new StringBuilder();
        viewBuilder.append(header.view()).append('\n');
        viewBuilder.append(tabs[tabIndex].view());
        return viewBuilder.toString();
    }

    private record AnimationTick() implements Message {
    }
}