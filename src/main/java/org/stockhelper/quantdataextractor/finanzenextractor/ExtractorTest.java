package org.stockhelper.quantdataextractor.finanzenextractor;
import org.apache.log4j.Logger;
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
	
	protected String getWebpageContent(String url, Proxy proxy) {
		String webpageContent;
		URL urlObj = null;
		try {
			urlObj = new URl(url);
		}
		return webpageContent;
	}
	
	/**
	 * Test if jsoup could recursively search for a tag.
	 */
	public void testRevenueExtractor() throws Exception {
		URL testUrl = new URL("http://www.finanzen.net/bilanz_guv/Wirecard");
		// Connect to a webpage
		InputStreamReader inr = new InputStreamReader(testUrl.openConnection().getInputStream());
		BufferedReader br = new BufferedReader(inr);
		StringBuilder strBuilder = new StringBuilder();
		String line = null;
		while((line = br.readLine())!= null) 
			strBuilder.append(line);
		
		int matchStart= strBuilder.toString().indexOf(revenueStartMarker);
		int matchEnd = strBuilder.toString().substring(matchStart).indexOf(revenueEndMarker)+matchStart-5;
		System.out.println("Match start marker: "+matchStart);
		
		System.out.println("Match end marker: "+matchEnd);
		String matchedString = strBuilder.substring(matchStart,matchEnd);
		System.out.println("Extracted string:");
		System.out.println(matchedString);
		
		String[] matchedStringSplit = matchedString.split("</td><td>");
		System.out.println("Data:");
		for(int i=1;i<matchedStringSplit.length;i++)
			System.out.println(matchedStringSplit[i]);
	}
	
	public static void main(String args[]) throws Exception{
		new ExtractorTest().testRevenueExtractor();
	}
}




