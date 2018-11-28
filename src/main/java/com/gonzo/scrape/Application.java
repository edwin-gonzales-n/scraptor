package com.gonzo.scrape;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        long property_id = 5;
        String location = "30.261815, -97.719815";

        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
        String cstdate = dateFormatGmt.format(new Date());

        String baseUrl = "https://www.7eastaustin.com/Floor-plans.aspx";

        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        try {
            HtmlPage page = client.getPage(baseUrl);

            List<HtmlElement> itemList = page.getByXPath("//div[contains(@class,'floorplan-block')]");
            if(itemList.isEmpty()){
                System.out.println("No item found");
            }else{

                for(HtmlElement htmlItem : itemList){
                    String price = ((DomAttr) htmlItem.getFirstByXPath("./meta[contains(@name, 'minimumMarketRent')]/@content")).getValue();
                    String price2 = ((DomAttr) htmlItem.getFirstByXPath("./meta[contains(@name, 'maximumMarketRent')]/@content")).getValue();
                    String pricing = String.format("$%s - $%s", price, price2);
                    String url = ((DomAttr) htmlItem.getFirstByXPath("./div/div/div/div/ul/li/span/a/img/@src")).getValue();
                    String title = ((HtmlElement) htmlItem.getFirstByXPath("./div/div/div/div[contains(@class,'specification')]")).asText().replaceAll("\\n"," ");
                    String amenities = ((HtmlElement) htmlItem.getFirstByXPath("./div/div/div/div/div[contains(@class,'amenities-container')]")).asText().replaceAll("\\n",", ");
                    String info = ((HtmlElement) htmlItem.getFirstByXPath("./div/div/div/div/p[contains(@class,'pt')]")).asText(); // see StringTokenizer below

                    /*
                     * StringTokenizer will count the words within a string.  I used this object in order to filter the 'info' variable.
                     * It would come back with the apartment info but for some it would contain a full description.
                     * So by using the tokenizer we could filter the data input that is longer than 4 words and replace them
                     * with a simple 'require within'
                     * Please see logic in the if statement below.
                     */
                    StringTokenizer stringTokenizer = new StringTokenizer(info);
                    System.out.println("This is how many words: " + stringTokenizer.countTokens());

                    if(stringTokenizer.countTokens() > 4){
                        info = "Inquire within";
                        System.out.println("Title: " + title + "\nprice range: " + pricing + "\nAmenities: " + amenities + "\nDescription: " + info + "\nLocation: " + location + "\nUrl: " + url);
                    } else {
                        System.out.println("Title: " + title + "\nprice range: " + pricing + "\nAmenities: " + amenities + "\nDescription: " + info + "\nLocation: " + location + "\nUrl: " + url);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
