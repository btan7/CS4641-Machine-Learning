All experiments were run using Weka 3.8.2.
The datasets run were letter.arff and spambase.arff.
The datasets were split into 80% and 20% for the training and testing sets, respectively. All cross validation was done with 10 folds.
All recorded data was recorded in Data.xlsx

Decision Trees:
Used J48 classifier, varied minNumObj and confidence from GUI
weka.jar weka.classifiers.trees.J48 -C [confidence] -M [minNumObj]

Neural Networks:
Used MultilayerPerceptron classifier function, varied hidden layer nodes and epochs from GUI
weka.jar weka.classifiers.functions.MultilayerPerceptron -L LEARNING_RATE -M MOMENTUM -N EPOCHS -V 0 -S 0 -E 20 -H a

Boosting:
Used AdaBoostM1 classifier function, varied confidence and number of boosting iterations from GUI
weka.jar weka.classifiers.meta.AdaBoostM1 -P 100 -S 1 -I [iterations] -W weka.classifiers.trees.J48 -- -C [confidence] -M 1

SVM:
Used Polynomial Kernal within SMO classifier function, varied exponent from GUI
weka.jar weka.classifiers.functions.SMO -C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K "weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E [exponent]"
Used RBF Kernal within SMO classifier function, varied gamma from GUI
weka.classifiers.functions.SMO -C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K "weka.classifiers.functions.supportVector.RBFKernel -C 250007 -G [gamma]"

kNN:
Used lazy IBk classifier, varied kNN and weighting from GUI
weka.jar weka.classifiers.lazy.IBk -K [knn] -W 0 -A "weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\""
