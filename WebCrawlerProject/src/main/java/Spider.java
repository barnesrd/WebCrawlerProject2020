/**
 * @COSC 2351 Data Structures [Web Crawler Project] 
 * @author marianky & Ryan Barnes & Andrew Piro-Yusico
 */

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.*;

public class Spider {

    private int MAX_PAGES_TO_SEARCH = 100;
    private final Set<String> pagesVisited = new HashSet<String>();
    private final List<String> pagesToVisit = new LinkedList<String>();
    // Used to keep track of hits and their associated urls
    private final HashMap<String, Integer> dictionary = new HashMap<>();
    // Used to keep track of urls and their associated string arrays
    private final HashMap<String, String[]> allDictionary = new HashMap<>();
    
    
    public Spider(int max){
        this.MAX_PAGES_TO_SEARCH = max;
    }
    /**
     * The main launching point for the Spider's functionality. Internally it
     * creates spider legs that make an HTTP request and parse the response (the
     * web page).
     *
     * @param url - The starting point of the spider
     * @param searchWord - The word or string that you are searching for
     */
    public void search(String url, String searchWord) {
        // Initialized to prevent infinite loop when starting at a url with no outbound links
        boolean visitLockPreventer = false;
        while (this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) {
            String currentUrl;
            SpiderLeg leg = new SpiderLeg();
            if (this.pagesToVisit.isEmpty()) {
                // If the same webpage was visited two times in a row, exit
                // (This never happens under normal circumstances)
                if(visitLockPreventer){
                    System.out.println("No webpages to go to!");
                    break;
                }
                currentUrl = url;
                this.pagesVisited.add(url);
                // To signal that this page was visited, and prevent further
                // execution if it is visited again immediately
                visitLockPreventer = true;
            } else {
                currentUrl = this.nextUrl();
            }
            // For use in finding robots.txt
            String[] urlSplit = currentUrl.toLowerCase().split("/|\\?");
            String urlShort = urlSplit[2];

            // For use in naming file
            String fileUrl;
            if(urlSplit.length >= 4){
                fileUrl = urlShort.concat(urlSplit[3]);
                for(int i = 4; i < urlSplit.length; i++){
                    fileUrl = fileUrl.concat(urlSplit[i]);
                }
            }
            else fileUrl = urlShort;
            fileUrl = fileUrl.replace(".", "");
            fileUrl = fileUrl.concat(".html");
            
            leg.crawl(currentUrl); // Lots of stuff happening here. Look at the crawl method in
            // SpiderLeg
            int hits = leg.searchForWord(searchWord, allDictionary, currentUrl);
            // Making stopwords set
            leg.readStop();
            // If the word was found in the page
            if (hits > 0) {
                System.out.println(String.format("**Success** Word %s found at %s, %d occurrences found", searchWord, currentUrl, hits));
                // Put the url and the number of hits it got into a dictionary
                dictionary.put(currentUrl, hits);
                // For saving webpages that have hits to HTML
                File directory = new File("HTML");
                // Making filepath if it does not exit already
                if(!directory.exists()){
                    directory.mkdir();
                }
                File subDirectory = new File("HTML/" + searchWord);
                if(!subDirectory.exists()){
                    subDirectory.mkdir();
                }
                // Making HTML file under HTML/[searchword]/[url].html
                File newFile = new File("HTML/" + searchWord + "/"+ fileUrl);
                try{
                    FileWriter writer = new FileWriter(newFile);
                    BufferedWriter bwriter = new BufferedWriter(writer);
                    bwriter.write(leg.gethtmlDocument().html());
                    bwriter.close();
                    System.out.println("File downloaded successfully!");
                }
                catch(IOException e){
                    System.out.println(e.getMessage());
                    System.out.println("Unable to write document to file.");
                // To stop after one hit, enable this
                // break;
                }
            }
            // Get the outbound links
            this.pagesToVisit.addAll(leg.getLinks());
        }
        System.out.println("\n**Done** Visited " + this.pagesVisited.size() + " web page(s)");
    }

    /**
     * Returns the next URL to visit (in the order that they were found). We
     * also do a check to make sure this method doesn't return a URL that has
     * already been visited.
     *
     * @return
     */
    private String nextUrl() {
        String nextUrl;
        do {
            nextUrl = this.pagesToVisit.remove(0);
        } while (this.pagesVisited.contains(nextUrl));
        this.pagesVisited.add(nextUrl);
        return nextUrl;
    }
    
    // When online mode is on, this passes to online mode operations
    public void passToOnOp(String search){
        OfflineOps onOp = new OfflineOps(this.dictionary, this.allDictionary, search);
        
        onOp.performOnlineTasks();
    }
    // When online mode is on, this passes to offline mode operations
    public void passToOffOp(String search){
        OfflineOps offOp = new OfflineOps(this.dictionary, this.allDictionary, search);
        
        offOp.performOfflineTasks(search);
    }
}
