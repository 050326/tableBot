//Author: Alex Plyachenko
import org.eclipse.jetty.util.IO;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.Timer;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class Main {
    private static Sheets sheetsService;
    private static String APPLICATION_NAME = "Google Sheets";
    private static String SPREADSHEET_ID = "ID";

    private static Credential authorize() throws IOException, GeneralSecurityException {
        InputStream in = Main.class.getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(), new InputStreamReader(in)
        );
        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
                clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver())
                .authorize("user");
        return credential;
    }

    public static Sheets getSheetsService() throws  IOException,GeneralSecurityException{
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        int tableSize[] = new int[2];
        sheetsService = getSheetsService();
        String range = "roman!A1:U200";

        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();

        List<List<Object>> values = response.getValues();
        WorkWithTable wwt = new WorkWithTable();
        tableSize = wwt.formattingTable(values);

        if(values == null || values.isEmpty()){
            System.out.println("NO DATA FOUND");
        }else{
            int count1 = 0;
            for (List row : values) {
                if (count1 < tableSize[0]) {
                    int count2 = 0;
                    for (Object cellue : row) {
                        if (count2 < tableSize[1]) {
                            System.out.print(cellue + " ");
                        }
                        count2++;
                    }
                    System.out.println();
                }
                count1++;
            }
        }
        int timings[] = wwt.getTimings(values, tableSize[0]);
        String statuses[] = wwt.getStatuses(values, tableSize[1]-1, tableSize[0]-2);
        for (int  i :timings) {
            System.out.println(i);
        }
        System.out.println("---------------------------");
        for (String i :statuses) {
            System.out.println(i);
        }
        System.out.println("---------------------------");
        System.out.println("Month:   "+wwt.getMonth(values, tableSize[1]));
    }
}



//public class Main {
//    public static void main(String[] args) {
//        ApiContextInitializer.init();
//        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
//        Timer time = new Timer();
//        SendMess sm = new SendMess();
//        time.schedule(sm, 0, 10000);
//        try {
//            telegramBotsApi.registerBot(new Bot());
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//}