import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UsersFile {

    // Returns a list of chatid users from the users.txt text file
    public List<Long> getUsers() throws FileNotFoundException {
        File file = new File("users.txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Scanner scanner = new Scanner(file);
        String line = "";
        List<Long> users = new ArrayList();
        if (scanner.hasNextLine()) {
            line = scanner.nextLine().trim();
            String[] numString = line.split(" ");
            int count = 0;
            for (String num : numString) {
                users.add(Long.valueOf(num));
                count++;
            }
        }
        scanner.close();
        return users;
    }

    //adds chatid to the end of the users file and reserves space for the subscription in the usersSubscriptions file
    public void SetUsers(long chatid) throws IOException {
        FileWriter file1 =  new FileWriter("users.txt", true);
        FileWriter file2 =  new FileWriter("usersSubscriptions.txt", true);

        PrintWriter pw1 = new PrintWriter(file1);
        pw1.print(" " + chatid);
        pw1.close();

        PrintWriter pw2 = new PrintWriter(file2);
        pw2.print(" {}");
        pw2.close();
    }

    //changes the subscription to the table or reserves space for a new user
    public boolean setSubscription(long chatid, String tablesName) throws IOException {
        //does the bot know the user
        boolean knowTheUser = false;
        //user number in the list
        int userNum = 0;
        List <Long> users = getUsers();

        for (int i = 0; i < users.size(); i++){
            if (users.get(i).equals(chatid)) {
                knowTheUser = true;
                userNum = i;
            }
        }
        //adding a to a file if the bot does know him
        if (knowTheUser) {
            List<String> TablesForUsers = getUsersSubscriptions();
            TablesForUsers.set(userNum, "{" + tablesName + "}");
            File file = new File("usersSubscriptions.txt");
            //if file exists
            if(file.exists()){
                //delete him
                file.delete();
                //and create new file
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //write the changed old one to the new file
            FileWriter fileWriter =  new FileWriter("usersSubscriptions.txt", true);
            PrintWriter pw = new PrintWriter(fileWriter);
            for (String value : TablesForUsers){
                pw.print(" " + value);
            }
            pw.close();
        }
        return knowTheUser;
    }

    //returns a list with subscriptions of all users from the usersSubscriptions file
    private List<String> getUsersSubscriptions() throws FileNotFoundException {
        File file = new File("usersSubscriptions.txt");
        Scanner scanner = new Scanner(file);
        String line = "";
        List<String> tablesForUsers = new ArrayList();
        if (scanner.hasNextLine()) {
            line = scanner.nextLine().trim();
            String[] values = line.split(" ");
            int count = 0;
            for (String value : values) {
                tablesForUsers.add(value);
                count++;
            }
        }
        scanner.close();
        return tablesForUsers;
    }

    //returns a string with the subscription of a specific user from the users Table file (userNum - user id number in the users file)
    public String getUserSubscription(int userNum) throws FileNotFoundException {
        File file = new File("usersSubscriptions.txt");
        Scanner scanner = new Scanner(file);
        String line = "";
        String tableForUser = "";
        if (scanner.hasNextLine()) {
            line = scanner.nextLine().trim().replace("{","").replace("}","");
            String[] values = line.split(" ");
            int count = 0;
            for (String value : values) {
                if(count == userNum){
                    tableForUser = value;
                }
                count++;
            }
        }
        scanner.close();
        return tableForUser;
    }
}
