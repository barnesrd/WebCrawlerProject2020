/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CrawlerPackage;

/**
 *
 * @author andre
 */
public class SpiderTest {
     /**
     * The main method creates a spider object (which creates spider legs) and crawls the web.
     * 
     */
    public static void main(String[] args)
    {
        Spider spider = new Spider();
        spider.search("https://www.cnn.com", "Cyber");
    }
}
