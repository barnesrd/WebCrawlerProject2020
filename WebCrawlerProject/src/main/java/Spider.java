/**
 * @COSC 2351 Data Structures [Web Crawler Project] 
 * @author marianky
 */

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.*;

public class Spider {

    private static final int MAX_PAGES_TO_SEARCH = 100;
    private Set<String> pagesVisited = new HashSet<String>();
    private List<String> pagesToVisit = new LinkedList<String>();
    private HashMap<String, Integer> dictionary = new HashMap<>();
    
    
    /**
     * The main launching point for the Spider's functionality. Internally it
     * creates spider legs that make an HTTP request and parse the response (the
     * web page).
     *
     * @param url - The starting point of the spider
     * @param searchWord - The word or string that you are searching for
     */
    public void search(String url, String searchWord) {
        boolean visitLockPreventer = false;
        OUTER:
        while (this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) {
            String currentUrl;
            SpiderLeg leg = new SpiderLeg();
            if (this.pagesToVisit.isEmpty()) {
                if(visitLockPreventer){
                    System.out.println("No webpages to go to!");
                    break OUTER;
                }
                currentUrl = url;
                this.pagesVisited.add(url);
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
            int hits = leg.searchForWord(searchWord);
            if (hits > 0) {
                System.out.println(String.format("**Success** Word %s found at %s, %d occurrences found", searchWord, currentUrl, hits));
                dictionary.put(currentUrl, hits);
                File directory = new File("HTML");
                if(!directory.exists()){
                    directory.mkdir();
                }
                File subDirectory = new File("HTML/" + searchWord);
                if(!subDirectory.exists()){
                    subDirectory.mkdir();
                }
                
                File newFile = new File("HTML/" + searchWord + "/" + fileUrl);
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
    
    public void passToOffOp(String searchWord){
        OfflineOps offOp = new OfflineOps(this.dictionary, searchWord);
        offOp.performOfflineTasks();
    }
    
}



/*
Hash Map
Blacklist:
Key: base url
values: restricted extensions
*/