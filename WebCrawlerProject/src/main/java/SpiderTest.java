/**
 * @COSC 2351 Data Structures [Web Crawler Project] 
 * @author marianky
 */
import java.util.*;
public class SpiderTest
{
    static Scanner input = new Scanner(System.in);
    /**
     * The main method creates a spider object (which creates spider legs) and crawls the web.
     * 
     */
    public static void main(String[] args)
    {
        boolean addressSwitch = false;
        System.out.println("Webcrawler Project 1.0");
        System.out.println("Edited by Ryan Barnes and Andrew Piro-Yusico");
        System.out.println("What term would you like to search for?");
        System.out.print(">");
        String response = input.nextLine().toLowerCase();
        System.out.println("Would you like to specify a starting address? (Y/N)");
        String addressQuery = input.nextLine().toLowerCase();
        String address = ("http://www.cnn.com");
        if(addressQuery.equals("y") || addressQuery.equals("yes")){
            addressSwitch = true;
            System.out.println("Please enter a url (format is http://www.example.com)");
            address = input.nextLine();

            System.out.println("Starting at " + address);
        }
        
        
        Spider spider = new Spider();
        
        if(addressSwitch){
            spider.search(address, response);
        }
        else{
            //spider.search("https://www.click2houston.com/", response);
            spider.search("http://www.cnn.com", response);
            //spider.search("http://www.hbu.edu", response);
            //spider.search("https://www.cnn.com/NewsPass/landingpage/", response);
        }
        
        spider.passToOffOp(response);
    }
}