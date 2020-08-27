import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    final private String token="token";
    final private String botname="name";
    List<Long> users;

    public void onUpdateReceived(Update update) {
        UsersFile usersFile = new UsersFile();

        try {
            users = usersFile.getUsers();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int count = 0;
        boolean fl = true;

        while(count < users.size()) {
            if (users.get(count).equals(update.getMessage().getChatId())) {
                fl = false;
            }
            count++;
        }
        if (fl || users.isEmpty()){
            users.add(update.getMessage().getChatId());
            try {
                usersFile.SetUsers(update.getMessage().getChatId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String getBotUsername() {
        return botname;
    }

    public String getBotToken() {
        return token;
    }

    public void sendMessages(){
        UsersFile usersFile = new UsersFile();
        try {
            users = usersFile.getUsers();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (long user : users){
            SendMessage sendMessage = new SendMessage().setChatId(user).setText("Hi");
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}










//Author: Alex Plyachenko