package ningenaki.inc.termonal.components;

import org.springframework.boot.CommandLineRunner;
import com.williamcallahan.tui4j.compat.bubbletea.Program;

public class Runner implements CommandLineRunner {
    private final MainViewModel app;

    public Runner(MainViewModel app) {
        this.app = app;
    }

    @Override
    public void run(String... args) throws Exception {
        new Program(app).withAltScreen().run();
    }
}