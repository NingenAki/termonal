package ningenaki.inc.termonal.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.williamcallahan.tui4j.compat.bubbletea.Program;

import ningenaki.inc.termonal.components.MainViewModel;

@Component 
public class Runner implements CommandLineRunner {
    private final MainViewModel mainView;

    public Runner(MainViewModel mainView) {
        this.mainView = mainView;
    }

    @Override
    public void run(String... args) throws Exception {
        new Program(mainView).withAltScreen().run();
    }
}