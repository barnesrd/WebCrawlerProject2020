 /**
 * Name: Ryan Barnes 
 * Desc: Class used to handle the offline operations of the web crawler
 */

import java.util.*;
import java.io.*;

public class OfflineOps implements Serializable {
/*
The concept of this program is to handle all of the offline operations 
required in this project. A core hashmap dictionary will be kept when
the program runs, and will serialize its data when the program exits
so that future runs will be able to access that data. 
Some functionalities include:
    - Serializing dictionary data that can be imported in later runs
    - Using previous dictionary data to add to a pre-existent dictionary 
      if new urls are found
    - When displaying the sorted urls, will sort the urls using the core
      dictionary
*/
    // Used to keep track of urls and their associated hits
    private HashMap<String, Integer> coreDictionary;
    // Used to keep track of urls and their associated body texts
    private HashMap<String, String[]>  allDictionary;
    // Used to keep track of the current word to prevent irrelevant data being loaded
    private String currWord;
    
    public OfflineOps(HashMap<String, Integer> dictionary, HashMap<String,String[]> allDictionary, String currWord){
        // Catch for if dictionaries come up empty
        if(dictionary == null || allDictionary == null){
            System.out.println("ERROR: Offline Operations were halted due to a "
                    + "null dictionary being passed");
            System.exit(1);
        }
        this.currWord = currWord;
        this.allDictionary = allDictionary;
        this.coreDictionary = dictionary;
    }
    
    public OfflineOps(){
        
    }
    
    // Used when in online mode
    public void performOnlineTasks(){
        // Get serialized dictionaries
        mergeDictionaries();
        // Make a tree from the merged coreDictionary
        BST tree = makeTree(coreDictionary);
        // Tree printout
        System.out.println(tree.getSize() + " urls found!");
        if(tree.getSize() == 0){
            return;
        }
        System.out.println("| Hits | URL");
        tree.printTree(tree.getHead());
        // Serialize data for future use
        serialize();
    }
    
    // Used when in offline mode
    public void performOfflineTasks(String search){
        // Variation of mergeDictionaries for offline mode
        mergeOfflinedictionaries();
        // Count the hits for the current searchword in the allDictionary
        // And output results to the coreDictionary
        countOffline(search);
        // Tree printout
        if(coreDictionary == null){
            System.out.println("The search came up empty");
            return;
        }
        BST tree = makeTree(coreDictionary);
        System.out.println(tree.getSize() + " urls found!");
        if(tree.getSize() == 0){
            return;
        }
        System.out.println("| Hits | URL");
        tree.printTree(tree.getHead());
        // Serialize data for future use
        serialize();
    }
    
    // Online variant to merge coreDictionaries and allDictionaries
    public void mergeDictionaries(){
        // Getting old versions
        OfflineOps oldOps = deSerialize();
        // If an old version does not exist, exit
        if(oldOps == null){
            return;
        }
        // If the words searched are different, then previous dictionary has irrelevant data
        if(!oldOps.currWord.equals(this.currWord)){
            return;
        }
        // The dictionaries have relevant data, so if entries to not already exist in our
        // current dictionary, we add to ours from the old one.
        for(String urls : oldOps.coreDictionary.keySet()){
            if(!this.coreDictionary.containsKey(urls)){
                this.coreDictionary.put(urls, oldOps.coreDictionary.get(urls));
            }
            if(!this.allDictionary.containsKey(urls)){
                this.allDictionary.put(urls, oldOps.allDictionary.get(urls));
            }
        }
    }
    
    // Offline dictionary that merges only allDictionaries
    public void mergeOfflinedictionaries(){
        // Getting old version
        OfflineOps oldOps = deSerialize();
        if(oldOps == null){
            return;
        }
        // defining our current allDictionary as our old one
        this.allDictionary = oldOps.allDictionary;
    }
    
    // Offline counting
    public void countOffline(String search){
        // If the allDictionary is empty, we don't want to count
        if(this.allDictionary.isEmpty()) return;
        // Hits to show word count on page
        int hits = 0;
        // For all urls in alldictionary
        for(String url : allDictionary.keySet()){
            // Get the body text
            String[] array = allDictionary.get(url);
            // For words in that body text
            for(String word : array){
                // If search matches the word, hits is incremented
                if(word.toLowerCase().equals(search)) hits++;
            }
            // If we found a hit, update coreDictionary
            if(hits > 0){
                coreDictionary.put(url, hits);
            }
            // Reset hit counter
            hits = 0;
        }
        
    }
    
    // Will input the dictionary and create a sorted tree
    public BST makeTree(HashMap<String, Integer> dictionary){
        BST tree = new BST();
        // Initializing nodes with <url, instances>
        for(String key : dictionary.keySet()){
            Node newNode = new Node(key, dictionary.get(key));
            tree.insert(newNode);
        }
        return tree;
    }
    
    // Used to serialize OfflineOps
    public void serialize(){
        try {
            System.out.format("Serializing current version under serialized.ser \n");
            // Create a file on the disk using FileOutputStream
            FileOutputStream fileOut = new FileOutputStream(String.format("serialized.ser"));
            // Create an object target stream that is connected to the file.
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            // Write object to object output stream
            objOut.writeObject(this);
            // Close and clear all resources
            fileOut.close();
            objOut.close();
            
            System.out.println("Serialization complete!");
            
        }
        catch(Exception er){
            System.out.println(er.getMessage());
        }
    }
    
    // Used to retrieve previous versions of OfflineOps
    public static OfflineOps deSerialize(){
        OfflineOps oldOffOp = new OfflineOps();
        try{
            // Input stream file
            FileInputStream fileIn = new FileInputStream(String.format("serialized.ser"));
            // Create an object input stream from file
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            
            System.out.println("Previous serialized dictionary found! Deserializing...");
            // ObjectImputStream into Employee e
            oldOffOp = (OfflineOps)objIn.readObject();
            fileIn.close();
            objIn.close();
            
            System.out.println("Deserialized successfully!");
            return oldOffOp;
        }
        catch(Exception er){
            System.out.println("Serialized dictionary not found");
            return null;
        }
    }
    
}
