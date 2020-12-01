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
        // Used to signal offline or online mode
        boolean modeSwitch = false;
        // Used to signal if a custom url is being used
        boolean addressSwitch = false;
        // Used to signal if all conditions are met for execution
        boolean executeFlag = false;
        while(!executeFlag){
            // Query for offline or online
            System.out.println("Webcrawler Project 1.3");
            System.out.println("Edited by Ryan Barnes and Andrew Piro-Yusico");
            System.out.println("Would you like to perform online or offline tasks?");
            System.out.print(">");
            String mode = input.nextLine().toLowerCase();
            switch (mode) {
                case "online":
                    break;
                case "offline":
                    modeSwitch = true;
                    break;
                default:
                    System.out.println("please specify 'offline' or 'online'");
                    System.out.println("-------------------------------------------");
                    continue;
            }
            // Query for search term
            System.out.println("What term would you like to search for?");
            System.out.print(">");
            response = input.nextLine().toLowerCase();
            // If online was specified before, we break out of the loop here
            if(modeSwitch){
                break;
            }
            // Query for custom starting address
            System.out.println("Would you like to specify a starting address? (Y/N)");
            System.out.print(">");
            String addressQuery = input.nextLine().toLowerCase();
            // Setting default address in case starting address is not chosen
            address = ("http://www.cnn.com");
            // Letting user specify starting address
            if(addressQuery.equals("y") || addressQuery.equals("yes")){
                // Setting custom address signal to true
                addressSwitch = true;
                System.out.println("Please enter a url (format is http://www.example.com)");
                System.out.print(">");
                address = input.nextLine();

                System.out.println("Starting at " + address);
            }
            // Query for pages to search
            System.out.println("How many pages would you like to search?");
            System.out.print(">");
            // Try-catch with buffer to stop nextInt from skipping next input
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
        
        // Initializing spider with maximum pages to search
        Spider spider = new Spider(pages);
        // If online was selected
        if(!modeSwitch){
            // If custom address was specified
            if(addressSwitch){
                spider.search(address, response);
            }
            // If custom address was not specified, default address is taken
            else{
                //spider.search("https://www.click2houston.com/", response);
                spider.search("http://www.cnn.com", response);
                //spider.search("http://www.hbu.edu", response);
                //spider.search("https://www.cnn.com/NewsPass/landingpage/", response);
            }
            // Perform online sorting operations
            spider.passToOnOp(response);
        }
        // If offline was selected
        else{
            // Perform offline operations
            spider.passToOffOp(response);
        }
        
    }
}