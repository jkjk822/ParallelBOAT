package parallelBOAT;

import parallelBOAT.tree.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Driver {


    //TODO: make Double.NaN a constant
    public static void main(String [] args) {

        Article[] rawData = new Article[39644];
        BufferedReader br = null;
        String file = "./resources/OnlineNewsPopularity.csv";
        String line = "";

        ImpurityFunction imp = chooseImpurityFunction();

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

//        DecisionTreeBuilder.generateDecisionTree(rawData);
        BootStrapTreeBuilder b = new BootStrapTreeBuilder(rawData, imp, 2, 2000);
        b.generateDecisionTree();





        //Willie's testing

//        System.out.println(rawData[0].getData()[Attribute.weekday_is_friday.getIndex()].toString());
//        System.out.println(rawData[0].isWeekday_is_monday());

//        System.out.println(rawData[0].getShares());
////        Arrays.sort(rawData, new CompareByAttribute(Attribute.shares));
//        System.out.println(rawData[0].getShares());


//        Article[] newdata = Arrays.copyOfRange(rawData, 0, 5000);
//        Node tree = DecisionTreeBuilder.generateDecisionTree(newdata);
        System.out.println("DONE");

        int correct = 0;
        for(int i = 0; i < 100; i++) {
            Popularity test = b.classify(rawData[20000 + i]);
            if(test == rawData[20000 + i].getPopularity())
                correct++;
            System.out.print("ACTUAL: " + rawData[20000 + i].getPopularity());
            System.out.println(", CLASSIFIED: " + test);
        }

        System.out.println(correct);

    }


    private static ImpurityFunction chooseImpurityFunction(){
        //if input = gini index
        return new ImpurityFunction() {
            @Override
            public double computeImpurity(Article[]... partitions) {
                return Arrays.stream(partitions).mapToDouble(this::giniIndex).sum()
                        /Arrays.stream(partitions).mapToInt(partition -> partition.length).sum();
            }

            private double giniIndex(Article[] data) {
                int[] count =  new int[Popularity.values().length];

                // Count up occurrences of each class
                for (Article a : data) {
                    count[a.getPopularity().ordinal()]++;
                }
                // Get each p^2
                double sum = 0;
                for(int c : count){
                    double d = c;
                    sum += (d / data.length)*(d / data.length);
                }
                // Return 1 - sum of p^2
                return (1 - sum)*data.length;

            }
        };

        //if input = entropy
    }



}
