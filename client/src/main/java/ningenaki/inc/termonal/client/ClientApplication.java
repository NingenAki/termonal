package ningenaki.inc.termonal.client;

import com.williamcallahan.tui4j.compat.bubbletea.Program;

import ningenaki.inc.termonal.client.components.MainViewModel;

public class ClientApplication {

    public static void main(String[] args) throws Exception {
        new Program(new MainViewModel()).withAltScreen().run();
    }
}