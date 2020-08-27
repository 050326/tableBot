import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WorkWithTable {
    public String getMonth(List<List<Object>> values, int max){
        String month = "Month not found";
        if(values == null || values.isEmpty()){
            System.out.println("NO DATA FOUND");
        }else{
            if (max>10) {
                month = values.get(0).get(5).toString();
            }else{
                month = values.get(0).get(3).toString();
            }
        }
        return month;
    }

    public String[] getStatuses(List<List<Object>> values, int max, int max2){
        String statuses[] = new String[max2];
        String cell = "";
        if(values == null || values.isEmpty()){
            System.out.println("NO DATA FOUND");
        }else{
            int count = 0;
            int count1 = 0;
            for (List row : values){
                if (count > 1){
                    cell = row.get(max).toString();
                    statuses[count1] = cell;
                    count1++;
                }
                count++;
            }
        }

        return statuses;
    }

    public int[] getTimings(List<List<Object>> values, int max){
        max = max-2;
        int timings[] = new int[max];
        if(values == null || values.isEmpty()){
            System.out.println("NO DATA FOUND");
        }else{
            int count = 0;
            for (List row : values) {
                if (row.get(0).equals("")&& count<max){
                    timings[count] = Integer.parseInt(row.get(2).toString());
                    count++;
                }

            }
        }
        return timings;
    }

    public int[] formattingTable(List<List<Object>> values){
        int tableSize[] = new int[2];
        List<String> statuses = new ArrayList<>();
        if(values == null || values.isEmpty()){
            System.out.println("NO DATA FOUND");
        }else{
            int count = 0;
            int valuesSize = values.size();
            for (List row : values) {
                if (!row.isEmpty()) {
                    if (row.get(0).equals("неактуальное") || row.get(0).equals("неактуально") || row.get(1).equals("неактуальное ") || row.get(1).equals("неактуально")) {
                        break;

                    }
                    count++;
                }
            }
            for (int i = count; i < valuesSize; i++){
                values.remove(count);
            }
            tableSize[0] = count;
            valuesSize = values.size();
            int count2 = 50;
            int count3 = 0;

            for (List row : values) {
                count = 0;
                if (count3 > 1 && !row.isEmpty()) {
                    someLabel:
                    for (Object cell : row) {
                        if (!cell.toString().equals("") && count!=2) {
                            String cellule = cell.toString();
                            if ((isNumeric(cellule) || cellule.contains("-") || cellule.contains("(") || cellule.contains("v"))  && cellule.length()<4) {
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
            tableSize[1] = count2;
        }
        return tableSize;
    }
    public static boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }
}
