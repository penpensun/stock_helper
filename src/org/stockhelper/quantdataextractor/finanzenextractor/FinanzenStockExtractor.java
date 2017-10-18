package org.stockhelper.quantdataextractor.finanzenextractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import org.jsoup.select.*;
import org.stockhelper.quantdataextractor.QuantDataMiner;
import org.stockhelper.structure.Company;

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
public class FinanzenStockExtractor implements QuantDataMiner{
	Document webStockDoc = null;
	
	
	// the list of "tbody" elements on the webpage.
	ArrayList<Element> tbodyList;
	
	private float[] revenue; // Umsatzloese
	private float[] ebit; // Operatives Ergebnis
	private int[] employeeNum; // Anzahl Mitarbeiter
	private int[] years; // The years
	private float[] prices;

	
	/**
	 * 
	 * @author: Peng Sun
	 * @param url: The url of the website
	 * @return: void
	 */
	public void parseWebpage(String urlString, boolean useProxy){	
		//User HttpURLConnection to connect to the webpage
		URL url = null;
		try{
			url = new URL(urlString);
		}catch(MalformedURLException e){
			e.printStackTrace();
		}
		
		//Init the proxy
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.185.190.100", 8080));
		
		// Get the connection
		HttpURLConnection urlConn = null;
		try{
			urlConn = (HttpURLConnection)url.openConnection(proxy);
		}catch(IOException e){
			e.printStackTrace();
		}
		//Read the webpage into a StingBuilder
		StringBuilder strBuilderWebpage = new StringBuilder();
		
		BufferedReader br = null;
		try{
			br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
		}catch(IOException e){
			e.printStackTrace();
		}
		String line= null;
		try{
			while((line =br.readLine())!= null)
				strBuilderWebpage.append(line).append("\n");
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
		//Parse the webpage by Jsoup
        webStockDoc = Jsoup.parse(strBuilderWebpage.toString());
        
        
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
        // The revenue is in the third tbody
        tbodyElement = tbodyList.get(2);
        revenue = webpageParser.parseRevenue(tbodyElement);
        
        // Parse the years 
        // year can be found in any tbody
        tbodyElement = tbodyList.get(1);
        years = webpageParser.parseYears(tbodyElement);
        
        // Parse the ebit
        // ebit is in the third tbody
        tbodyElement = tbodyList.get(2);
        ebit = webpageParser.parseEbit(tbodyElement);
        
        // Parse the employee number
        // The employee number can be found in the fifth tbody.
        tbodyElement = tbodyList.get(4);
        employeeNum = webpageParser.parseEmployeeNum(tbodyElement);
	}

	/**
	 * This method retrieves the content of the webpage with given url
	 * and returns a string.
	 * First, this method will check whether the current network environment is:
	 * 1. inside Bayer intranetwork, or 
	 * 2. in "outer" internet.
	 * @param url
	 * @return
	 */
	public String retrieveWebpage(String url){
		// Check if in outer internet.
		
	}
	/**
	 * This method parses the webpage content, assigns values to:
	 * revenue,
	 * ebit,
	 * employeeNum,
	 * years,
	 * prices.
	 * 
	 * @param strWebpageContent
	 */
	protected void parseWebpage2(String strWebpageContent){

		//Parse the webpage by Jsoup
        webStockDoc = Jsoup.parse(strWebpageContent);
        
        
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
        // The revenue is in the third tbody
        tbodyElement = tbodyList.get(2);
        revenue = webpageParser.parseRevenue(tbodyElement);
        
        // Parse the years 
        // year can be found in any tbody
        tbodyElement = tbodyList.get(1);
        years = webpageParser.parseYears(tbodyElement);
        
        // Parse the ebit
        // ebit is in the third tbody
        tbodyElement = tbodyList.get(2);
        ebit = webpageParser.parseEbit(tbodyElement);
        
        // Parse the employee number
        // The employee number can be found in the fifth tbody.
        tbodyElement = tbodyList.get(4);
        employeeNum = webpageParser.parseEmployeeNum(tbodyElement);
	}
	
	
	/**
	 * This method returns the revenue
	 */
	public float[] getRevenue() {
		return revenue;
	}

	public float[] getEbit() {
		return ebit;
	}


	public int[] getEmployeeNum() {
		return employeeNum;
	}


	/**
	 * This method parses the stock webpage and outputs the year array.
	 */
	@Deprecated
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


	@Override
	public void parseWebpage(String urlString) {
		// TODO Auto-generated method stub
		
	}

}



