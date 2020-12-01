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
    private HashMap<String, Integer> coreDictionary;
    transient private String word;
    
    public OfflineOps(HashMap<String, Integer> dictionary, String word){
        if(dictionary == null){
            System.out.println("ERROR: Offline Operations were halted due to a "
                    + "null dictionary being passed");
            System.exit(1);
        }
        this.coreDictionary = dictionary;
        this.word = word;
    }
    
    public OfflineOps(){
        
    }
    
    public void performOfflineTasks(){
        mergeDictionaries();
        BST tree = makeTree();
        System.out.println(tree.getSize() + " urls found!");
        if(tree.getSize() == 0){
            return;
        }
        System.out.println("| Hits | URL");
        tree.printTree(tree.getHead());
        serialize(word);
        
    }
    
    public void mergeDictionaries(){
        OfflineOps oldOps = deSerialize(this.word);
        if(oldOps == null){
            return;
        }
        for(String urls : oldOps.coreDictionary.keySet()){
            if(!this.coreDictionary.containsKey(urls)){
                this.coreDictionary.put(urls, oldOps.coreDictionary.get(urls));
            }
        }
    }
    
    // Will input the dictionary and create a sorted tree
    public BST makeTree(){
        BST tree = new BST();
        for(String key : coreDictionary.keySet()){
            Node newNode = new Node(key, coreDictionary.get(key));
            tree.insert(newNode);
        }
        return tree;
    }
    
    // Used to serialize OfflineOps
    public void serialize(String currWord){
        try {
            System.out.format("Serializing current version under %s.ser \n", currWord);
            // Create a file on the disk using FileOutputStream
            FileOutputStream fileOut = new FileOutputStream(String.format("%s.ser", currWord));
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
    public static OfflineOps deSerialize(String currWord){
        OfflineOps oldOffOp = new OfflineOps();
        try{
            // Input stream file
            FileInputStream fileIn = new FileInputStream(String.format("%s.ser", currWord));
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
