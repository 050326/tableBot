import java.util.TimerTask;


public class SendMess extends TimerTask {

    @Override
    public void run() {
        Bot bot = new Bot();
        bot.sendMessages();
    }
}
