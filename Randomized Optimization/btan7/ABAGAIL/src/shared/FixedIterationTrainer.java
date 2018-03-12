package shared;

import java.io.FileWriter;

/**
 * A fixed iteration trainer
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class FixedIterationTrainer implements Trainer {
    
    /**
     * The inner trainer
     */
    private Trainer trainer;
    
    /**
     * The number of iterations to train
     */
    private int iterations;
    
    /**
     * Make a new fixed iterations trainer
     * @param t the trainer
     * @param iter the number of iterations
     */
    public FixedIterationTrainer(Trainer t, int iter) {
        trainer = t;
        iterations = iter;
    }

    /**
     * @see shared.Trainer#train()
     */
    public double train() {
        double sum = 0;
        for (int i = 0; i < iterations; i++) {
            sum += trainer.train();
        }
        return sum / iterations;
    }

    public double train(String filename) {
        try {
            FileWriter writer = new FileWriter(filename + ".csv");
            writer.append("Iteration");
            writer.append(",");
            writer.append("Fitness");
            writer.append('\n');

            double sum = 0;
            double start = System.nanoTime();
            double end = 0;
            double trainingTime = 0;
            for (int i = 0; i < iterations; i++) {
                writer.append("" + i);
                writer.append(',');

                double iterationFitness = trainer.train();
                writer.append("" + iterationFitness);
                writer.append('\n');
                sum += iterationFitness;
            }
            end = System.nanoTime();
            trainingTime = end - start;
            trainingTime /= Math.pow(10, 9);
            writer.append("" + trainingTime);
            writer.close();
            return sum / iterations;
        } catch (Exception e) {
            System.out.println("Not a valid file!");
            return 0.0;
        }
    }
    

}
