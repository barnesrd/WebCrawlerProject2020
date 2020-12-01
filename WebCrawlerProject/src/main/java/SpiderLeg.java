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
    private HashMap<String, HashSet<String>> blackList = new HashMap<>();
    private HashMap<String, Integer> delay = new HashMap<>();
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
            String robot = findRobots(url);
            if(!blackList.containsKey(robot)){
                readRobots(robot);
            }
            if(delay.containsKey(robot)){
                try{
                    System.out.println("Requested wait time of " + delay.get(robot) + "ms");
                    Thread.sleep(delay.get(robot));
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
                
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
            if(word.toLowerCase().equals(searchWord) && !stopwords.contains(word.toLowerCase())){
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
            Integer delayTime = null;
            
            URL robUrl = new URL(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(robUrl.openStream()));
            boolean forMe = false;
            String line;
            while((line = reader.readLine()) != null){
                String[] tokens = line.split(" ");
                if(tokens.length > 1){
                    if(tokens[0].equals("User-agent:")){
                        if(tokens[1].equals("*")) forMe = true;
                        else forMe = false;
                    }
                    else if(forMe){
                        if(tokens[0].equals("Disallow:")){
                            String added = tokens[1].replace("/","");
                            added = added.replace("*", "");
                            blacklistSet.add(added.toLowerCase());
                        }
                        if(tokens[0].equals("crawl-delay:")){
                            delayTime = Integer.parseInt(tokens[1]);
                        }
                    }
                }
            }
            blackList.put(url, blacklistSet);
            if(delayTime != null){
                delay.put(url, delayTime);
            }

        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    public void readStop(){
        try{
            File text = new File("stopwords.txt");
            BufferedReader reader = new BufferedReader(new FileReader(text));
            String line;
            while((line = reader.readLine()) != null){
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
