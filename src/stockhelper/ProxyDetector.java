package stockhelper;
import java.net.*;
import java.util.*;
import java.io.*;

public class ProxyDetector {
	public static void detectProxy(String args[]) throws Exception{
		System.setProperty("java.net.useSystemProxies", "true");
		System.out.println("detecting proxies");
		List<Proxy> pl = ProxySelector.getDefault().select(new URI("http://www.google.com"));
		for(Proxy p : pl){
			System.out.println(p);
			
			InetSocketAddress addr = (InetSocketAddress)p.address();
			if(addr == null)
				System.out.println("no proxy");
			else
				System.out.println("proxy hostname:  "+addr.getHostName()+"  proxy port:  "+addr.getPort());
		}
	}
	
	public static void connectURL() throws Exception{
		URL url = new URL("http://www.google.com");
		URLConnection urlConnection = url.openConnection();
		InputStream is = urlConnection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line= null;
		while((line = reader.readLine())!= null){
			System.out.println(line);
		}
	}
	
	public static void main(String args[]){
		try{
			//connectURL();
			detectProxy(args);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
