import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    final private String token = "0000000000:AAAAAAAAAAAAAAAAAAAAAAAAAAA";
    final private String botname = "BotsName";
    private static String SPREADSHEET_ID = "1rrrrrrrrrrrrr-iiiiIiiI-qQQ3qqQqQQQQ";
    //add new strings to add new tables
    private static String namesOfTables[] = {"katya", "admin", "roman", "tania", "asia"};
    List<Long> users;

    public void onUpdateReceived(Update update) {

        if(update.getMessage().hasText()) {
            //incoming message text and chat id
            String inputText = update.getMessage().getText().toLowerCase();
            long chatid = update.getMessage().getChatId();
            
            if (inputText.equals("hi") || inputText.equals("hello") || inputText.equals("/start")) {
                //-------creating and preparing for shipment message-------
                //creating keyboard
                ArrayList<KeyboardRow> keyboard = new ArrayList<>();
                KeyboardRow keyboardRow = new KeyboardRow();
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup().setOneTimeKeyboard(true);
                keyboard.clear();
                keyboardRow.clear();
                for (int i = 0; i < namesOfTables.length; i++){
                    keyboardRow.add(namesOfTables[i]);
                }
                keyboard.add(keyboardRow);
                replyKeyboardMarkup.setKeyboard(keyboard);
                //creating message
                SendMessage sendMessage = new SendMessage().setChatId(chatid).setText("Please, chose our table:").setReplyMarkup(replyKeyboardMarkup);
                //---------------------------------------------------------

                //-----------------updating list of users------------------
                //get list of users id file
                UsersFile usersFile = new UsersFile();
                try {
                    users = usersFile.getUsers();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //checking if the bot knows the user
                boolean botDoesNotKnowTheUser = true;
                for (int i = 0; i < users.size(); i++){
                    if (users.get(i).equals(chatid)) {
                        botDoesNotKnowTheUser = false;
                    }
                }
                //adding a user to a file if the bot does not know him
                if (botDoesNotKnowTheUser || users.isEmpty()) {
                    //users.add(chatid);//most likely this line can be removed since the list of users is updated at the beginning
                    try {
                        usersFile.SetUsers(chatid);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //---------------------------------------------------------
                //if the bot knows the user, suggest changing the table
                if (!botDoesNotKnowTheUser){
                    sendMessage.setText("You can change the subscription to the table");
                }
                //send message
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

            //if the bot knows the user, change the subscription in the file
            for (String table : namesOfTables){
                //if the message text equals one of the table names
                if(inputText.contains(table)){
                    //default text of send message equals table subscription failed
                    SendMessage sendMessage = new SendMessage().setChatId(chatid).setText("table subscription failed");
                    //trying to set a subscription
                    UsersFile usersFile = new UsersFile();
                    try {
                        if (usersFile.setSubscription(chatid, table)){
                            sendMessage.setText("you subscribed to the " + table + " table");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //sending message with result
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

    }

    public void sendMessages() throws IOException, GeneralSecurityException {
        //get list of users id
        UsersFile usersFile = new UsersFile();
        try {
            users = usersFile.getUsers();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //get an array with text for messages for different tables
        WorkWithTable wwt = new WorkWithTable();
        String texts[] = wwt.getTasksForWeek(namesOfTables, SPREADSHEET_ID);
        //counting all users
        int count2 = 0;
        for (long user : users) {
            try {
                //get user subscription
                String userSubscription = usersFile.getUserSubscription(count2);
                //get the text for the message
                String text = "";
                if (!userSubscription.equals(""))
                    for (int i = 0; i < namesOfTables.length; i++) {
                        if (namesOfTables[i].equals(userSubscription)) {
                            text = texts[i];
                            break;
                        }
                    }
                //if the text is not empty
                if (text != "") {
                    //if the text is greater than the maximum allowed in the message in the telegram
                    if (text.length() > 4096) {
                        //a list with broken large messages
                        ArrayList<String> out = new ArrayList<>();
                        //a list with the message text split into lines
                        ArrayList<String> str = new ArrayList<>();
                        str.add(text.substring(0, text.indexOf("\n")));
                        int index = text.indexOf("\n");
                        //number of lines in the message text
                        int numOfLines = (text.length() - text.replace("\n", "").length());
                        for (int i = 0; i < numOfLines; i++) {
                            String substring = text.substring(index, (index + text.substring(index).indexOf("\n")));
                            if (!substring.equals("")) {
                                str.add(substring);//index + 1,2
                                index = index + str.get(i + 1).length() + 1;
                            } else {
                                i--;
                                numOfLines--;
                                index = index + 1;
                            }
                        }
                        int count1 = 0;

                        for (int i = 0; i < str.size(); i++) {
                            if (!out.isEmpty()) {
                                if (out.get(count1).length() + str.get(i).length() + 2 < 4096) {
                                    out.set(count1, out.get(count1) + str.get(i) + "\n");
                                }
                                if (!(out.get(count1).length() + str.get(i).length() <= 4096)) {
                                    count1++;
                                    out.add("");
                                }
                            } else {
                                out.add(str.get(i) + "\n");
                            }
                        }

                        for (String message : out) {
                            SendMessage sendMessage1 = new SendMessage().setChatId(user).setText(message).setParseMode("HTML");
                            execute(sendMessage1);
                        }
                    }else {
                        SendMessage sendMessage = new SendMessage().setChatId(user).setText(text).setParseMode("HTML");
                        execute(sendMessage);
                    }
                }
            }catch(TelegramApiException e){
                e.printStackTrace();
            }
            count2++;
        }
    }

    public String getBotUsername() {
        return botname;
    }

    public String getBotToken() {
        return token;
    }
}










//Author: Alex Plyachenko
