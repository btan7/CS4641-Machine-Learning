For this project, I modified and used implementations of randomized search algorithms on ABAGAIL.

Compile alongside ABAGAIL and use the following commands.


Neural Network Tests
		where: 	args[0] is the path to the my dataset file
			   	args[1] is the number of hidden nodes
			    args[2] is the number of iterations to train it through
			    args[3] is the randomized optimization algorithm to use
Randomized Hill Climbing:
	java -cp ABAGAIL.src.project2.SpambaseTest datasets/spambase.arff rhc 6 5000
Simulated Annealing:
	java -cp ABAGAIL.src.project2.SpambaseTest datasets/spambase.arff sa 6 5000 1e6 0.5
		where: 	args[4] is the starting temperature
				args[5] is the cooling factor
Genetic Algorithm:
	java -cp ABAGAIL.src.project2.SpambaseTest datasets/spambase.arff ga 6 5000 200 25 100
		where:	args[4] is the starting population
				args[5] is the number of individuals to mate per iteration
				args[6] is the number of mutations to induce per iteration



Interesting Problems
	Traveling Salesman Problem
		java -cp ABAGAIL.src.opt.test.TravelingSalesmanTest
	Flip Flop
		java -cp ABAGAIL.src.opt.test.FlipFlopTest 80 200
	Knapsack Problem
		java -cp ABAGAIL.src.opt.test.KnapsackTest
	


All commands will run the algorithms and write results to CSV files.