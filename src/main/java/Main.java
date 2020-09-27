//Author: Alex Plyachenko
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Timer;

public class Main {
    //message sending period in miliseconds
    static private int  sendingPeriod = 10000;

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        //run a method that every "sendingPeriod" seconds sends all users the data of the table to which they subscribed
        Timer time = new Timer();
        MessageSender messageSender = new MessageSender();
        time.schedule(messageSender, 0, sendingPeriod);
        //run bot which accepts messages
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}

