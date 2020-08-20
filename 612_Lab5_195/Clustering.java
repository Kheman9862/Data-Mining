
import java.util.*;

/**
 * Document clustering
 * 
 * @author: Kheman Garg ISTE-612-2195 Lab #5 4/22/2020
 */
@SuppressWarnings("unchecked")
public class Clustering {

	ArrayList<String[]> tokenizedDocs;
	HashMap<Integer, double[]> vectorSpace;
	ArrayList<String> termList;
	ArrayList<ArrayList<Doc>> docLists;
	int numClusters;

	/**
	 * Constructor called Clustering for assigning the number of clusters
	 * 
	 * @param numC: It is the number of clusters
	 */
	public Clustering(int numC) {
		numClusters = numC;
	}

	/**
	 * Load the documents to build the vector representations In this both a
	 * vectorspace list will be generated which will store ids and their weights
	 * 
	 * @param docs
	 */
	public void preprocess(String[] docs) {
		tokenizedDocs = new ArrayList<String[]>();
		termList = new ArrayList<String>();
		docLists = new ArrayList<ArrayList<Doc>>();
		ArrayList<Doc> docList;
		for (int i = 0; i < docs.length; i++) {
			String[] tokens = docs[i].split(" ");
			tokenizedDocs.add(i, tokens);

			for (String token : tokens) {
				if (!termList.contains(token)) {
					termList.add(token);
					docList = new ArrayList<Doc>();
					Doc doc = new Doc(i, 1);
					docList.add(doc);
					docLists.add(docList);
				} else {
					int index = termList.indexOf(token);
					docList = docLists.get(index);
					boolean match = false;
					for (Doc d : docList) {
						if (d.id == i) {
							d.weights++;
							match = true;
							break;
						}
					}
					if (!match) {
						Doc d = new Doc(i, 1);
						docList.add(d);
					}
				}
			}
		}
		vectorSpace = new HashMap<Integer, double[]>();
		double[] weights;
		int i = 0;

		while (i < docLists.size()) {

			docList = docLists.get(i);
			int j = 0;
			while (j < docList.size()) {

				Doc d = docList.get(j);
				if (vectorSpace.containsKey(d.id)) {
					weights = vectorSpace.get(d.id);
					weights[i] = d.weights;
					vectorSpace.put(d.id, weights);
				} else {
					weights = new double[termList.size()];
					weights[i] = d.weights;
					vectorSpace.put(d.id, weights);

				}
				j++;
			}
			i++;
		}

	}

	/**
	 * Cluster the documents For kmeans clustering, use the first and the ninth
	 * documents as the initial centroids This function will form two clusters in
	 * which the value of documents will be stored.
	 */
	public void cluster() {
		double[][] centroids = new double[numClusters][];
		centroids[0] = vectorSpace.get(8);
		centroids[1] = vectorSpace.get(0);
		HashMap<Integer, double[]>[] clusters = new HashMap[numClusters];
		double[][] previous = null;
		while (!Arrays.deepEquals(previous, centroids)) {
			previous = centroids;
			clusters = assignClusters(centroids);
			centroids = computeCentroid(clusters);
		}
		OutputString(clusters);
	}

	/**
	 * assign documents to cluster
	 * 
	 * @param centroids: This is the multidimensional array that will contain the
	 *                   locaation or ids and wights of first and ninth documents
	 * 
	 * @return clusters: this will contain the documents poistion for a specific id
	 *         that willbe returned to the cluster function.
	 */
	public HashMap<Integer, double[]>[] assignClusters(double[][] centroids) {
		HashMap<Integer, double[]>[] clusters = new HashMap[numClusters];
		int i = 0;
		while (i < numClusters) {

			clusters[i] = new HashMap<Integer, double[]>();
			i++;
		}
		int j = 0;
		while (j < vectorSpace.size()) {

			double[] currDocVector = vectorSpace.get(j);
			int currDocId = j;
			double[] scores = new double[numClusters];
			int k = 0;
			while (k < numClusters) {

				scores[k] = cosineSimilaties(centroids[k], currDocVector);
				k++;
			}
			int clusterId = 0;
			double max = scores[clusterId];
			int n = 0;
			while (n < scores.length) {

				if (scores[n] > max) {
					max = scores[n];
					clusterId = n;
				}
				n++;
			}

			clusters[clusterId].put(currDocId, currDocVector);
			j++;
		}

		return clusters;
	}

	/**
	 * compute centroids
	 * 
	 * @param clusters: In this we will take the clusters value obtained from the
	 *                  function which contains the cluster location for a specific
	 *                  cluster
	 * @return centroids: This will calculate the values or data points for the
	 *         centroid and return it in this value.
	 *
	 */
	public double[][] computeCentroid(HashMap<Integer, double[]>[] clusters) {
		double[][] centroids = new double[numClusters][];
		int i = 0;
		while (i < clusters.length) {

			HashMap<Integer, double[]> cluster = clusters[i];
			double[] mean = new double[termList.size()];
			for (Integer id : cluster.keySet()) {
				double[] currDocVector = cluster.get(id);
				int x = 0;
				while (x < currDocVector.length) {

					mean[x] += currDocVector[x];
					x++;
				}
				int y = 0;
				while (y < mean.length) {

					mean[y] = mean[y] / cluster.size();
					y++;
				}
			}
			centroids[i] = mean;
			i++;
		}

		return centroids;
	}

	/***
	 * 
	 * @param doc1
	 * @param doc2
	 * @return cosineSimilarity: This is the score that is used to compare the
	 *         document values.
	 * 
	 *         This function will help in calculating cosinesimilarity between two
	 *         documents
	 */
	public double cosineSimilaties(double[] doc1, double[] doc2) {
		double dotProduct = 0.0, x = 0.0, y = 0.0;
		double cosineSimilarity = 0.0;
		int i = 0;
		while (i < doc1.length) {

			dotProduct += doc1[i] * doc2[i];
			x += Math.pow(doc1[i], 2);
			y += Math.pow(doc2[i], 2);
			i++;
		}
		x = Math.sqrt(x);
		y = Math.sqrt(y);
		if (x != 0.0 | y != 0.0) {
			cosineSimilarity = dotProduct / (x * y);
		}
		return cosineSimilarity;
	}

	/**
	 * @param clusters: This is the cluster nodes that contains the loocation of all
	 *                  the values.
	 */
	public void OutputString(HashMap<Integer, double[]>[] clusters) {
		String cluster;
		int i = 0;
		while (i < clusters.length) {
			cluster = "Cluster number: " + i + "\n";
			HashMap<Integer, double[]> clusterset = clusters[i];
			for (Integer id : clusterset.keySet()) {
				cluster += id + " ";
			}
			System.out.println(cluster);
			i++;
		}
	}

	public static void main(String[] args) {
		String[] docs = { "hot chocolate cocoa beans", "cocoa ghana africa", "beans harvest ghana", "cocoa butter",
				"butter truffles", "sweet chocolate can", "brazil sweet sugar can", "suger can brazil",
				"sweet cake icing", "cake black forest" };
		Clustering c = new Clustering(2);
		c.preprocess(docs);
		c.cluster();
	}
}

/**
 * 
 * Document class for the vector representation of a document this class will
 * store the id and weights of each term
 */
class Doc {
	int id;
	double weights;

	public Doc(int id, double tw) {
		this.id = id;
		this.weights = tw;
	}

	public String toString() {
		String str = id + ": " + weights;
		return str;
	}

}