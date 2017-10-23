package org.stockhelper.quantdataextractor.finanzenextractor;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import javax.net.ssl.HttpsURLConnection;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.net.SocketTimeoutException;
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
	private String targetUrlStr = "https://www.baidu.com";
	public NetworkEnv checkEnv(){
		URL targetUrl = null;
		// This bool indicates whether target url could be connected without proxy.
		boolean canConnNoProxy = false;
		
		// this bool indicates whether target url could be connected with proxy.
		boolean canConnWithProxy = false;
		
		//Assign the targetUrl for conneciton checking.
		try{
			targetUrl = new URL(targetUrlStr);
		}catch(MalformedURLException e){
			// If the target url is illegal, then error message is logged and null is returned.
			logger.error("Network environment checking target url invalid:  "+targetUrlStr);
			return NetworkEnv.ILLEGAL_TARGET_URL;
		}
		//logger.debug("No ILLEGAL_TARGET_URL is returned.");
		
		// Try to open the connection without proxy
		HttpsURLConnection urlConn = null;
		try{
			urlConn = (HttpsURLConnection)targetUrl.openConnection();
		}catch(UnknownHostException e){
			logger.debug("Cannot connect with target url without proxy.");
			canConnNoProxy = false;
		}
		/*
		 * If IOException is thrown out, then something is wrong
		 */
		catch(IOException e) {
			logger.error("IOException popped up.");
			return null;
		}
		
		// Here the code might be more efficient, only one way of checking connection is
		// enough.
		//Check if urlConn is null.
		
		//Test response code for no proxy
		try {	
			if(urlConn.getResponseCode() == HttpsURLConnection.HTTP_OK) 
				canConnNoProxy = true;
			
		}catch(IOException e) {
			logger.error("IOException thrown in getResponseCode()");
		}
		try{
			logger.debug("no proxy response code: "+urlConn.getResponseCode());
		}catch(IOException e) {e.printStackTrace();}
		
		
		//Try to open the connection with proxy.
		try{
			urlConn = (HttpsURLConnection)targetUrl.openConnection(ProxyCollection.BAYER_PROXY);
			urlConn.setConnectTimeout(5000);
			urlConn.setReadTimeout(5000);
		// If cannot connect with target url with proxy, then with proxy is set to be false.
		}catch(UnknownHostException e) {
			logger.debug("Cannot connect with target url with proxy.");
			canConnWithProxy = false;
		}
		catch(IOException e){
			logger.debug("IOException popped up.");
		}
		
		//Test the response code for the connection with proxy.
		
		try{
			logger.debug("with proxy reponse code: "+ urlConn.getResponseCode());
			
		}catch(IOException e) {}
		
		
		if(canConnWithProxy && !canConnNoProxy)
			return NetworkEnv.BAYER_NETWORK;
		
		if(!canConnWithProxy && !canConnNoProxy)
			return NetworkEnv.OUTER_NETWORK;
		
		if(!canConnWithProxy && !canConnNoProxy)
			return NetworkEnv.NO_CONNECTION;
		
		else return null;
	}
}

