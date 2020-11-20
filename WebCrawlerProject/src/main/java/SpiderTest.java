/**
 * @COSC 2351 Data Structures [Web Crawler Project] 
 * @author marianky
 */

public class SpiderTest
{
    /**
     * The main method creates a spider object (which creates spider legs) and crawls the web.
     * 
     */
    public static void main(String[] args)
    {
        Spider spider = new Spider();
        spider.search("http://www.cnn.com", "the");
    }
}