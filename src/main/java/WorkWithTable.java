import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkWithTable {
    //With sheetsService you can get tables to which you have access on your google account
    private static Sheets sheetsService;
    private static String APPLICATION_NAME = "Google Sheets";
    //Maximum number of lines
    private static int maxLines= 200;

    public String[] getTasksForWeek(String namesOfTablesInput[], String SPREADSHEET_ID) throws IOException, GeneralSecurityException{
        String[] TasksForWeek = new String[namesOfTablesInput.length];
        int date = 0;
        WorkWithTable wwt = new WorkWithTable();
        List<int[][]> ValueForWeek = new ArrayList();
        List<List<List<Object>>> values = new ArrayList();

        //get the most recent date from all tables
        for (int y = 0; y < namesOfTablesInput.length; y++) {
            sheetsService = getSheetsService();
            String range = namesOfTablesInput[y] + "!A1:U" + maxLines;
            // Get a fragment of the table
            ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, range).execute();
            // Get the value from the table
            values.add(response.getValues());
            // Dimensions of the filled table [0] - high, [1] - length
            int tableSize[] = wwt.getTableSize(values.get(y));
            // Time to complete tasks
            int timings[] = wwt.getTimings(values.get(y), tableSize[0]);
            // Task execution status
            String statuses[] = wwt.getStatuses(values.get(y), tableSize[1], tableSize[0]);
            // looking for the most recent date in the tables
            if (wwt.getDate(values.get(y), tableSize[1]) > date){
                date = wwt.getDate(values.get(y), tableSize[1]);
            }
        }
        int count = 0;
        //formatting the message for each table
        for(List<List<Object>> valuesOfTable : values) {
            int tableSize[] = wwt.getTableSize(valuesOfTable);
            String describe[] = wwt.getDescribe(valuesOfTable, tableSize[0]);
            ValueForWeek.add(wwt.getValueForWeek(wwt.getTimings(valuesOfTable, tableSize[0]), wwt.getStatuses(valuesOfTable, tableSize[1], tableSize[0]), date - wwt.getDate(valuesOfTable, tableSize[1])));
            for (int q = 0; q < 7; q++) {
                switch (q) {
                    case 0:
                        TasksForWeek[count] = " <b><u>Monday</u></b>";
                        break;
                    case 1:
                        TasksForWeek[count] = TasksForWeek[count] + "\n \n<b><u>Tuesday</u></b>";
                        break;
                    case 2:
                        TasksForWeek[count] = TasksForWeek[count] + "\n \n<b><u>Wednesday</u></b>";
                        break;
                    case 3:
                        TasksForWeek[count] = TasksForWeek[count] + "\n \n<b><u>Thursday</u></b>";
                        break;
                    case 4:
                        TasksForWeek[count] = TasksForWeek[count] + "\n \n<b><u>Friday</u></b>";
                        break;
                    case 5:
                        TasksForWeek[count] = TasksForWeek[count] + "\n \n<b><u>Saturday</u></b>";
                        break;
                    case 6:
                        TasksForWeek[count] = TasksForWeek[count] + "\n \n<b><u>Sunday</u></b>";
                        break;
                }
                for (int w = 0; w < tableSize[0]; w++) {
                    if (ValueForWeek.get(count)[w][q] == 0 && !describe[w].replace(" ", "").equals("")) {
                        TasksForWeek[count] = TasksForWeek[count] + "\n" + "<i>- " + describe[w].trim() + "</i>";
                    }
                }
            }
            TasksForWeek[count] = TasksForWeek[count] + "\n";
            count++;
        }
        return TasksForWeek;
    }

    //returns the current day
    private int getDate(List<List<Object>> values, int lengthTable){
        int date = 0;
        date = Integer.parseInt(values.get(1).get(lengthTable).toString());
        return date;
    }

    //returns last statuses
    private String[] getStatuses(List<List<Object>> values, int lengthTable, int hightTable){
        String statuses[] = new String[hightTable];
        String cell = "";

        if(values == null || values.isEmpty()){
            System.out.println("NO DATA FOUND");
        }else{
            int count = 0;
            int count1 = 0;
            for (List row : values){
                if (count > 1){
                    cell = row.get(lengthTable).toString();
                    statuses[count1] = cell;
                    count1++;
                }
                count++;
            }
        }

        return statuses;
    }

    //returns timings
    private int[] getTimings(List<List<Object>> values, int tableHigh){
        int timings[] = new int[tableHigh];
        if(values == null || values.isEmpty()){
            System.out.println("NO DATA FOUND");
        }else{
            int count = 0;
            for (List row : values) {
                if (!row.get(2).equals("период") && !row.get(2).equals("в днях") && count<tableHigh){
                    timings[count] = Integer.parseInt(row.get(2).toString());
                    count++;
                }
            }
        }
        return timings;
    }

    //returns an array with the names of the activities
    private String[] getDescribe(List<List<Object>> values, int tableHigh){
        String describe[] = new String[tableHigh];
        if(values == null || values.isEmpty()){
            System.out.println("NO DATA FOUND");
        }else{
            int count = 0;
            for (List row : values) {
                if ( count<tableHigh && !row.get(1).equals("") && !row.get(2).equals("в днях") && !row.get(2).equals("период")){
                    describe[count] = row.get(1).toString();
                    count++;
                }
            }
        }
        return describe;
    }

    //returns a two-dimensional array with weekly values for each activity
    private int[][] getValueForWeek(int timings[], String statuses[], int dateOffset){
        int value[][] = new int[timings.length][7 + dateOffset];
        int count0 = 0;

        for (int timing : timings) {
            for (int i = 0; i < 7 + dateOffset; i++){
                if(timing == 1 || statuses[count0].equals("(") || statuses[count0].equals("(((") || statuses[count0].equals("0")){
                    value[count0][i] = 0;
                    statuses[count0] = "v";
                }else if(isNumeric(statuses[count0])){
                    value[count0][i] = Integer.parseInt(statuses[count0]) - 1;
                    if (Integer.parseInt(statuses[count0]) == 1){
                        statuses[count0] = "v";
                    }else {
                        statuses[count0] = String.valueOf(Integer.parseInt(statuses[count0]) - 1);
                    }
                }else if (statuses[count0].equals("v")){
                    statuses[count0] = String.valueOf(timing - 1);
                    value[count0][i] = timing - 1;
                }
            }
            count0++;
        }

        int result[][] = new int[timings.length][7];
        for (int i = 0; i < timings.length; i++){
            for (int b = 0; b < 7; b++){
                result[i][b] = value[i][b+dateOffset];
            }
        }
        return result;
    }

    //returns the size of the filled table
    private int[] getTableSize(List<List<Object>> values){
        // Dimensions of the filled table [0] - high, [1] - length
        int tableSize[] = new int[2];
        //if table is empty, return empty array
        if(values == null || values.isEmpty()){
            System.out.println("NO DATA FOUND");
        }else{
            int count = 0;
            int valuesSize = values.size();
            //---count the number of rows in the table---
            for (List row : values) {
                if (!row.isEmpty()) {
                    if (row.get(0).equals("неактуальное") || row.get(0).equals("неактуально") || row.get(1).equals("неактуальное ") || row.get(1).equals("неактуально")) {
                        break;
                    }
                    count++;
                }
            }
            //remove empty rows in the table
            for (int i=count; i<valuesSize; i++){
                values.remove(count);
            }
            tableSize[0] = count - 2;
            //-------------------------------------------
            //--count the number of columns in the table--
            valuesSize = values.size();
            int count2 = 50;
            int count3 = 0;
            for (List row : values) {
                count = 0;
                if (count3 > 1 && !row.isEmpty()) {
                    someLabel:
                    for (Object cell : row) {
                        if (!cell.toString().equals("") && count>3) {
                            String cellule = cell.toString();
                            if ((isNumeric(cellule) || cellule.contains("-") || cellule.contains("(") || cellule.contains("v")) && cellule.length()<4) {
                                break someLabel;
                            }
                        }
                        count++;
                    }
                    if (count2>count && !row.get(0).toString().equals("статусы") && !row.get(0).toString().equals("коммент")) {
                        count2 = count;
                    }
                }
                count3++;
            }
            count2++;
            tableSize[1] = count2 - 1;
            //--------------------------------------------
        }
        return tableSize;
    }

    private static Credential authorize() throws IOException, GeneralSecurityException {
        InputStream in = Main.class.getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in));
        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),clientSecrets, scopes).setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens"))).setAccessType("offline").build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    //Returns sheetsService, with which you can get tables to which you have access on your google account
    public static Sheets getSheetsService() throws  IOException,GeneralSecurityException{
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),JacksonFactory.getDefaultInstance(), credential).setApplicationName(APPLICATION_NAME).build();
    }

    private static boolean isNumeric(String strNum) {
        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }
    //returns the current month
    private String getMonth(List<List<Object>> values, int tableLength){
        String month = "Month not found";
        if(values == null || values.isEmpty()){
            System.out.println("NO DATA FOUND");
        }else{
            // can not work
            if (tableLength>10) {
                month = values.get(0).get(5).toString();
            }else{
                month = values.get(0).get(3).toString();
            }
        }
        return month;
    }
}