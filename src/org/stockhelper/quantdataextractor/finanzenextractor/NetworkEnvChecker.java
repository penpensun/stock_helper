package org.stockhelper.quantdataextractor.finanzenextractor;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import org.apache.log4j.Logger;
import java.io.IOException;
/**
 * This class implements the network environment checker.
 * This checker will check the current network environment, 
 * whether it is a Bayer intranetwork, or it is a normal "outer network".
 * @author Peng Sun
 *
 */
public class NetworkEnvChecker {
	private static Logger logger = Logger.getLogger(NetworkEnvChecker.class);
	
	//Use baidu to check the network environment.
	private String targetUrlStr = "http://www.baidu.com";
	public NetworkEnv checkEnv(){
		URL targetUrl = null;
		// This bool indicates whether target url could be connected without proxy.
		boolean canConnNoProxy;
		
		// this bool indicates whether target url could be connected with proxy.
		boolean canConnWithProxy;
		
		
		try{
			targetUrl = new URL(targetUrlStr);
		}catch(MalformedURLException e){
			// If the target url is illegal, then error message is logged and null is returned.
			logger.error("Network environment checking target url invalid:  "+targetUrlStr);
			return null;
		}
		
		
		// Try to open the connection 
		HttpURLConnection urlConn = null;
		try{
			urlConn = (HttpURLConnection)targetUrl.openConnection();
		}catch(IOException e){
			logger.debug("Cannot connect with target url without proxy.");
			canConnNoProxy = false;
		}
		
		try{
			urlConn = (HttpURLConnection)targetUrl.openConnection();
		}catch(IOException e){
			logger.debug("Cannot connect with target url with proxy");
		}
		
		// placeholder, should be deleted.
		return null;
	}
}
