import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UsersFile {
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

    public void SetUsers(long chatid) throws IOException {
        FileWriter file =  new FileWriter("users.txt", true);
        PrintWriter pw = new PrintWriter(file);

        pw.print(" " + chatid);

        pw.close();
    }
}
