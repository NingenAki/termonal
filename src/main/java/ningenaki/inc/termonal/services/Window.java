package ningenaki.inc.termonal.services;

import java.io.IOException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Window {
    int WIDTH = 200;
    int HEIGHT = 50;

    Terminal terminal;

    int cursor_x = 0;
    int cursor_y = 0;

    private MatrixStream matrixStream = new MatrixStream(WIDTH, HEIGHT);

    int tabIndex = 0;
    Tab[] tabs = new Tab[3];

    public Window() throws Exception {
        terminal = new DefaultTerminalFactory().setTerminalEmulatorTitle("Termonal")
                .setInitialTerminalSize(new TerminalSize(WIDTH, HEIGHT))
                .createTerminalEmulator();
        tabs[0] = new Tab(WIDTH, HEIGHT, 1);
        tabs[1] = new Tab(WIDTH, HEIGHT, 2);
        tabs[2] = new Tab(WIDTH, HEIGHT, 4);
    }

    public void getInput() throws IOException {
        KeyStroke keyStroke = terminal.pollInput();
        if (keyStroke != null) {
            log.info(keyStroke.toString());

            if (keyStroke.getKeyType() == KeyType.EOF)
                System.exit(0);
            if (keyStroke.getKeyType() == KeyType.Tab) {
                if (tabIndex < tabs.length - 1) {
                    tabIndex++;
                } else
                    tabIndex = 0;
            }
            if (keyStroke.getKeyType() == KeyType.ReverseTab) {
                if (tabIndex > 0) {
                    tabIndex--;
                } else
                    tabIndex = tabs.length - 1;
            } else
                tabs[tabIndex].handleKeyStroke(keyStroke);
        }
    }

    @Scheduled(fixedRate = 100)
    private void update() {
        try {
            matrixStream.update();
            getInput();
            tabs[tabIndex].draw(terminal, matrixStream);
        } catch (IOException ex) {
            log.error("Erro ao printar no termonal", ex);
        }
    }
}