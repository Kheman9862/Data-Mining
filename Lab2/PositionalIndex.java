import java.util.ArrayList;

/**
 * ISTE-612-2195 Lab #2 Kheman Garg 2/27/2020
 */

public class PositionalIndex {
	String[] myDocs;
	ArrayList<String> termDictionary;
	ArrayList<ArrayList<Doc>> docLists;

	/**
	 * Construct a positional index
	 * 
	 * @param docs List of input strings or file names
	 * 
	 */
	public PositionalIndex(String[] docs) {
		// TASK1: TO BE COMPLETED
		myDocs = docs;
		docLists = new ArrayList<ArrayList<Doc>>();
		termDictionary = new ArrayList<String>();
		ArrayList<Doc> docList = new ArrayList<Doc>();

		for (int i = 0; i < myDocs.length; i++) {
			String[] words = myDocs[i].split(" ");
			String word;

			for (int j = 0; j < words.length; j++) {
				boolean match = false;
				word = words[j];
				if (!termDictionary.contains(word)) {
					termDictionary.add(word);
					docList = new ArrayList<Doc>();
					Doc doc = new Doc(i, j);
					docList.add(doc);
					docLists.add(docList);
				} else {
					int index = termDictionary.indexOf(word);
					docList = docLists.get(index);

					int k = 0;
					for (Doc did : docList) {
						if (did.docId == i) {
							did.insertPosition(j);
							docList.set(k, did);
							match = true;
							break;
						}
						k++;
					}
					if (!match) {
						Doc doc = new Doc(i, j);
						docList.add(doc);
					}
				}

			}

		}
	}

	/**
	 * Return the string representation of a positional index
	 */
	public String toString() {
		String matrixString = new String();
		ArrayList<Doc> docList;
		for (int i = 0; i < termDictionary.size(); i++) {
			matrixString += String.format("%-15s", termDictionary.get(i));
			docList = docLists.get(i);
			for (int j = 0; j < docList.size(); j++) {
				matrixString += docList.get(j) + "\t";
			}
			matrixString += "\n";
		}
		return matrixString;
	}

	/**
	 * 
	 * @param post1 first postings
	 * @param post2 second postings
	 * @return merged result of two postings
	 */
	public ArrayList<Doc> intersect(ArrayList<Doc> post1, ArrayList<Doc> post2) {
		// TASK2: TO BE COMPLETED
		ArrayList<Doc> intersectList = new ArrayList<Doc>();
		int pAL1 = 0, pAL2 = 0;

		while (pAL1 < post1.size() && pAL2 < post2.size()) {
			if (post1.get(pAL1).docId == post2.get(pAL2).docId) {
				ArrayList<Integer> posAL1 = post1.get(pAL1).positionList;
				ArrayList<Integer> posAL2 = post2.get(pAL2).positionList;
				int pposAL1 = 0, pposAL2 = 0;
				while (pposAL1 < posAL1.size()) {
					while (pposAL2 < posAL2.size()) {
						if (posAL1.get(pposAL1) - posAL2.get(pposAL2) == -1) {
							intersectList.add(post2.get(pAL2));
							break;
						}
						pposAL2++;
					}
					pposAL1++;
				}
				pAL1++;
				pAL2++;
			} else if (post1.get(pAL1).docId < post2.get(pAL2).docId)
				pAL1++;
			else
				pAL2++;
		}
		return intersectList;
	}

	/**
	 * 
	 * @param query a phrase query that consists of any number of terms in the
	 *              sequential order
	 * @return ids of documents that contain the phrase
	 */
	public ArrayList<Doc> phraseQuery(String[] query) {
		// TASK3: TO BE COMPLETED
		ArrayList<Doc> mergeList = docLists.get(termDictionary.indexOf(query[0]));
		for (int i = 1; i < query.length; i++) {
			ArrayList<Doc> finalList = docLists.get(termDictionary.indexOf(query[i]));
			mergeList = intersect(mergeList, finalList);
		}
		return mergeList;
	}

	public static void main(String[] args) {
		String[] docs = { "data text warehousing over big data", "dimensional data warehouse over big data",
				"nlp before text mining", "nlp before text classification" };

		PositionalIndex pi = new PositionalIndex(docs);
		System.out.print(pi);
		// TASK4: TO BE COMPLETED: design and test phrase queries with 2-5 terms
		String[] query = { "data text", "nlp before text", "text warehousing over big",
				"dimensional data warehouse over big" };
		System.out.println("***********************\n");

		for (int i = 0; i < query.length; i++) {
			String[] q = query[i].split(" ");
			System.out.println("Search for " + (i + 2) + " terms");
			ArrayList<Doc> result = pi.phraseQuery(q);
			if (!result.isEmpty()) {
				for (int a = 0; a < result.size(); a++) {
					System.out.println(docs[result.get(a).docId]);
				}
			} else {
				System.out.println("ERROR!! Query Search Not Found Please Try Again");
			}
			System.out.println("***********************\n");
		}
	}
}

/**
 * 
 * Document class that contains the document id and the position list
 */
class Doc {
	int docId;
	ArrayList<Integer> positionList;

	public Doc(int did) {
		docId = did;
		positionList = new ArrayList<Integer>();
	}

	public Doc(int did, int position) {
		docId = did;
		positionList = new ArrayList<Integer>();
		positionList.add(new Integer(position));
	}

	public void insertPosition(int position) {
		positionList.add(new Integer(position));
	}

	public String toString() {
		String docIdString = "" + docId + ":<";
		for (Integer pos : positionList)
			docIdString += pos + ",";
		docIdString = docIdString.substring(0, docIdString.length() - 1) + ">";
		return docIdString;
	}
}
