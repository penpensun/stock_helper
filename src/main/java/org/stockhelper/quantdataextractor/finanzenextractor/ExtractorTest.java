package org.stockhelper.quantdataextractor.finanzenextractor;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import org.jsoup.select.*;
import org.stockhelper.quantdataextractor.FundamentalDataMiner;
import org.stockhelper.structure.Company;

import java.util.ArrayList;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.InetSocketAddress;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;


/**
 * This class tests the "data miner" of finanzen.net webpage.
 * @author penpen926
 *
 */
public class ExtractorTest {
	private Logger logger = Logger.getLogger(ExtractorTest.class);
	private String revenueStartMarker = "<td class=\"font-bold\">Umsatzerl";
	private String revenueEndMarker = "</tr>";
	
	private String employeeNumStartMarker = "<td class=\"font-bold\">Anzahl Mitarbeiter</td>";
	private String employeeNumEndMarker = "</tr>";
	
	private String yearStartMarker = "<th>Chart</th>";
	private String yearEndMarker = "</tr>";
	
	
	private int[] year;
	private float[] revenue;
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
	public int[] getMatchStartEndIndex(String startMarker, String endMarker, String webContent){
		int matchStart = webContent.indexOf(startMarker);
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
	 * <td class="font-bold">Umsatzerlöse</td>
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
	public float[] parseFloatData(String matchedString){
		String[] split = matchedString.split("</td><td>");
		float[] data = new float[split.length-1];
		String dataString = null;
		for(int i=0;i<data.length;i++){
			dataString = split[i].replace(".", "").replace(",", ".");
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
	public int[] parseIntegerData(String matchedString){
		String[] split = matchedString.split("</td><td>");
		int[] data = new int[split.length-1];
		String dataString = null;
		for(int i=0;i<data.length;i++){
			dataString = split[i].replace(".","");
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
	public int[] parseYear(String matchedString){
		String[] split =  matchedString.split("</th><th>");
		int[] years = new int[split.length-1];
		String dataString = null;
		for(int i=0;i<years.length;i++){
			years[i] = Integer.parseInt(dataString);
			dataString = null;
		}
		return years;
	}
	
	
	public void testRevenueExtractor() throws Exception {
		URL testUrl = new URL("http://www.finanzen.net/bilanz_guv/Wirecard");
		Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("10.185.190.100",8080));
		
		// Connect to a webpage
		InputStreamReader inr = new InputStreamReader(testUrl.openConnection(proxy).getInputStream());
		BufferedReader br = new BufferedReader(inr);
		StringBuilder strBuilder = new StringBuilder();
		String line = null;
		while((line = br.readLine())!= null) 
			strBuilder.append(line);
		
		int matchStart= strBuilder.toString().indexOf(yearStartMarker);
		int matchEnd = strBuilder.toString().substring(matchStart).indexOf(yearEndMarker)+matchStart-5;
		System.out.println("Match start marker: "+matchStart);
		
		System.out.println("Match end marker: "+matchEnd);
		String matchedString = strBuilder.substring(matchStart,matchEnd);
		System.out.println("Extracted string:");
		System.out.println(matchedString);
		
		String[] matchedStringSplit = matchedString.split("</th><th>");
		System.out.println("Data:");
		for(int i=1;i<matchedStringSplit.length;i++)
			System.out.println(matchedStringSplit[i]);
	}
	
	public static void main(String args[]) throws Exception{
		new ExtractorTest().testRevenueExtractor();
	}
}




