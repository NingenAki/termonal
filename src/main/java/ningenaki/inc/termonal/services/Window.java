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

    Tab currTab;
    Tab tabSingle = new Tab(WIDTH, HEIGHT, 1);
    Tab tabDuo = new Tab(WIDTH, HEIGHT, 2);
    Tab tabQuartet = new Tab(WIDTH, HEIGHT, 4);

    public Window() throws Exception {
        terminal = new DefaultTerminalFactory().setTerminalEmulatorTitle("Termonal")
                .setInitialTerminalSize(new TerminalSize(WIDTH, HEIGHT))
                .createTerminalEmulator();
        currTab = tabQuartet;
    }

    public void getInput() throws IOException {
        KeyStroke keyStroke = terminal.pollInput();
        if (keyStroke != null) {
            log.info(keyStroke.toString());

            if (keyStroke.getKeyType() == KeyType.EOF)
                System.exit(0);
            currTab.handleKeyStroke(keyStroke);
        }
    }

    @Scheduled(fixedRate = 100)
    private void update() {
        try {
            matrixStream.update();
            getInput();
            currTab.draw(terminal, matrixStream);
        } catch (IOException ex) {
            log.error("Erro ao printar no termonal", ex);
        }
    }
}