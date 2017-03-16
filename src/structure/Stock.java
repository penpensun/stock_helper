package structure;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;


public class Stock {
	private float[] revenue; 
	private float[] equity;
	private float[] ebit;
	private float[] employeeNum;
	private float[] priceArray;
	private float[] yearArray;
	
	
	public float[] getRevenue(){
		return revenue;
	}
	public float[] getPriceArray() {
		return priceArray;
	}
	public void setPriceArray(float[] priceArray) {
		this.priceArray = priceArray;
	}
	
	public void parseWebpage(String url){
		System.out.println(url);
		Document doc = null;
        try{
            doc = Jsoup.connect(url).get();
        }catch(IOException e){
            e.printStackTrace();;
        }
        Element bguvFormElement = doc.getElementById("bguvform");
        Elements mainElementList = bguvFormElement.getElementsByClass("main");
        if(mainElementList.isEmpty())
            throw new IllegalArgumentException("No main class found");
        Element mainElement = mainElementList.get(0);
        
        //Extract the revenue;
        extractRevenue(mainElement);
	}
	
	public void extractRevenue(Element mainElement){
		// Get the list of "content tableQuote" elements.
        Elements contentBoxTableQuoteElementList = mainElement.getElementsByClass("contentBox tableQuotes");
        // Check the number of elements in contentTableQuoteElementList
        if(contentBoxTableQuoteElementList.isEmpty())
            throw new IllegalArgumentException("No content tableQuote class element found.");
        if(contentBoxTableQuoteElementList.size()<3)
        	throw new IllegalArgumentException("Less than 3 content tableQuote class element found.");
        
        Element thirdContentBoxTableQuote = contentBoxTableQuoteElementList.get(2);
      //Get the class content elements.
        Elements contentElementList = thirdContentBoxTableQuote.getElementsByClass("content");
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
        
        
        // The second row contains the umsatz
        Elements revenueList = trElementList.get(1).getElementsByTag("td");
        if(revenueList.isEmpty())
            throw new IllegalArgumentException("No td tag element found.");
        // The revenues are listed, starting from the 3rd element
        // convert the elements in revenuelist into array
        revenue  = new float[revenueList.size()-2];
        try{
        	for(int i=0;i<revenue.length;i++)
        		revenue[i] = NumberFormat.getNumberInstance(Locale.GERMAN)
    				.parse(revenueList.get(i+2).text()).floatValue();
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	public void extractEquity(Element mainElement){
		
	}
	
	public void extractEbit(Element mainElement){
		
	}
	
	public void extractEmployNum(Element mainElement){
		
	}
}
