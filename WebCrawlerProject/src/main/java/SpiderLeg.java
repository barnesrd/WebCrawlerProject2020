/**
 * @COSC 2351 Data Structures [Web Crawler Project]
 * @author marianky & Ryan Barnes
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

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
    private HashMap<String, HashSet<String>> blackList = new HashMap<>();

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
            String robot = findRobots(url);
            if(!blackList.containsKey(robot)){
                readRobots(robot);
            }
            boolean skip = false;
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
                String[] extensions = link.absUrl("href").toLowerCase().split("/|\\?|\\*");
                if(extensions.length > 0){
                    for(String extension : extensions){
                        if(blackList.get(robot) != null && blackList.get(robot).contains(extension)){
                            System.out.println("Link " + link.absUrl("href") + " not allowed, removing from links to visit");
                            skip = true;
                            break;
                        }
                    }
                }
                
                if(!skip){
                    this.links.add(link.absUrl("href"));
                }
                skip = false;
            }
            return true;
        } catch (IOException ioe) {
            // We were not successful in our HTTP request
            return false;
        }
    }

/**
 * Performs a search on the body of the HTML document that is retrieved. This
 * method should only be called after a successful crawl.
 *
 * @param searchWord - The word or string to look for
 * @return whether or not the word was found
 */
public int searchForWord(String searchWord) {
        // This method should only be used after a successful crawl.
        if (this.htmlDocument == null) {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return 0;
        }
        System.out.println("Searching for the word " + searchWord + "...");
        int hitCount = 0;
        String[] bodyText = this.htmlDocument.body().text().split(" ");
        for(String word : bodyText){
            if(word.toLowerCase().equals(searchWord)){
                hitCount++;
            }
        }
        return hitCount;
        
        // Uncomment this to just search for if the word is present.
        //return bodyText.toLowerCase().contains(searchWord.toLowerCase());
    }

    public String findRobots(String absUrl){
            String openerURL = "http://";
            String[] urlSplit = absUrl.toLowerCase().split("/|\\?|\\*");
            String urlShort = urlSplit[2];
            String robots = openerURL.concat(urlShort);
            robots = robots.concat("/robots.txt");
            return robots;
    }
    
    public void readRobots(String url){
        try{
            HashSet<String> blacklistSet = new HashSet<>();
            URL robUrl = new URL(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(robUrl.openStream()));
            
            String line;
            while((line = reader.readLine()) != null){
                String[] tokens = line.split(" ");
                if(tokens.length > 1){
                    if(tokens[0].equals("Disallow:")){
                        String added = tokens[1].replace("/","");
                        added = added.replace("*", "");
                        blacklistSet.add(added.toLowerCase());
                    }
                }
            }
            blackList.put(url, blacklistSet);
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
