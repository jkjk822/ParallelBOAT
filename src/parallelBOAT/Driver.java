package parallelBOAT;

import parallelBOAT.tree.BootStrapTreeBuilder;
import parallelBOAT.tree.DecisionTreeBuilder;
import parallelBOAT.tree.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        // is equal testing:
//        Article[] aData = Arrays.copyOfRange(rawData, 0, 100);
//        Article[] bData = Arrays.copyOfRange(rawData,100, 200);
//        DecisionTreeBuilder a = new DecisionTreeBuilder(aData, imp);
//        a.generateDecisionTree();
//        DecisionTreeBuilder b = new DecisionTreeBuilder(bData, imp);
//        b.generateDecisionTree();
//
//        System.out.println(a.isEqual(b.getTree()));

        int classifySize = rawData.length / 4;
        randomizeData(classifySize, rawData);
        Article[] trainingDataOrig = Arrays.copyOfRange(rawData, 0, rawData.length - classifySize);
        Article[] trainingDataBoat = Arrays.copyOfRange(rawData, 0, rawData.length - classifySize);
        Article[] classifyData = Arrays.copyOfRange(rawData,rawData.length - classifySize, rawData.length);
        DecisionTreeBuilder original = new DecisionTreeBuilder(trainingDataOrig, imp);
        BootStrapTreeBuilder boat = new BootStrapTreeBuilder(trainingDataBoat, imp, 5, 2000);

        long startTimeOrig = System.currentTimeMillis();
        original.generateDecisionTree();
        long endTimeOrig = System.currentTimeMillis();

        long startTimeBoat = System.currentTimeMillis();
        boat.generateDecisionTree();
        long endTimeBoat = System.currentTimeMillis();

        int correctOrig = 0;
        int correctBoat = 0;
        for( Article a : classifyData) {
            Popularity testOrig = original.classify(a);
            Popularity testBoat = boat.classify(a);
            if(testOrig == a.getPopularity())
                correctOrig++;
            if(testBoat == a.getPopularity())
                correctBoat++;
        }

        double accuracyOrig = (double) correctOrig / (double) classifyData.length;
        double accuracyBoat = (double) correctBoat / (double) classifyData.length;
        System.out.println("Accuracy Original: " + accuracyOrig);
        System.out.println("Accuracy Boat: " + accuracyBoat);
        System.out.println("Build time Original (s): " + ((endTimeOrig - startTimeOrig) / 1000));
        System.out.println("Build time Boat (s): " + ((endTimeBoat - startTimeBoat) / 1000));
    }

    // Randomly select the data that will be used in classification, and separate it at the end from the training data
    private static void randomizeData(int sampleSize, Article[] rawData) {
        Random rand = new Random();
        for(int i = 1; i < sampleSize + 1; i++) {
            int select = rand.nextInt(rawData.length - i);
            Article temp = rawData[rawData.length - i];
            rawData[rawData.length - i] = rawData[select];
            rawData[select] = temp;
        }
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
