package ningenaki.inc.termonal.components;

import org.springframework.stereotype.Component;

import com.williamcallahan.tui4j.compat.bubbles.textarea.Textarea;
import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.KeyPressMessage;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.QuitMessage;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;

@Component
public class MainViewModel implements Model {

    private Textarea editor = new Textarea();

    @Override
    public Command init() {
        editor.setWidth(200);
        editor.setHeight(50);
        editor.focus();
        return null;
    }

    @Override
    public UpdateResult<? extends Model> update(Message msg) {
        if (msg instanceof KeyPressMessage keyPressMessage) {
            return switch (keyPressMessage.key()) {
                // "left" move the cursor left
                case "left" -> UpdateResult.from(this.moveLeft());

                // "right" move the cursor right
                case "right" -> UpdateResult.from(this.moveRight());

                // "backspace" deletes character before cursor
                case "backspace" -> UpdateResult.from(this.removePreviousCharacter());

                // "delete" deletes character before cursor
                case "delete" -> UpdateResult.from(this.removeNextCharacter());

                // "enter" submits current word if valid
                case "enter" -> UpdateResult.from(this.validateWordAndSubmit());

                // "tab" switches to next tab
                case "tab" -> UpdateResult.from(this.nextTab());

                // "shift+tab" switches to previous tab
                case "shift+tab" -> UpdateResult.from(this.previousTab());

                // "esc" quits
                case "esc" -> UpdateResult.from(this, QuitMessage::new);

                default -> UpdateResult.from(this);
            };
        }
        return UpdateResult.from(this);
    }

    private Model previousTab() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'previousTab'");
    }

    private Model nextTab() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'nextTab'");
    }

    private Model validateWordAndSubmit() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateWordAndSubmit'");
    }

    private Model removePreviousCharacter() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removePreviousCharacter'");
    }

    private Model removeNextCharacter() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeNextCharacter'");
    }

    private Model moveRight() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'moveRight'");
    }

    private Model moveLeft() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'moveLeft'");
    }

    @Override
    public String view() {        
        return editor.view();
    }
}