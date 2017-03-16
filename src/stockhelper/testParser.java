/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockhelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import org.jsoup.select.*;

import structure.Stock;
/**
 * This class test the parser of jsoup. The parser is resposible to extract stock information from http://www.finanzen.net/bilanz_guv/Wirecard
 * @author GGTTF
 */
public class testParser {
	
	
    public void testParser(){
        String wirecardURLString = "http://www.finanzen.net/bilanz_guv/Wirecard";
    	//String wirecardURLString = "http://www.google.com";
        /*
        URL wirecardURL = null;
        try{
            wirecardURL = new URL(wirecardURLString);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        */
        Document doc = null;
        try{
            doc = Jsoup.connect(wirecardURLString).get();
        }catch(IOException e){
            e.printStackTrace();;
        }
        Element bguvFormElement = doc.getElementById("bguvform");
        Elements mainElementList = bguvFormElement.getElementsByClass("main");
        if(mainElementList.isEmpty())
            throw new IllegalArgumentException("No main class found");
        // Get the list of "content tableQuote" elements.
        Elements contentBoxTableQuoteElementList = mainElementList.get(0).getElementsByClass("contentBox tableQuotes");
        // Check the number of elements in contentTableQuoteElementList
        if(contentBoxTableQuoteElementList.isEmpty())
            throw new IllegalArgumentException("No content tableQuote class element found.");
        // The first "content tableQuote" element contains "Ergebnis je Aktie (unverwaessert, nach Steurn)",
        // "Ergebnis je Aktie (verwaessert, nach Steurn)", "Dividende je Aktie".
        Element firstContentTableQuote = contentBoxTableQuoteElementList.get(0);
        
        //Get the class content elements.
        Elements contentElementList = firstContentTableQuote.getElementsByClass("content");
        if(contentElementList.isEmpty())
            throw new IllegalArgumentException("No content class element found.");
        // Get the table elements.
        Elements tableElementList = contentElementList.get(0).getElementsByTag("table");
        if(tableElementList.isEmpty())
            throw new IllegalArgumentException ("No table tag element found.");
        // Get the tbody elements.
        Elements tbodyElementList = tableElementList.get(0).getElementsByTag("tbody");
        if(tbodyElementList.isEmpty())
            throw new IllegalArgumentException("No tbody tag element found.");
        // Get the tr elements.
        Elements trElementList = tbodyElementList.get(0).getElementsByTag("tr");
        if(trElementList.isEmpty())
            throw new IllegalArgumentException("No tr tag element found.");
        
        //The first row contains the years
        Elements yearList = trElementList.get(0).getElementsByTag("th");
        if(yearList.isEmpty())
            throw new IllegalArgumentException("No th tag element found.");
        // Output the years.
        for(int i=0;i<yearList.size();i++)
            System.out.println("Year:  "+yearList.get(i).text());
        
        //The second row contains the "Ergebnis je Aktie (unverwässert, nach Steurn)"
        Elements eps1ElementList = trElementList.get(1).getElementsByTag("td");
        
        //Output the "Ergebnis je Aktie (unverwässert, nach Steurn)"
        for(int i=0;i<eps1ElementList.size();i++)
            System.out.println("Ergebnis je Aktie (unverwaessert, nach Steurn): "+eps1ElementList.get(i).text());
        
        //The third row contains the "Ergebnis je Aktie (verwässert, nach Steurn)"
        Elements eps2ElementList = trElementList.get(2).getElementsByTag("td");
         //Output the "Ergebnis je Aktie (verwässert, nach Steurn)"
        for(int i=0;i<eps2ElementList.size();i++)
            System.out.println("Ergebnis je Aktie (verwaessert, nach Steurn): "+eps2ElementList.get(i).text());
       
        
    }
    
    public void testExtractRevenue(){
    	Stock s = new Stock();
    	String url = "http://www.finanzen.net/bilanz_guv/Wirecard";
    	//url = "http://www.google.com";
    	s.parseWebpage(url);
    	float[] revenue = s.getRevenue();
    	for(int i=0;i<revenue.length;i++)
    		System.out.println(revenue[i]);
    }
    public static void main(String args[]){
        new testParser().testExtractRevenue();
    }
}
