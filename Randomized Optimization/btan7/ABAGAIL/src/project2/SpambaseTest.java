package project2;

import func.nn.backprop.BackPropagationNetwork;
import func.nn.backprop.BackPropagationNetworkFactory;
import opt.OptimizationAlgorithm;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.NeuralNetworkOptimizationProblem;
import opt.ga.StandardGeneticAlgorithm;
import shared.DataSet;
import shared.ErrorMeasure;
import shared.Instance;
import shared.SumOfSquaresError;
import shared.filt.TestTrainSplitFilter;
import shared.reader.ArffDataSetReader;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * Implementation of randomized hill climbing, simulated annealing, and genetic algorithm to
 * find optimal weights to a neural network that is classifying email into spam and not spam based on word/character
 * frequencies
 *
 * @author James Liu
 * @author Brittany Tan
 * @version 1.0
 */
public class SpambaseTest {
    private static Instance[] instances;

    private static int inputLayer, hiddenLayer, outputLayer = 1, trainingIterations;
    private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();
    
    private static ErrorMeasure measure = new SumOfSquaresError();

    private static DataSet set;

    private static BackPropagationNetwork network;
    private static NeuralNetworkOptimizationProblem nnop;

    private static OptimizationAlgorithm oa;
    private static String oaName;
    private static String results = "";

    private static DecimalFormat df = new DecimalFormat("0.000");

    public static void main(String[] args) throws IOException {
        if(args.length < 4)
        {
            System.out.println("Provide arff dataset file path, number of hidden nodes, and number of iterations as an argument.");
            System.exit(0);
        }

        String path = args[0];

        instances = initializeInstances(path);
        set = new DataSet(instances);
        inputLayer = instances[0].getData().size() - 1;
        hiddenLayer = Integer.parseInt(args[2]);
        trainingIterations = Integer.parseInt(args[3]);

        network = factory.createClassificationNetwork(
                new int[] {inputLayer, hiddenLayer, outputLayer});
        nnop = new NeuralNetworkOptimizationProblem(set, network, measure);

        if(args[1].equals("rhc"))
        {
            System.out.println(oaName = "Randomized Hill Climbing");
            oa = new RandomizedHillClimbing(nnop);
        }
        else if(args[1].equals("sa"))
        {
            System.out.println(oaName = "Simulated Annealing");
            if(args.length < 6)
            {
                System.out.println("Provide the initial temperature and cooling factor");
                System.exit(0);
            }
            oa = new SimulatedAnnealing(Double.parseDouble(args[4]), Double.parseDouble(args[5]), nnop);
        }
        else if(args[1].equals("ga"))
        {
            System.out.println(oaName = "Genetic Algorithm");
            if(args.length < 7)
            {
                System.out.println("Provide the initial population size, amount to mate per iteration, and amount to mutate per generation");
                System.exit(0);
            }
            oa = new StandardGeneticAlgorithm(Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]), nnop);
        }
        else
        {
            System.out.println("Optimization algorithm not recognized");
            System.exit(0);
        }

        double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0;
        train(oaName, oa, network);
        end = System.nanoTime();
        trainingTime = end - start;
        trainingTime /= Math.pow(10,9);

        Instance optimalInstance = oa.getOptimal();
        System.out.println("WEIGHTS:" + optimalInstance.getData());
        network.setWeights(optimalInstance.getData());

        double predicted, actual;
//        FileWriter writer = new FileWriter("testData.csv");
        start = System.nanoTime();
        for(int j = 0; j < instances.length; j++) {
            network.setInputValues(instances[j].getData());
            network.run();

            predicted = Double.parseDouble(instances[j].getLabel().toString());
            actual = Double.parseDouble(network.getOutputValues().toString());
//            double acc = predicted/actual;
//            writer.append(Double.toString(acc));
//            writer.append(',');


            double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;

        }
//        writer.flush();
//        writer.close();
        end = System.nanoTime();
        testingTime = end - start;
        testingTime /= Math.pow(10,9);

        System.out.println("\nResults for " + oaName + ": \nCorrectly classified " + correct + " instances." +
                "\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
                + df.format(correct/(correct+incorrect)*100) + "%\nTraining time: " + df.format(trainingTime)
                + " seconds\nTesting time: " + df.format(testingTime) + " seconds\n");
    }

    private static void train(String name, OptimizationAlgorithm oa, BackPropagationNetwork network) throws IOException {
        TestTrainSplitFilter ttsf = new TestTrainSplitFilter(70);
        ttsf.filter(set);
        DataSet train = ttsf.getTrainingSet();
        DataSet test = ttsf.getTestingSet();
        Instance[] trainInstances = train.getInstances();
        Instance[] testInstances = test.getInstances();
        FileWriter writer = new FileWriter(name + ".csv");


        System.out.println("\nError results\n---------------------------");
        for (int trial = 0; trial < 5; trial++) {
            writer.append("Training Accuracy");
            writer.append(',');
            writer.append("Testing Accuracy");
            writer.append("\n");


            for (int i = 0; i < trainingIterations; i++) {
                oa.train();

                double trainError = 0, predicted, actual;
                int trainCorrect = 0;
                for (int j = 0; j < trainInstances.length; j++) {
                    network.setInputValues(trainInstances[j].getData());
                    network.run();

                    Instance output = trainInstances[j].getLabel(), example = new Instance(network.getOutputValues());
                    example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
                    trainError += measure.value(output, example);

                    predicted = Double.parseDouble(trainInstances[j].getLabel().toString());
                    actual = Double.parseDouble(network.getOutputValues().toString());

                    if (Math.abs(predicted - actual) < 0.5) {
                        trainCorrect++;
                    }
                }

                System.out.println(df.format(trainError) + ", " + (100 * ((double) trainCorrect / (double) trainInstances.length)) + "%");
//            writer.append(df.format(trainError));
//            writer.append(',');
                writer.append((Double.toString(100 * ((double) trainCorrect / (double) trainInstances.length))));
                writer.append(',');

                double testError = 0;
                int testCorrect = 0;

                for (int j = 0; j < testInstances.length; j++) {
                    network.setInputValues(testInstances[j].getData());
                    network.run();

                    Instance output = testInstances[j].getLabel(), example = new Instance(network.getOutputValues());
                    example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
                    testError += measure.value(output, example);

                    predicted = Double.parseDouble(testInstances[j].getLabel().toString());
                    actual = Double.parseDouble(network.getOutputValues().toString());

                    if (Math.abs(predicted - actual) < 0.5) {
                        testCorrect++;
                    }
                }
                System.out.println(df.format(testError) + ", " + (100 * ((double) testCorrect / (double) testInstances.length)) + "%");
//            writer.append(df.format(testError));
//            writer.append(',');
                writer.append((Double.toString(100 * ((double) testCorrect / (double) testInstances.length))));
                writer.append("\n");
            }
            writer.append(',');
            writer.append(',');
        }

        writer.flush();
        writer.close();
    }

    private static Instance[] initializeInstances(String file) {
        ArffDataSetReader arffDSreader = new ArffDataSetReader(file);

        try
        {
            return arffDSreader.read().getInstances();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        /*
        double[][][] attributes = new double[4177][][];


        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("src/opt/test/abalone.txt")));

            for(int i = 0; i < attributes.length; i++) {
                Scanner scan = new Scanner(br.readLine());
                scan.useDelimiter(",");

                attributes[i] = new double[2][];
                attributes[i][0] = new double[7]; // 7 attributes
                attributes[i][1] = new double[1];

                for(int j = 0; j < 7; j++)
                    attributes[i][0][j] = Double.parseDouble(scan.next());

                attributes[i][1][0] = Double.parseDouble(scan.next());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        Instance[] instances = new Instance[attributes.length];

        for(int i = 0; i < instances.length; i++) {
            instances[i] = new Instance(attributes[i][0]);
            // classifications range from 0 to 30; split into 0 - 14 and 15 - 30
            instances[i].setLabel(new Instance(attributes[i][1][0] < 15 ? 0 : 1));
        }
        */

        return null;
    }
}
