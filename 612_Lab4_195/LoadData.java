
/** 
* Text Classification using a Naïve Bayes (Multinomial) Classifier
* @author: Kheman Garg ISTE-612-2195 Lab #4 4/15/2020
*/

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LoadData {
    public HashMap<Integer, String> docs;
    public HashMap<Integer, String> TestDocs;
    public static HashMap<Integer, String> DocsFileNameReferenceMap;
    public int[] classes;

    public LoadData(String trainFolderPath, String testFolderPath) {
        File trainPos = new File(trainFolderPath + "/pos");
        File trainNeg = new File(trainFolderPath + "/neg");
        File testPos = new File(testFolderPath + "/pos");
        File testNeg = new File(testFolderPath + "/neg");

        docs = new HashMap<Integer, String>();
        DocsFileNameReferenceMap = new HashMap<Integer, String>();
        TestDocs = new HashMap<Integer, String>();
        int i = 0;
        classes = new int[2000];
        try {
            for (File file : trainPos.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    byte[] bytes;
                    bytes = Files.readAllBytes(Paths.get(file.getPath()));
                    String content = new String(bytes, "UTF-8");
                    docs.put(i, content.toLowerCase());
                    DocsFileNameReferenceMap.put(i, file.getName());
                    classes[i] = 1;
                    i++;
                }
            }
            for (File file : trainNeg.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    byte[] bytes;
                    bytes = Files.readAllBytes(Paths.get(file.getPath()));
                    String content = new String(bytes, "UTF-8");
                    docs.put(i, content.toLowerCase());
                    DocsFileNameReferenceMap.put(i, file.getName());
                    classes[i] = 0;
                    i++;
                }
            }
            for (File file : testPos.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    byte[] bytes;
                    bytes = Files.readAllBytes(Paths.get(file.getPath()));
                    String content = new String(bytes, "UTF-8");
                    TestDocs.put(i, content.toLowerCase());
                    DocsFileNameReferenceMap.put(i, file.getName());
                    classes[i] = 1;
                    i++;
                }
            }
            for (File file : testNeg.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    byte[] bytes;
                    bytes = Files.readAllBytes(Paths.get(file.getPath()));
                    String content = new String(bytes, "UTF-8");
                    TestDocs.put(i, content.toLowerCase());
                    DocsFileNameReferenceMap.put(i, file.getName());
                    classes[i] = 0;
                    i++;
                }
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }
}