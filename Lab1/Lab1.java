import java.io.*;
import java.nio.file.*;
import java.util.*;

/*

COURSE# : ISTE-612 (Knowledge Processing Technologies)
LAB#: LAB1 PROGRAM
NAME: KHEMAN GARG

*/
/*Class Lab1*/
public class Lab1 {
    /* Global Variables Declaration */
    private ArrayList<String> DataFiles = new ArrayList<String>();
    private ArrayList<String> stopwordsData = new ArrayList<String>();
    static String[] docs = { "cv000_29416.txt", "cv001_19502.txt", "cv002_17424.txt", "cv003_12683.txt",
            "cv004_12641.txt" };
    private ArrayList<String> termList; // dictionary
    private ArrayList<ArrayList<Integer>> docLists;
    private ArrayList<Integer> docList;
    private ArrayList<ArrayList<String>> myDocs = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<String>> finalData = new ArrayList<ArrayList<String>>();

    /*
     * Constructor build just to call functions in the main class using
     * constructor's object
     */

    public Lab1() {

    }

    // Task-1 Inverted Index Construction (3 points)

    /*
     * Method ReadFiles :- this function is reading all the files given in the other
     * directory
     * 
     * @param FileName :- it is String passing docs from the main function
     * 
     * @return FileData :- Filedata which is a string type is getting return from
     * this function it will read all the files as a string
     */

    public String ReadFiles(String Filename) throws Exception {
        String FileData = "";
        FileData = new String(Files.readAllBytes(Paths.get(Filename)));
        // System.out.println(FileData);
        return FileData;
    }

    /*
     * Method assignToDocList :- this function is adding all the FileData along with
     * the index number read above in the DataFiles directory
     * 
     * @param i: An int type used to give the index number to the FileData
     * 
     * @param FileData: It is a string type that us passing the files from the
     * FileData and adding it to DataFiles
     * 
     * @return DataFiles :- This is storing all the data from FileData in the form
     * of the ArrayList<String> and returning it
     */

    public ArrayList<String> assignToDoclist(int i, String FileData) {
        DataFiles.add(i, FileData);
        return DataFiles;
    }

    /*
     * Method readStopWords :- this function is reading all the stopwords.txt file
     * and storing it in the stopwordsData which is a ArrayList<String>. It also use
     * try catch exception as it is using IO to read file
     * 
     */

    public void readStopWords() {
        // String stopwordsData = new String();
        try {
            File file = new File("stopwords.txt");
            Scanner sc = new Scanner(file);
            String words = "";
            while (sc.hasNextLine()) {
                words += sc.nextLine().toLowerCase() + " ";
            }
            String wordsData[] = words.split(" ");
            Arrays.sort(wordsData);
            for (String word : wordsData) {
                stopwordsData.add(word);
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /*
     * It is a binary search algorithm that will search for take theString key and
     * compare it with the words in the stopwords
     * 
     * Method searchStopWords:- it is used to compare the given query with the list
     * of stopWords we have and it will return the value of the index where the word
     * actually exists based on it
     * 
     * @param key: key is the value of the query which is used to compare it with
     * the stopWords.txt and based on that value wil be returned
     * 
     * @return mid:- it is the index value which states that the key passed in this
     * function matches with the word in the stopwords.txt file and therefore
     * returning its poistion
     * 
     * @return -1:- it is the index value which states that the key passed in this
     * function does not matches with any word in the stopwords.txt file and
     * therefore returning -1 instead
     * 
     */

    // Binary tree search Algorithm
    public int searchStopWords(String key) {
        int lo = 0;
        int hi = stopwordsData.size() - 1;
        // System.out.println(hi);

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int output = key.compareTo(stopwordsData.get(mid));
            if (output < 0)
                hi = mid - 1;
            else if (output > 0)
                lo = mid + 1;
            else
                return mid;
        }
        return -1;
    }

    /*
     * Method tokenization: This function takes every word from the files and
     * tokenize them and it also filters out the delimeters and remove stopWords and
     * finally it takes the pure/filtered tokens do the stemming using Porter's
     * Stemming algorithm
     * 
     * @params document: it is taking the diocument whioch contains all the files
     * that need to be tokenized.
     * 
     * @return stemms: it is the ArrayList<String> which contains all the words
     * after stemming the filterd tokens from delimeters and stopWords
     * 
     */

    public ArrayList<String> tokenization(String document) {
        String[] tokens = null;
        ArrayList<String> pureTokens = new ArrayList<String>();
        ArrayList<String> stemms = new ArrayList<String>();

        tokens = document.split("[ '.,&#?!:;$%+()\\-\\/*\"]+");

        // remove stop words
        for (String token : tokens) {
            if (searchStopWords(token) == -1) {
                pureTokens.add(token);
            }
        }

        // stemming
        Stemmer st = new Stemmer();
        for (String token : pureTokens) {
            st.add(token.toCharArray(), token.length());
            st.stem();
            stemms.add(st.toString());
            st = new Stemmer();
        }
        return stemms;
    }

    /*
     * Method InvertedIndex: It is used in help in mapping data such as word or
     * number from the content by giving them the locations/index.
     * 
     */

    private void InvertedIndex() {
        termList = new ArrayList<String>();
        docLists = new ArrayList<ArrayList<Integer>>();
        docList = new ArrayList<Integer>();
        for (int i = 0; i < myDocs.size(); i++) {
            for (String word : myDocs.get(i)) {
                if (!termList.contains(word)) {
                    termList.add(word);
                    docList = new ArrayList<Integer>();
                    docList.add(i);
                    docLists.add(docList);
                } else {
                    int index = termList.indexOf(word);
                    docList = docLists.get(index);
                    if (!docList.contains(i)) {
                        docList.add(i);
                        docLists.set(index, docList);
                    }
                }
            }
        }

        String outputString = "";
        for (int j = 0; j < termList.size(); j++) {
            outputString += String.format("%-15s", termList.get(j));
            docList = docLists.get(j);
            for (int k = 0; k < docList.size(); k++) {
                outputString += docList.get(k) + "\t";
            }
            outputString += "\n";
        }
        System.out.println(outputString);

    }

    /*
     * Method search: It is used to search the given query and return its index if
     * present otherwise return null
     * 
     * @param query: we are passing the query in the string format which will be
     * searched if it is present in the given termList.
     * 
     * @return docLists.get(index): if the given query match with the word in the
     * termLists then it will return the index of the docList which is the place of
     * the file where query is present.
     * 
     */

    public ArrayList<Integer> search(String query) {
        int index = termList.indexOf(query);
        if (index >= 0) {
            return docLists.get(index);
        } else
            return null;
    }

    /*
     * Method Merge: it will be used when it will recieve postion list in the form
     * of ArrayList<Integer> and then it will check the both the quantity list
     * whether any of those contain value or not if they won't have values then it
     * will just return that searched queries are invalid
     * 
     * @param postingList1,postingList2 : these are the values recieved from the end
     * function it contains the index of the value present in the docLists or null
     * 
     * @return mergedList: it will store the value returned from the index of the
     * posting List.
     * 
     */

    public ArrayList<Integer> Merge(ArrayList<Integer> postingList1, ArrayList<Integer> postingList2) {
        ArrayList<Integer> mergedList = new ArrayList<Integer>();
        int i = 0, j = 0;
        if (postingList1 != null || postingList2 != null) {
            while (i < postingList1.size() && j < postingList2.size()) {
                if (postingList1.get(i) == postingList2.get(j)) {
                    mergedList.add(postingList1.get(i));
                    i++;
                    j++;
                } else if (postingList1.get(i) < postingList2.get(j)) {
                    i++;
                } else {
                    j++;
                }
            }
        } else {
            System.out.println("Searched queries are invalid and not available please check.");
        }
        return mergedList;
    }

    /*
     * Method search_for_one will take a single query and and at first it will check
     * if its a single query and if its not then it will print invalid query
     * otherwise it will print the filename/filenames in which this given qord is
     * present.
     * 
     * @param words: It is the query given in the method which will be compared with
     * the given termList.
     */

    public void search_for_one(String words) {
        String[] term = words.split(" ");
        System.out.println("Query entered is: \n" + words);
        if (term.length == 1) {
            ArrayList<Integer> al = new ArrayList<>();
            Stemmer st = new Stemmer();
            String stTest = words;
            st.add(stTest.toCharArray(), stTest.length());
            st.stem();
            al = search(st.toString());
            if (al != null) {
                // System.out.println(words + " is at " + al);
                for (int i = 0; i < al.size(); i++) {
                    System.out.println(docs[al.get(i)]);
                }
            } else {
                System.out.println(words + " is not present in the document ");
            }
        } else {
            System.out.println("Invalid query please check for 1 word only");
        }
    }

    /*
     * Method search_AND_for_two: this is the method that will take 2 queries in the
     * form of String array and then split it. It will tokenize the given queries
     * and search for the words and give the index number or the null value. After
     * that it will call the merge function
     * 
     * @param query: It will take the query in the form of the String array.
     */
    public void search_AND_for_two(String query) {
        ArrayList<Integer> outcomeA = new ArrayList<>();
        ArrayList<Integer> outcomeB = new ArrayList<>();
        ArrayList<Integer> output = new ArrayList<>();
        ArrayList<String> temp1 = new ArrayList<>();
        ArrayList<String> temp2 = new ArrayList<>();
        System.out.println("Queries entered are: \n" + query);
        String[] word = query.split(" ");
        if (word.length == 2) {
            String word1 = query.split(" ")[0];
            String word2 = query.split(" ")[1];
            temp1 = (tokenization(word1));
            temp2 = (tokenization(word2));

            for (String term : temp1) {
                outcomeA = search(term);
            }
            for (String term : temp2) {
                outcomeB = search(term);
            }
            output = Merge(outcomeA, outcomeB);

            if (output.size() == 0) {
                System.out.println("List is not present in the document");
            } else {
                for (int i : output)
                    System.out.println(docs[i]);
            }
        } else {
            System.out.println("Invalid Query please fill 2 words only");
        }
    }

    /*
     * Method seach_OR_for_two: this is the method that will take 2 queries in the
     * form of String array and then split it. It will tokenize the given queries
     * and search for the words and give the index number or the null value. After
     * that it will Select the files that are not coming once and remove the
     * repeated file names.
     * 
     * @param query: It will take the query in the form of the String array.
     * 
     */

    public void search_OR_for_two(String query) {
        ArrayList<Integer> outcomeA = new ArrayList<>();
        ArrayList<Integer> outcomeB = new ArrayList<>();
        ArrayList<Integer> output = new ArrayList<>();
        ArrayList<String> temp1 = new ArrayList<>();
        ArrayList<String> temp2 = new ArrayList<>();
        System.out.println("Queries entered are: \n" + query);

        String[] word = query.split(" ");
        if (word.length == 2) {
            String word1 = query.split(" ")[0];
            String word2 = query.split(" ")[1];
            temp1 = (tokenization(word1));
            temp2 = (tokenization(word2));
            for (String term : temp1) {
                outcomeA = search(term);
            }
            for (String term : temp2) {
                outcomeB = search(term);
            }

            if (outcomeA != null && outcomeB != null) {
                output.addAll(outcomeA);
                for (int i = 0; i < outcomeB.size(); i++) {
                    if (!output.contains(outcomeB.get(i))) {
                        output.add(outcomeB.get(i));
                    }
                }
            } else if (outcomeB == null && outcomeA != null) {
                System.out.println("one search query not found");
                output.addAll(outcomeA);
            } else if (outcomeA == null && outcomeB != null) {
                System.out.println("one search query not found");
                output.addAll(outcomeB);
            } else {
                System.out.println("No search query found");
            }
            if (output.size() != 0) {
                for (int i : output)
                    System.out.println(docs[i]);
            } else {
                System.out.println("OR List not found");
            }
        } else {
            System.out.println("Invalid Query please fill 2 words only");
        }
    }

    /*
     * Method sort: it will take the ElementList/postingList and sizeList from the
     * search_AND_for_three function and then based on the size of the list it will sort using bubble sorting.
     *
     *@param postingList=> This is the list which will give the queries.
     *
     *@param postingList=> This is the list which will give the size of the number of docLists in which queries are present.
     */

    private void sort(ArrayList<String> postingList, ArrayList<Integer> sizeList) {

        for (int i = 0; i < sizeList.size() - 1; i++) {
            int smallest = i;
            for (int j = i + 1; j < sizeList.size(); j++) {
                if (sizeList.get(j) < sizeList.get(smallest))
                    smallest = j;
            }
            int temp = sizeList.get(smallest);
            sizeList.set(smallest, sizeList.get(i));
            sizeList.set(i, temp);
            String temp2 = postingList.get(smallest);
            postingList.set(smallest, postingList.get(i));
            postingList.set(i, temp2);
        }
    }

     /*
     * Method search_AND_for_three: First it will take the query and tokenize it and
     * sort it and then with respect to the sorted list it will add the filenames in the result and print it.
     *
     *@param query=> It will take the query in the form of the String.
     *
     */

    public void search_AND_for_three(String query) {
        System.out.println("Queries entered are: \n" + query);
        ArrayList<Integer> Result = new ArrayList<>();
        ArrayList<Integer> InitialList;
        ArrayList<String> postingList = new ArrayList<String>();
        ArrayList<Integer> sizeList = new ArrayList<Integer>();
        ArrayList<String> words = tokenization(query);
        System.out.println(words);
        if (words.size() >= 3) {
            for (String term : words) {
                InitialList = search(term);
                if (InitialList == null) {
                    System.out.println("Word not found");
                    break;
                } else {
                    postingList.add(term);
                    sizeList.add(InitialList.size());
                }
            }
            sort(postingList, sizeList);
            for (String word : postingList) {
                InitialList = search(word);
                if (Result.size() == 0) {
                    for (Integer i = 0; i < InitialList.size(); i++) {
                        Result.add(InitialList.get(i));
                    }
                } else {
                    Result = Merge(Result, InitialList);
                    if (Result.size() == 0) {
                        break;
                    }
                }
            }
            if (Result.size() != 0) {
                for (int i : Result)
                    System.out.println(docs[i]);
            } else {
                System.out.println("Merged List not found");
            }
        } else {
            System.out.println("Invalid Query please fill 3 or more words");
        }
    }

    public static void main(String args[]) throws Exception {
        Lab1 p = new Lab1();
        int i = 0;
        String FileData = "";
        for (String lists : docs) {
            FileData = p.ReadFiles("Lab1_Data/" + lists);
            p.assignToDoclist(i++, FileData);
        }
        p.readStopWords();
        ArrayList<String> DataFiles = p.DataFiles;
        ArrayList<ArrayList<String>> myDocs = p.myDocs;
        for (String file : DataFiles) {
            myDocs.add(p.tokenization(file));
        }
        p.InvertedIndex();
        System.out.println("\nsearch for 1 variable :");
        p.search_for_one("plain");
        System.out.println("\nsearch for 2 variable using AND :");
        p.search_AND_for_two("star time");
        // p.search_AND_for_two("episod episod");
        System.out.println("\nsearch for 2 variable using OR :");
        p.search_OR_for_two("star time");
        System.out.println("\nsearch for 3 or more variable using AND :");
        p.search_AND_for_three("watch bring video");

    }

}
