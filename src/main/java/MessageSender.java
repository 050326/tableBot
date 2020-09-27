import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.TimerTask;

public class MessageSender extends TimerTask {
    @Override
    public void run() {
        Bot bot = new Bot();
        try {
            bot.sendMessages();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
