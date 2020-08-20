import java.util.*;

/**
 * 
 * @author: Kheman Garg ISTE-612-2195 Lab #3 4/5/2020
 */
public class BTreeIndex {
	String[] myDocs;
	BinaryTree termList;
	BTNode root;
	ArrayList<ArrayList<Integer>> docLists;

	/**
	 * Constructing a binary search tree to store the term dictionary
	 * 
	 * @param docs List of input strings
	 * 
	 *             This constructor will first store all the values from docs in the
	 *             names which will be further sorted. After that a Node tree will
	 *             be created and stored in termList using add method in BinaryTree
	 *             class and doclists will get the index of the folder where the
	 *             name is present. Also, in this we will print the names along with
	 *             the doc values where they are present using prinInOrder function
	 *             in BinaryTree Class.
	 * 
	 */
	public BTreeIndex(String[] docs) {
		myDocs = docs;
		ArrayList<String> names = new ArrayList<String>();
		docLists = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> docList = new ArrayList<Integer>();
		termList = new BinaryTree();
		for (int i = 0; i < myDocs.length; i++) {
			String[] words = myDocs[i].split(" ");
			for (String word : words) {
				if (!names.contains(word)) {
					names.add(word);
				}
			}
		}
		Collections.sort(names);
		System.out.println("Sorted names are:-");
		System.out.println(names + "\n");
		int start = 0;
		int end = names.size() - 1;
		int mid = (start + end) / 2;
		BTNode r = new BTNode(names.get(mid), docList);
		root = r;
		for (int i = 0; i < myDocs.length; i++) {
			String[] tokens = myDocs[i].split(" ");
			for (String token : tokens) {
				if (termList.search(r, token) == null) {
					docList = new ArrayList<Integer>();
					docList.add(new Integer(i));
					docLists.add(docList);
					termList.add(root, new BTNode(token, docList));
				} else {

					BTNode indexNode = termList.search(r, token);
					docList = indexNode.docLists;
					if (!docList.contains(new Integer(i))) {
						docList.add(new Integer(i));
					}
					indexNode.docLists = docList;
				}
			}

		}
		System.out.println("\nResult for print in order:");
		termList.printInOrder(root);
	}

	/**
	 * Single keyword search
	 * 
	 * @param query the query string
	 * @return doclists that contain the term
	 */
	public ArrayList<Integer> search(String query) {
		BTNode node = termList.search(root, query);
		if (node == null)
			return null;
		return node.docLists;
	}

	/**
	 * conjunctive query search
	 * 
	 * @param query the set of query terms
	 * @return doclists that contain all the query terms
	 */
	public ArrayList<Integer> search(String[] query) {
		ArrayList<Integer> result = search(query[0]);
		int termId = 1;
		while (termId < query.length) {
			ArrayList<Integer> result1 = search(query[termId]);
			result = merge(result, result1);
			termId++;
		}
		return result;
	}

	/**
	 * 
	 * @param wildcard the wildcard query, e.g., ho (so that home can be located)
	 * @return a list of ids of documents that contain terms matching the wild card
	 */
	public ArrayList<Integer> wildCardSearch(String wildcard) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		ArrayList<BTNode> results = termList.wildCardSearch(root, wildcard, new ArrayList<BTNode>());
		if (results.size() > 0) {
			BTNode start = results.get(0);
			result = start.docLists;
			if (results.size() > 1) {
				for (BTNode node : results) {
					result = union(result, node.docLists);
				}
			}
		}
		return result;
	}

	/*
	 * union function is used to return all the positions of the queries when
	 * searched as a wildcard query.
	 * 
	 * @param arr1: This will contain the position of the first docList/doc where
	 * the term is present
	 * 
	 * @param arr2: This will contain the second position where the term is present
	 * 
	 * @return result: it will return all the positions where any of the passed
	 * queries are present.
	 */
	public ArrayList<Integer> union(ArrayList<Integer> arr1, ArrayList<Integer> arr2) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		int m = arr1.size();
		int n = arr2.size();
		int i = 0, j = 0;
		while (i < m && j < n) {
			if (arr1.get(i) < arr2.get(j))
				result.add(arr1.get(i++));
			else if (arr2.get(j) < arr1.get(i))
				result.add(arr2.get(j++));
			else {
				result.add(arr2.get(j++));
				i++;
			}
		}
		while (i < m)
			result.add(arr1.get(i++));
		while (j < n)
			result.add(arr2.get(j++));
		return result;
	}

	/*
	 * merge function is used to return all the postions that are common in both the
	 * queries
	 * 
	 * @param l1:This will contain the position of the first docList/doc where the
	 * term is present
	 * 
	 * @param arr2: This will contain the second position where the term is present
	 * 
	 * @return result: it will return all the positions of the docs where both of
	 * the passed queries are present.
	 */
	private ArrayList<Integer> merge(ArrayList<Integer> l1, ArrayList<Integer> l2) {
		ArrayList<Integer> mergedList = new ArrayList<Integer>();
		int id1 = 0, id2 = 0;
		while (id1 < l1.size() && id2 < l2.size()) {
			if (l1.get(id1).intValue() == l2.get(id2).intValue()) {
				mergedList.add(l1.get(id1));
				id1++;
				id2++;
			} else if (l1.get(id1) < l2.get(id2))
				id1++;
			else
				id2++;
		}
		return mergedList;
	}

	/**
	 * Test cases
	 * 
	 * @param args commandline input
	 * 
	 *             In the main we will pass the docs and test for the single,
	 *             conjunctive and wildcard queries.
	 */
	public static void main(String[] args) {
		String[] docs = { "text warehousing over big data", "dimensional data warehouse over big data",
				"nlp before text mining", "nlp before text classification" };
		BTreeIndex bTree = new BTreeIndex(docs);
		/*
		 * Single Query
		 */
		System.out.println("\nTestCase 1 : Single Query");
		String[] singleQuery = { "text", "data", "nlp" };
		for (int j = 0; j < singleQuery.length; j++) {
			ArrayList<Integer> result = bTree.search(singleQuery[j]);
			if (!result.isEmpty()) {
				System.out.println(singleQuery[j] + ": " + result);
			} else {
				System.out.println("Not found in the dictionary");
			}
		}
		/*
		 * For conjunctive queries
		 */
		String[] query2 = { "nlp", "before" };
		System.out.println("\nTestCase 2 :  Cojunctive queries");
		ArrayList<Integer> result1 = bTree.search(query2);
		if (result1 != null && !result1.isEmpty()) {
			System.out.println("nlp AND before: " + result1);
		} else {
			System.out.println("Not found in the dictionary");
		}
		/*
		 * For Wildcard queries
		 */
		System.out.println("\nTestCase 3 :  Wildcard queries");
		String[] wildcardQuery = { "te", "b", "war" };
		for (int i = 0; i < wildcardQuery.length; i++) {
			ArrayList<Integer> result2 = bTree.wildCardSearch(wildcardQuery[i]);
			if (result2 != null && !result2.isEmpty()) {
				System.out.println(wildcardQuery[i] + ": " + result2);
			} else {
				System.out.println("Not found in the dictionary");
			}
		}
	}
}