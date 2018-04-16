package parallelBOAT;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Driver {

    public static void main(String [] args) {

        Article[] rawData = new Article[39644];
        BufferedReader br = null;
        String file = "./resources/OnlineNewsPopularity.csv";
        String line = "";

        try {
            br = new BufferedReader(new FileReader(file));
            int i = -1;
            while ((line = br.readLine()) != null) {
                if(i == -1) {
                    i++;
                    continue;
                }
                rawData[i] = new Article(line.split(", "));
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

//        System.out.println(rawData[0].getData()[Attribute.weekday_is_friday.getIndex()].toString());
//        System.out.println(rawData[0].isWeekday_is_monday());

        System.out.println(rawData[0].getShares());
//        Arrays.sort(rawData, new CompareByAttribute(Attribute.shares));
        System.out.println(rawData[0].getShares());


        DecisionTree dt = new DecisionTree();
        Article[] newdata = Arrays.copyOfRange(rawData, 0, 3);
//        System.out.println(dt.getMajorityClass(newdata));

    }
}
