package infoextractor.impl;
import infoextractor.WebStockExtractor;
import structure.Stock;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import org.jsoup.select.*;
import java.util.ArrayList;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * This class extracts the content of the GuV webpage.
 * The structure of the GuV webpage.
 * 
 * form id = "bguvform" --> div class = "main" 
 * --> several <div class= "contentBox tableQuotes" >
 * --> <div class = "content">
 * --> <table>
 * --> <tbody> 
 * --> serverl rows with <tr>. The first row is the title.
 * 
 * @author penpen926
 *
 */
public class FinanzenStockExtractor implements WebStockExtractor{
	Document webStockDoc = null;
	
	
	// the list of "tbody" elements on the webpage.
	ArrayList<Element> tbodyList;
	
	private float[] revenue;
	private float[] equity;
	private float[] ebit;
	private float[] employeeNum;
	private int[] years;
	private float[] prices;

	/**
	 * 
	 * @author: Peng Sun
	 * @param url: The url of the website
	 * @return: void
	 */
	@Override
	public void parseWebpage(String urlString){	
		//User HttpURLConnection to connect to the webpage
		URL url = null;
		try{
			url = new URL(urlString);
		}catch(MalformedURLException e){
			e.printStackTrace();
		}
		
		//Init the proxy
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.185.190.100", 8080));
		
		
		HttpURLConnection urlConn = null;
		try{
			urlConn = (HttpURLConnection)url.openConnection(proxy);
		}catch(IOException e){
			e.printStackTrace();
		}
		//Read the webpage into a StingBuilder
		StringBuilder webpageStrBuilder = new StringBuilder();
		BufferedReader br = null;
		try{
			br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
		}catch(IOException e){
			e.printStackTrace();
		}
		String line= null;
		try{
			while((line =br.readLine())!= null)
				webpageStrBuilder.append(line).append("\n");
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
		//Parse the webpage by Jsoup
        webStockDoc = Jsoup.parse(webpageStrBuilder.toString());
        
        
        //Init FinanzenWebpageExtractor
        FinanzenWebpageExtractor webpageExtractor = new FinanzenWebpageExtractor();
        //Init FinanzenWebpageParser
        FinanzenWebpageParser webpageParser = new FinanzenWebpageParser();
        
        ArrayList<Element> contentBoxList = webpageExtractor.extractContentBoxList(webStockDoc);   
        // Init the tbodylist
        tbodyList = new ArrayList<>();
        // For each contentBoxList, get the tbody element 
        for(Element contentBox: contentBoxList){
        	
        	// For each contentBox element, we extract the tbody element.
        	Element tbody = webpageExtractor.extractTbodyElement(contentBox);
        	// Put the tbody element into the list
        	tbodyList.add(tbody);
        }
        
        Element tbodyElement = null;
        
        // Parse the revenue.
        // The revenue is in the three tbody
        tbodyElement = tbodyList.get(2);
        revenue = webpageParser.parseRevenue(tbodyElement);
        
        // Parse the equity 
        tbodyElement = tbodyList.get(1);
        years = webpageParser.parseYears(tbodyElement);
	}
	
	
	
	public Stock initStockObj(){
		Stock s = null;
		return s;
	}
	
	
	
	public float[] getRevenue() {
		return revenue;
	}
	
	public float[] getEquity() {
		return equity;
	}

	public float[] getEbit() {
		return ebit;
	}


	public float[] getEmployeeNum() {
		return employeeNum;
	}

	@Override
	public int[] getYears() {
		
		return years;
	}


	@Override
	public float[] getPrices() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This method parses the stock webpage and outputs the year array.
	 */
	public float[] parseYearArray(){
		float[] yearArray;
		Element bguvFormElement = webStockDoc.getElementById("bguvform");
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
        
        
        // Parse the years.
        yearArray= new float[yearList.size()];
        for(int i=0;i<yearList.size();i++)
        	yearArray[i] = Float.parseFloat(yearList.get(i).text());
		return yearArray;
	}

	
	/**
	 * This method parses the stock webpage for 
	 * @return
	 */
	public float[] parseEquity(){
		
	}


	@Override
	public Stock getStockObj() {
		// TODO Auto-generated method stub
		return null;
	}
	


	/**
	 * A test method to test the proxy in java connection.
	 * 
	 * test successful
	 */
	public void testConnect(){
		URL testUrl = null;
		try{
			testUrl = new URL("http://www.finanzen.net/aktien/Wirecard-Aktie");
		}catch(MalformedURLException e){
			e.printStackTrace();
		}
		Proxy p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.185.190.100", 8080));
		HttpURLConnection uc = null;
		try{
			uc = (HttpURLConnection)testUrl.openConnection(p);
			//uc = (HttpURLConnection)testUrl.openConnection();
			uc.connect();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		//Open a inputstream to get the http content
		String line = null;
		StringBuilder webContentTmp = new StringBuilder();
		BufferedReader br = null;
		try{
			br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		}catch(IOException e){
			e.printStackTrace();
		}
		
		try{
		while((line = br.readLine())!= null){
			webContentTmp.append(line).append("\n");
		}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		System.out.println(webContentTmp.toString());
	}

	
	public static void main(String args[]){
		new FinanzenStockExtractor().testConnect();
	}

}


/**
 * This class is responsible for pre-parse. 
 * The class FinanzenWebpageExtractor receives the Document object (contains GuV webpage) and returns
 * a list of <tbody>. 
 * 
 * @author penpen926
 *
 */

class FinanzenWebpageExtractor{
	/**
	 * This method extracts the contentBox in the webpage.
	 * The method receives the root element of the webpage and returns a list of 
	 * <div class = "content"> elements.
	 * @param doc
	 * @return
	 */
	ArrayList<Element> extractContentBoxList(Document doc){
		ArrayList<Element> contentBoxList;
		// Parse the webpage and init the tbodylist
        Element bguvFormElement = doc.getElementById("bguvform");
        Elements mainElementList = bguvFormElement.getElementsByClass("main");
        if(mainElementList.isEmpty())
            throw new IllegalArgumentException("No main class found");
        
        // Get the list of "content tableQuote" elements.
        contentBoxList = mainElementList.get(0).getElementsByClass("contentBox tableQuotes");
        // Check the number of elements in contentTableQuoteElementList
        if(contentBoxList.isEmpty())
            throw new IllegalArgumentException("No content tableQuote class element found.");
       
        return contentBoxList;
	}
	
	/**
	 * This method extracts the tbody element within a contentBox.
	 * The method receives the element of <div class = "content"> and returns the <tbody> within the element.
	 * @param contentBox
	 * @return
	 */
	Element extractTbodyElement(Element contentBox){
		// First get the content element
    	Elements contentList = contentBox.getElementsByClass("content");
    	// Check the content element list.
    	// check if the contentList is empty.
    	if(contentList.isEmpty())
            throw new IllegalArgumentException("No content class element found.");
        // check if the contentList contains more than one "content" element. 
    	if(contentList.size()>1)
    		throw new IllegalArgumentException("More than one content class element are found.");
    	
    	// For the content element, we go further to the table element.
    	Elements tableElementList = contentList.get(0).getElementsByTag("table");
    	
    	// check the table elemnent list
    	// check if the table elemetn list is empty.
    	if(tableElementList.isEmpty())
    		throw new IllegalArgumentException("No table class found.");
    	// check if the tableElementList contains more than one "table" element.
    	if(tableElementList.size()>1)
    		throw new IllegalArgumentException("More than one table element are found.");
    	
    	//Go further to tbody
    	Elements tbodyElementList = tableElementList.get(0).getElementsByTag("tbody");
    	
    	// check the tbody element list
    	// check if the tbody element list is empty.
    	if(tbodyElementList.isEmpty())
            throw new IllegalArgumentException ("No table tag element found.");
        
    	if(tbodyElementList.size()>1)
    		throw new IllegalArgumentException("More than one table element are found.");
    	
    	return tbodyElementList.get(0);
	}


	
}

/**
 * This class contains the method parsing the content of the <tbody> element.
 * @author penpen926
 *
 */
class FinanzenWebpageParser{
	/**
	 * This method receives the tbody element, parses the tbody element and returns
	 * the float array of revenue.
	 * Revenue:
	 * @param tbody
	 * @return
	 */
	public float[] parseRevenue(Element tbody){
		//Get the tr element list.
		ArrayList<Element> trElements = tbody.getElementsByTag("tr");
		// Revenue (Umsatzerloese) is in the second row
		Element revenueTr = trElements.get(1);
		
		//Get the list of td
		ArrayList<Element> tdElements = revenueTr.getElementsByTag("td");
		float[] revenue = new float[tdElements.size()-2];
		// Starting from the third element, start parsing the revenue
		for(int i=0;i<revenue.length;i++){
			String revenueText = tdElements.get(i+2).text();
			revenueText = revenueText.replaceAll(",", ".");
			revenue[i] = Float.parseFloat(revenueText);
		}
		return revenue;
	}
	
	/**
	 * This method receives the tbody element, parses the tbody element and returns the
	 * integer array of years;
	 * @param tbody
	 * @return
	 */
	public int[] parseYears(Element tbody){
		// Get the tr element list
		ArrayList<Element> trElements = tbody.getElementsByTag("tr");
		// The years are in the first row.
		Element yearTr = trElements.get(0);
		
		// Get the list of td
		ArrayList<Element> tdElements = yearTr.getElementsByTag("th");
		System.out.println(tdElements.size());
		int[] years = new int[tdElements.size()-2];
		// Starting from the third element, start parsing the years.
		for(int i=0;i<years.length;i++){
			String yearTextString = tdElements.get(i+2).text();
			years[i] = Integer.parseInt(yearTextString);
		}
		
		return years;
	}
	
	/**
	 * Ebit. German word: Umsatzloese
	 * @param tbody
	 * @return
	 */
	public float[] parseEbit(Element tbody){
		// Get the tr element list
		ArrayList<Element> trElements = tbody.getElementsByTag("tr");
		
		// The EBIT (Umsatzerloese) is in the second row.
		Element ebitTr = trElements.get(1);
		
		// Get the list of td
		ArrayList<Element> tdElements = ebitTr.getElementsByTag("td");
		float[] ebit = new float[tdElements.size()-2];
		// Starting from the third element, start parsing the ebits;
		for(int i=0;i<ebit.length;i++){
			String ebitTextString = tdElements.get(i+2).text();
			ebit[i] = Float.parseFloat(ebitTextString);
		}
		return ebit;
	}
	
	public float[] parsePriceArray(Element tbody){
		float[] prices = null;
		return prices;
	}
	
	/**
	 * This method receives the tbody element, parses the tbody element and returns the
	 * integer array of employee numbers;
	 * @param tbody
	 * @return
	 */
	public int[] parseEmployeeNum(Element tbody){
		// Get the tr element list
		ArrayList<Element> trElements = tbody.getElementsByTag("tr");
		// The employ number (Anzahl Mitarbeiter) is in the 8th row
		Element employeeNumTr = trElements.get(7);
		
		//Get the list of td
		ArrayList<Element> tdElements = employeeNumTr.getElementsByTag("td");
		int[] employNum = new int[tdElements.size()-2];
		//Starting from the third element, start parsing the employNum
		for(int i=0;i<employNum.length;i++){
			
		}
		return employNum;
	}
	
	
	


}


