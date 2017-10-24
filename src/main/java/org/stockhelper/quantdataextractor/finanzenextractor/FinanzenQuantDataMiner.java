package org.stockhelper.quantdataextractor.finanzenextractor;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import org.jsoup.select.*;
import org.stockhelper.quantdataextractor.FundamentalDataMiner;
import org.stockhelper.structure.Company;
import java.time.YearMonth;

import java.util.ArrayList;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.InetSocketAddress;

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
public class FinanzenQuantDataMiner implements FundamentalDataMiner{
	private static Logger logger = Logger.getLogger(FinanzenQuantDataMiner.class);
	private static final String GUV_WEB_PREFIX = "http://www.finanzen.net/bilanz_guv/";
	
	
	private static final String REVENUE_START_MARKER = "<td class=\"font-bold\">Umsatzerl";
	private static final String REVENUE_END_MARKER = "</tr>";
	
	// The start and end markers for "anzahln Mitarbeiter"
	private static final String EMPLOYEE_NUM_START_MARKER = "<td class=\"font-bold\">Anzahl Mitarbeiter</td>";
	private static final String EMPLOYEE_NUM_END_MARKER = "</tr>";
	
	// The start and end markers for "operatives Ergebnis"
	private static final String EBIT_START_MARKER = "<td class=\"font-bold\">Operatives Ergebnis</td>";
	private static final String EBIT_END_MARKER="</tr>";
	
	// The start and end markers for "bilanzsumme"
	private static final String TOTAL_ASSET_START_MARKER = "<td class=\"font-bold\">Bilanzsumme</td>";
	private static final String TOTAL_ASSET_END_MARKER = "</tr>";
	
	// The start and end markers for years
	private static final String YEAR_START_MARKER = "<th>Chart</th>";
	private static final String YEAR_END_MARKER = "</tr>";

	
	
	/**
	 * Implements the method in QuantDataMiner
	 * 
	 * Given a company id, this method extracts fundamental analytic data from
	 * www.finanzen.net.
	 * The fundamental analytic data includes:
	 * 1. revenue
	 * 2. kgv
	 * 3. ebit
	 * 4. employeeNum
	 * 5. totalAsset
	 * 
	 * and the corresponding years.
	 * 
	 * @param companyID
	 * @return
	 */
	public Company extractCompanyFundamentalData(String companyID){
		Company companyObj = null;
		
		
		
		return companyObj;
	}
	
	
	/**
	 * This method returns the content of the webpage of the given url, with proxy (when necessary)
	 * The returned content is in the form of String.
	 * @param url
	 * @param proxy
	 * @return
	 */
	protected String getWebpageContent(String url, Proxy proxy) {
		
		URL urlObj = null;
		try {
			urlObj = new URL(url);
		}catch(MalformedURLException e){
			logger.error("Illegal url.");
		}
		InputStreamReader inr = null;
		BufferedReader br = null;
		try{
			if(proxy!= null)
				inr = new InputStreamReader(urlObj.openConnection(proxy).getInputStream());
			else
				inr = new InputStreamReader(urlObj.openConnection().getInputStream());
			
			br = new BufferedReader(inr);
		}catch(IOException e){
			logger.error("Webpage open error.");
		}
		
		String line = null;
		StringBuilder webpageContentBuilder = new StringBuilder();
		try{
			while((line = br.readLine())!=null){
				webpageContentBuilder.append(line).append("\n");
			}	
		}catch(IOException e){
			logger.error("Webpage reading error.");
		}
		return webpageContentBuilder.toString();
	}
	
	/**
	 * This method returns the:
	 * match start: the start of the "data matching"
	 * match end: the end of the "data matching" 
	 * of a certain "data matching marker".
	 * 
	 * For example, to get the revenue (umsatzerloese):
	 * data matching marker: <td class=\"font-bold\">Umsatzerl
	 * data start marker: <td class=\"font-bold\">Umsatzerl
	 * data end marker: </tr>
	 * 
	 * 
	 * @param startMarker
	 * @param endMarker
	 * @param webContent
	 * @return
	 */
	protected int[] getMatchStartEndIndex(String startMarker, String endMarker, String webContent){
		int matchStart = webContent.indexOf(startMarker);
		logger.debug("matchstart:");
		//logger.debug(matchStart);
		//logger.debug(startMarker);
		int matchEnd = webContent.substring(matchStart).indexOf(endMarker)+matchStart-5;
		int[] matchStartEndIndex = new int[2];
		matchStartEndIndex[0] = matchStart;
		matchStartEndIndex[1] = matchEnd;
		
		return matchStartEndIndex;
	}
	
	
	/**
	 * This method parses the float data from the matched string.
	 * For example:
	 * the matched String is: 
	 * <td class="font-bold">Umsatzerl�se</td>
	 * <td>271,62</td>
	 * <td>324,80</td>
	 * <td>394,60</td>
	 * <td>481,74</td>
	 * <td>601,03</td>
	 * <td>771,34</td>
	 * <td>1.028,36
	 * 
	 * The returned value will be:
	 * {271.62, 324.80, 394.60, 481.74, 601.03, 771.34, 1028.36}
	 * @return
	 */
	protected float[] parseFloatData(String matchedString){
		String[] split = matchedString.split("</td><td>");
		float[] data = new float[split.length-1];
		String dataString = null;
		for(int i=0;i<data.length;i++){
			dataString = split[i+1].replace(".", "").replace(",", ".");
			data[i] = Float.parseFloat(dataString);
			dataString = null;
		}
		return data;
	}
	
	
	/**
	 * This method parses the integer data from the matched string.
	 * For example:
	 * <td class="font-bold">Anzahl Mitarbeiter</td>
	 * <td>500</td>
	 * <td>498</td>
	 * <td>674</td>
	 * <td>1.025</td>
	 * <td>1.750</td>
	 * <td>2.300</td>
	 * <td>3.766
	 * 
	 * The returned values will be:
	 * {500, 498, 674, 1025, 1750, 2300, 3766}
	 * @return
	 */
	protected int[] parseIntegerData(String matchedString){
		String[] split = matchedString.split("</td><td>");
		int[] data = new int[split.length-1];
		String dataString = null;
		for(int i=0;i<data.length;i++){
			dataString = split[i+1].replace(".","");
			data[i] = Integer.parseInt(dataString);
			dataString= null;
		}
		return data;
	}
	
	/**
	 * This method parses the years from the matched String.
	 * for example:
	 * <th>Chart</th>
	 * <th></th>
	 * <th>2010</th>
	 * <th>2011</th>
	 * <th>2012</th>
	 * <th>2013</th>
	 * <th>2014</th>
	 * <th>2015</th>
	 * <th>2016
	 * 
	 * The returned values will be:
	 * {2010,2011, 2012, 2013, 2014, 2015, 2016}
	 * 
	 * @param matchedString
	 * @return
	 */
	protected int[] parseYear(String matchedString){
		String[] split =  matchedString.split("</th><th>");
		int[] years = new int[split.length-1];
		for(int i=0;i<years.length;i++)
			years[i] = Integer.parseInt(split[i+1]);
		
		return years;
	}

	
	/**
	 * This method extracts the revenue from finanzen.net webpage, given the parameter of web content
	 * in the form of string.
	 * @param webcontentString: the whole web content in form of String.
	 * @return the float array of revenues.
	 */
	public float[] extractRevenue(String webcontentString){
		int[] revenueMatchStartEnd = getMatchStartEndIndex(REVENUE_START_MARKER, REVENUE_END_MARKER,
				webcontentString);
		return parseFloatData(webcontentString.substring(revenueMatchStartEnd[0], revenueMatchStartEnd[1]));
	}
	
	/**
	 * This method extracts the total asset from finanzen.net webpage, given the parameter of web content
	 * in the form of string.
	 * @param webcontentString: the whole web content in form of String.
	 * @return the float array of total assets.
	 */
	public float[] extractTotalAsset(String webcontentString){
		int[] totalAssetMatchStartEnd = getMatchStartEndIndex(TOTAL_ASSET_START_MARKER, TOTAL_ASSET_END_MARKER,
				webcontentString);
		return parseFloatData(webcontentString.substring(totalAssetMatchStartEnd[0], totalAssetMatchStartEnd[1]));
				
	}
	
	/**
	 * This method extracts the ebit from finanzen.net webpage, given the parameter of web content
	 * in the form of string.
	 * @param webcontentString: the whole web content in form of String.
	 * @return the float array of ebits.
	 */
	public float[] extractEbit(String webcontentString){
		int[] ebitMatchStartEnd = getMatchStartEndIndex(EBIT_START_MARKER, EBIT_END_MARKER,
				webcontentString);
		
		logger.debug("substring of web content string:");
		logger.debug(webcontentString.substring(ebitMatchStartEnd[0], ebitMatchStartEnd[1]));
		return parseFloatData(webcontentString.substring(ebitMatchStartEnd[0], ebitMatchStartEnd[1]));
	}
	
	
	/**
	 * This method extracts the employee number from finanzen.net webpage, given the parameter of web content
	 * in the form of string.
	 * @param webcontentString: the whole web content in form of String.
	 * @return the int array of employee number.
	 */
	public int[] extractEmployeeNum(String webcontentString){
		int[] employeeNumMatchStartEnd = getMatchStartEndIndex(EMPLOYEE_NUM_START_MARKER, EMPLOYEE_NUM_END_MARKER,
				webcontentString);
		return parseIntegerData(webcontentString.substring(employeeNumMatchStartEnd[0], employeeNumMatchStartEnd[1]));
	}
	
	
	/**
	 * This method extracts years from finanzen.net webpage, given the parameter of web content
	 * in the form of string.
	 * @param webcontentString: the whole web content in form of String.
	 * @return
	 */
	public YearMonth[] parseYearMonth(String webcontentString){
		int[] yearMatchStartEnd = getMatchStartEndIndex(YEAR_START_MARKER, YEAR_END_MARKER,
				webcontentString);
		
		int[] years = parseYear(webcontentString.substring(yearMatchStartEnd[0], yearMatchStartEnd[1]));
		
		YearMonth[] toReturn = new YearMonth[years.length];
		
		for(int i=0;i<toReturn.length;i++) {
			toReturn[i] = YearMonth.of(years[i], 12);
		}
		
		return toReturn;
	}
	
	
	
	Document webStockDoc = null;
	FinanzenWebpageExtractor webpageExtractor = null;
	FinanzenWebpageParser webpageParser = null;
	
	
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
	public void parseWebpage(String urlString, Proxy proxy){	
		//User HttpURLConnection to connect to the webpage
		URL url = null;
		try{
			url = new URL(urlString);
		}catch(MalformedURLException e){
			e.printStackTrace();
		}
		
		
		// Get the connection
		HttpURLConnection urlConn = null;
		try{
			// if proxy is not null, then the connection is achieved through proxy
			if(proxy != null)
				urlConn = (HttpURLConnection)url.openConnection(proxy);
			else
				urlConn = (HttpURLConnection)url.openConnection();
			
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
        System.out.println(webStockDoc.toString());
        
        
        //Read all "contentBox" elements
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
		String strWebContent = null;
		return strWebContent;
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


	/**
	 * for testing
	 * @param args
	 */
	public static void main(String args[]) {
		BasicConfigurator.configure();
		String companyID = "wirecard";
		
		Proxy proxy = null;
		//proxy = ProxyCollection.BAYER_PROXY;
		
		FinanzenQuantDataMiner dataMiner = new FinanzenQuantDataMiner();
		String webcontentString = dataMiner.getWebpageContent(
				new StringBuilder(GUV_WEB_PREFIX).append(companyID).toString(), proxy);
		float[] ebit = new FinanzenQuantDataMiner().extractEbit(webcontentString);
		
		logger.debug("For test: output the ebit");
		
		for(int i=0;i<ebit.length;i++)
			logger.debug(ebit[i]);
		
		float[] revenue = new FinanzenQuantDataMiner().extractRevenue(webcontentString);
				
		logger.debug("For test: output the revenue");
		
		for(int i=0;i<revenue.length;i++)
			logger.debug(revenue[i]);
		
		
		int[] employeeNum = new FinanzenQuantDataMiner().extractEmployeeNum(webcontentString);
		logger.debug("For test: output the employee number.");
		
		for(int i=0;i<employeeNum.length;i++){
			logger.debug(employeeNum[i]);
		}
	}
}





