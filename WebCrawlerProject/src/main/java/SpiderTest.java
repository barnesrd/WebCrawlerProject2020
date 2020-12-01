/**
 * @COSC 2351 Data Structures [Web Crawler Project] 
 * @author marianky
 */
import java.util.*;
public class SpiderTest
{
    static Scanner input = new Scanner(System.in);
    static String address;
    static String response;
    static int pages;
    
    /**
     * The main method creates a spider object (which creates spider legs) and crawls the web.
     * 
     */
    public static void main(String[] args)
    {
        boolean addressSwitch = false;
        boolean executeFlag = false;
        while(!executeFlag){
            System.out.println("Webcrawler Project 1.2");
            System.out.println("Edited by Ryan Barnes and Andrew Piro-Yusico");
            System.out.println("What term would you like to search for?");
            System.out.print(">");
            response = input.nextLine().toLowerCase();
            System.out.println("Would you like to specify a starting address? (Y/N)");
            System.out.print(">");
            String addressQuery = input.nextLine().toLowerCase();
            address = ("http://www.cnn.com");
            if(addressQuery.equals("y") || addressQuery.equals("yes")){
                addressSwitch = true;
                System.out.println("Please enter a url (format is http://www.example.com)");
                System.out.print(">");
                address = input.nextLine();

                System.out.println("Starting at " + address);
            }
            System.out.println("How many pages would you like to search?");
            System.out.print(">");
            try{
                pages = input.nextInt();
                String buffer = input.nextLine();
                executeFlag = true;
            }
            catch(Exception e){
                System.out.println("Your input could not be read");
                System.out.println("---------------------------------------------------");
                String buffer = input.nextLine();
            }
        }
        
        
        
        Spider spider = new Spider(pages);
        
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