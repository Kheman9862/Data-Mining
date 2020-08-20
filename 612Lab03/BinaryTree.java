import java.util.*;

/**
 * 
 * @author: Kheman Garg ISTE-612-2195 Lab #3 4/5/2020
 */
class BTNode {
	BTNode left, right;
	String term;
	ArrayList<Integer> docLists;

	/**
	 * Creating a tree node using a term and a document list
	 * 
	 * @param term    the term in the node
	 * @param docList the ids of the documents that contain the term
	 */
	public BTNode(String term, ArrayList<Integer> docList) {
		this.term = term;
		this.docLists = docList;
	}

}

/**
 * Binary search tree structure to store the term dictionary This class contains
 * 3 main functions of binary tree that are adding, searching nodes and printing
 * them in order.
 */
public class BinaryTree {

	/**
	 * insert a node to a subtree
	 * 
	 * @param node  root node of a subtree
	 * @param iNode the node to be inserted into the subtree
	 * 
	 *              This function helps in adding node to the root/parent on both
	 *              left and right side by comparing the terma and assigning them
	 *              the side based on the alphabetic order.
	 */
	public void add(BTNode node, BTNode iNode) {
		BTNode parent = node;
		BTNode child = iNode;

		if (child.term.compareTo(parent.term) < 0) {
			if (parent.left != null) {
				add(parent.left, child);
			} else {
				parent.left = child;
				System.out.println("Inserted " + child.term + " to left node " + parent.term);
			}
		} else if (child.term.compareTo(parent.term) > 0) {
			if (parent.right != null) {
				add(parent.right, child);
			} else {
				parent.right = child;
				System.out.println("Inserted " + child.term + " to right node " + parent.term);

			}
		}
	}

	/**
	 * Search a term in a subtree
	 * 
	 * @param n   root node of a subtree
	 * @param key a query term
	 * @return parent/null: tree nodes with term that match the query term or null
	 *         if no match
	 */
	public BTNode search(BTNode n, String key) {
		BTNode parent = n;
		while (parent != null) {
			if (parent.term.compareTo(key) == 0) {
				return parent;
			} else {
				if (parent.term.compareTo(key) > 0) {
					parent = parent.left;
				} else {
					parent = parent.right;
				}
			}
		}
		return null;
	}

	/**
	 * Do a wildcard search in a subtree
	 * 
	 * @param n      the root node of a subtree
	 * @param key    a wild card term, e.g., ho (terms like home will be returned)
	 * @param result it is a Arraylist that contains null but after searching it
	 *               will create values that will be returned
	 * @return tree nodes that match the wild card
	 */
	public ArrayList<BTNode> wildCardSearch(BTNode n, String key, ArrayList<BTNode> result) {
		if (n == null) {
			return result;
		}
		if (n.term.startsWith(key)) {
			wildCardSearch(n.left, key, result);
			if (n.term.startsWith(key)) {
				result.add(n);
			}
			wildCardSearch(n.right, key, result);
			return result;
		} else {
			if (n.term.compareTo(key) < 0) {
				wildCardSearch(n.right, key, result);
			} else {
				wildCardSearch(n.left, key, result);
			}
			return result;
		}
	}

	/**
	 * Print the inverted index based on the increasing order of the terms in a
	 * subtree
	 * 
	 * @param node the root node of the subtree
	 */
	public void printInOrder(BTNode node) {
		BTNode parent = node;
		if (parent != null) {
			printInOrder(parent.left);
			System.out.println(parent.term + " " + parent.docLists);
			printInOrder(parent.right);
		}
	}
}
