/**
 * @COSC 2351 Data Structures [Web Crawler Project]
 * @author marianky & Ryan Barnes & Andrew Piro-Yusico
 */

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.lang.Thread;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpiderLeg {

    //This sets a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    //This isnt' entirely the decent way of doing this
    //private static final String USER_AGENT
    //        = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    
    //This is the decent polite way of doing this!!
    private static final String USER_AGENT = "COSC 2351 testing crawler (hbu.edu)";
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;
    // Used to keep track of disallowed extensions in urls specified in [url]/robots.txt
    private HashMap<String, HashSet<String>> blackList = new HashMap<>();
    // Used to keep track of crawl-delay specified in [url]/robots.txt
    private HashMap<String, Integer> delay = new HashMap<>();
    // Used to keep track of words specified in stopwords.txt
    private HashSet<String> stopwords = new HashSet<>();

    /**
     * This performs all the work. It makes an HTTP request, checks the
     * response, and then gathers up all the links on the page. Perform a
     * searchForWord after the successful crawl
     *
     * @param url - The URL to visit
     * @return whether or not the crawl was successful
     */
    public boolean crawl(String url) {
        try {
            // Variable used to store the domain name's robots.txt for future use
            String robot = findRobots(url);
            // If there isn't already an entry in blacklist for this domain, add one
            // We only need to check blacklist, because readRobots adds to both blacklist and delay
            if(!blackList.containsKey(robot)){
                readRobots(robot);
            }
            // If there is an entry in delay for this domain, delay crawl by crawl-delay
            if(delay.containsKey(robot)){
                try{
                    System.out.println("Requested wait time of " + delay.get(robot) + "ms");
                    Thread.sleep(delay.get(robot));
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }
            // skip variable used to skip disallowed urls
            boolean skip = false;
            // Getting url's associated html data
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            if (connection.response().statusCode() == 200) // 200 is the HTTP OK status code
            // indicating that everything is great.
            {
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }
            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            // This is where links are added 
            for (Element link : linksOnPage) {
                // Getting the individual extensions of the url
                String[] extensions = link.absUrl("href").toLowerCase().split("/|\\?|\\*");
                // If there are extensions
                if(extensions.length > 0){
                    // For each extension
                    for(String extension : extensions){
                        // If the entry for this domain isn't null, and if the entry contains an extension
                        // in the url,
                        if(blackList.get(robot) != null && blackList.get(robot).contains(extension)){
                            System.out.println("Link " + link.absUrl("href") + " not allowed, removing from links to visit");
                            // Set variable to true to skip this link in adding process
                            skip = true;
                            break;
                        }
                    }
                }
                // If we need to skip this link
                if(!skip){
                    this.links.add(link.absUrl("href"));
                }
                // To ensure that all links after the first skipped are not skipped
                skip = false;
            }
            return true;
        } catch (IOException ioe) {
            // We were not successful in our HTTP request
            return false;
        }
    }

/**
 * Performs a search on the body of the HTML document that is retrieved.This
 method should only be called after a successful crawl.
 *
 * @param searchWord - The word or string to look for
 * @param allDictionary - the dictionary to be passed to OfflineOps
 * @param url - url to associate string[] to url
 * @return whether or not the word was found
 */
public int searchForWord(String searchWord, HashMap<String, String[]> allDictionary, String url) {
        // This method should only be used after a successful crawl.
        if (this.htmlDocument == null) {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return 0;
        }
        System.out.println("Searching for the word " + searchWord + "...");
        // Variable keeping track of how many searchWords are found
        int hitCount = 0;
        String[] bodyText = this.htmlDocument.body().text().split(" ");
        // <String, String[]> dictionary associates url and body text data
        allDictionary.put(url, bodyText);
        // for each word in body text
        for(String word : bodyText){
            // If the current word matches word being searched for
            if(word.toLowerCase().equals(searchWord) && !stopwords.contains(word.toLowerCase())){
                // Increment hit counter
                hitCount++;
            }
        }
        
        return hitCount;
        
        // Uncomment this to just search for if the word is present.
        //return bodyText.toLowerCase().contains(searchWord.toLowerCase());
    }

    public String findRobots(String absUrl){
            // The opening http for all urls
            String openerURL = "http://";
            // Split to find the base url
            String[] urlSplit = absUrl.toLowerCase().split("/|\\?|\\*");
            // Base url is 2 after http://[baseUrl]/extensions
            String urlShort = urlSplit[2];
            // Concatenating [http://] + [baseUrl]
            String robots = openerURL.concat(urlShort);
            // Putting it all together [http://] + [baseUrl] + [/robots.txt]
            robots = robots.concat("/robots.txt");
            return robots;
    }
    
    public void readRobots(String url){
        try{
            // Set to keep track of parsed data
            HashSet<String> blacklistSet = new HashSet<>();
            // Integer to keep track of delay time, if found
            Integer delayTime = null;
            // URL class allows reading from Input Stream
            URL robUrl = new URL(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(robUrl.openStream()));
            // Flag to make sure data being read is for us
            boolean forMe = false;
            // Line in robots.txt
            String line;
            // For each line
            while((line = reader.readLine()) != null){
                // Split each line by spaces
                String[] tokens = line.split(" ");
                // If the tokens are greater than one
                if(tokens.length > 1){
                    // If User-agent is being specified
                    if(tokens[0].equals("User-agent:")){
                        // We only want to read under user agent *
                        if(tokens[1].equals("*")) forMe = true;
                        // Not others
                        else forMe = false;
                    }
                    // If the data is for us
                    else if(forMe){
                        // Look at disallow data and add extensions to blacklist
                        if(tokens[0].equals("Disallow:")){
                            String added = tokens[1].replace("/","");
                            added = added.replace("*", "");
                            blacklistSet.add(added.toLowerCase());
                        }
                        // If crawl-delay is defined, assign it to delayTime
                        if(tokens[0].equals("crawl-delay:")){
                            delayTime = Integer.parseInt(tokens[1]);
                        }
                    }
                }
            }
            // Put extensions in blacklist
            blackList.put(url, blacklistSet);
            // If crawl-delay was defined, define it for this domain
            if(delayTime != null){
                delay.put(url, delayTime);
            }

        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    // Code that reads stopwords.txt and appends antries to hashset
    public void readStop(){
        try{
            // Load stopwords.txt into a file
            File text = new File("stopwords.txt");
            BufferedReader reader = new BufferedReader(new FileReader(text));
            // Read the file
            String line;
            while((line = reader.readLine()) != null){
                // For each line, add it to the hashset
                stopwords.add(line);
            }
            reader.close();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public List<String> getLinks() {
        return this.links;
    }
    
    public Document gethtmlDocument(){
        return this.htmlDocument;
    }

    public void setHtmlDocument(Document htmlDocument) {
        this.htmlDocument = htmlDocument;
    }

}
