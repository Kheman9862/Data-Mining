
/** 
* Text Classification using a Naïve Bayes (Multinomial) Classifier
* @author: Kheman Garg ISTE-612-2195 Lab #4 4/15/2020
*/

import java.util.*;

public class NBClassifier {
	private HashMap<Integer, String> trainingDocs; // training data
	private int[] trainingClasses; // training class values
	private int numClasses = 2;
	private int[] classDocCounts; // number of docs per class
	private String[] classStrings; // concatenated string for a given class
	private int[] classTokenCounts; // total number of tokens per class
	private HashMap<String, Double>[] condProb; // term conditional prob
	private HashSet<String> vocabulary; // entire vocabulary
	LoadData docparse;

	/**
	 * Build a Naive Bayes classifier using a training document set
	 * 
	 * @param trainDataFolder the training document folder
	 * @param testDataFolder  the test document folder
	 * 
	 * @ Explanation: In this Constructor, first it will call the preprocess
	 * function which will load all the train and test files. From the constructor
	 * we will add terms from the document in vocabulary(stored in HashSet as a
	 * String) and conditional probability for each term which will be stored in a
	 * HashMap along with the key and the value.
	 */

	@SuppressWarnings("unchecked")
	public NBClassifier(String trainDataFolder, String testDataFolder) {
		preprocess(trainDataFolder, testDataFolder);
		trainingDocs = docparse.docs;
		trainingClasses = docparse.classes;
		classDocCounts = new int[numClasses];
		classStrings = new String[numClasses];
		classTokenCounts = new int[numClasses];

		condProb = new HashMap[numClasses];
		vocabulary = new HashSet<String>();

		for (int i = 0; i < numClasses; i++) {
			classStrings[i] = "";
			condProb[i] = new HashMap<String, Double>();
		}

		for (int i = 0; i < trainingClasses.length; i++) {
			classDocCounts[trainingClasses[i]]++;
			classStrings[trainingClasses[i]] += (trainingDocs.get(i) + " ");
		}

		for (int i = 0; i < numClasses; i++) {
			String[] tokens = classStrings[i].split("[\" ()_,?:;%&-]+");
			classTokenCounts[i] = tokens.length;
			for (String token : tokens) {
				vocabulary.add(token);
				if (condProb[i].containsKey(token)) {
					double count = condProb[i].get(token);
					condProb[i].put(token, count + 1);
				} else
					condProb[i].put(token, 1.0);
			}
		}

		for (int i = 0; i < numClasses; i++) {
			Iterator<Map.Entry<String, Double>> iterator = condProb[i].entrySet().iterator();
			int vSize = vocabulary.size();
			while (iterator.hasNext()) {
				Map.Entry<String, Double> entry = iterator.next();
				String token = entry.getKey();
				Double count = entry.getValue();
				Double prob = (count + 1) / (classTokenCounts[i] + vSize);
				condProb[i].put(token, prob);
			}
		}
	}

	/**
	 * Classify a test doc
	 * 
	 * @param doc test doc
	 * @return class label
	 * 
	 * @ Explanation: In this function we are classifying single test document.
	 * First we will tokenize each doc.Then we will calculate the score using the
	 * Naive Bayes Classifier equation. After that we will compare both positive and
	 * negative score and the one with the greater value will be returned in the
	 * form of the label.
	 */
	public int classify(String doc) {
		int label = 0;
		int vSize = vocabulary.size();
		double[] score = new double[numClasses];
		int i = 0;
		while (i < numClasses) {
			score[i] = Math.log10(classDocCounts[i] * 1.0 / trainingDocs.size());
			i++;
		}
		String[] tokens = doc.split("[\" ()_,?:;%&-]+");
		int j = 0;
		while (j < numClasses) {
			for (String token : tokens) {
				if (!condProb[j].containsKey(token))
					score[j] += Math.log10(1.0 / (classTokenCounts[j] + vSize));
				else
					score[j] += Math.log10(condProb[j].get(token));
			}
			j++;
		}
		double LargestScore = score[0];
		int k = 0;
		while (k < score.length) {
			if (LargestScore < score[k])
				label = k;
			k++;
		}
		return label;
	}

	/**
	 * Load the training documents
	 * 
	 * @param trainDataFolder
	 * @param testDataFolder
	 * 
	 * @ Explanation: This function helps in loading all the data using NIO streams
	 * and converting them in UTF-8 encoding as well as in lower case. This function
	 * will help in gathering all of the train and test data in docs and TestDocs
	 * respectively. There is a seperate class of LoadData you can check that out.
	 */
	public void preprocess(String trainDataFolder, String testDataFolder) {
		docparse = new LoadData(trainDataFolder, testDataFolder);
	}

	/**
	 * Classify a set of testing documents and report the accuracy
	 * 
	 * @param testDocs:        fold that contains the testing documents
	 * @param trainingClasses: which is training class values
	 * 
	 * @ Explanation: In this we will classify all the test documents. We will pass
	 * all the docs through classify function which will return the label. In this
	 * we will match that result with the training classes and by the basis of that
	 * we will calculate 4 different parameters that are true positive, true
	 * negative, flase positive, false negative. After calculating these values we
	 * will calculate the Accuracy of this classifier and number of corrected
	 * documents alomg with precision and recall.
	 */
	public void classifyAll(HashMap<Integer, String> testDocs, int[] trainingClasses) {
		float tp = 0;
		float tn = 0;
		float fp = 0;
		float fn = 0;
		int correctlyClassified = 0;
		float precision;
		float recall;
		float accuracy;
		for (Map.Entry<Integer, String> testDoc : testDocs.entrySet()) {
			int result = classify(testDoc.getValue());
			if (trainingClasses[testDoc.getKey()] == 1 && result == trainingClasses[testDoc.getKey()]) {
				tp++;
			} else if (trainingClasses[testDoc.getKey()] == 0 && result == trainingClasses[testDoc.getKey()]) {
				tn++;
			} else if (trainingClasses[testDoc.getKey()] == 0 && result != trainingClasses[testDoc.getKey()]) {
				fn++;
			} else if (trainingClasses[testDoc.getKey()] == 1 && result != trainingClasses[testDoc.getKey()]) {
				fp++;
			}
		}
		correctlyClassified = (int) tp + (int) tn;
		precision = tp / (tp + fp);
		recall = tp / (tp + fn);
		accuracy = (tp + tn) / (tp + tn + fp + fn);
		System.out.println("Correctly classified " + correctlyClassified + " out of " + testDocs.size());
		System.out.println("Accuracy: " + accuracy);
		System.out.println("Precision is : " + precision);
		System.out.println("Recall is : " + recall);
	}

	/**
	 * @ Explanation: In the main function we will pass the file location and
	 * calculate the values. Notice that there is a value 1800 that you will notice
	 * below since the size of my TestDocs is 200 and therefore my random number
	 * will give value between 0-199 while key of my TestDocs starts from 1800-1999.
	 * Therefore to rectify this problem i am adding 1800 in my random number so
	 * that it will be equal to the key of my TestDocs so that it can retrieve the
	 * result by matching the key.
	 */

	public static void main(String[] args) {
		NBClassifier NBc = new NBClassifier("data/train", "data/test");
		System.out.println("CLASSIFYING SINGLE DOCS FROM TEST FOLDER:");
		int randomIndex = new Random().nextInt(NBc.docparse.TestDocs.size());
		randomIndex += 1800;
		System.out.println("Doc Index : " + (randomIndex - 1800) + " which is "
				+ (NBc.classify(NBc.docparse.TestDocs.get(randomIndex)) == 1 ? "Positive" : "Negative"));
		randomIndex = new Random().nextInt(NBc.docparse.TestDocs.size());
		randomIndex += 1800;
		System.out.println("Doc Index : " + (randomIndex - 1800) + " which is "
				+ (NBc.classify(NBc.docparse.TestDocs.get(randomIndex)) == 1 ? "Positive" : "Negative"));
		System.out.println();
		System.out.println("CLASSIFYING ALL DOCS FROM TEST FOLDER:");
		NBc.classifyAll(NBc.docparse.TestDocs, NBc.docparse.classes);

	}
}
