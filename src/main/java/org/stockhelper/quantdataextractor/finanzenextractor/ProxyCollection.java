package org.stockhelper.quantdataextractor.finanzenextractor;
import java.net.Proxy;
import java.awt.Window.Type;
import java.net.InetSocketAddress;

/**
 * This class is a colleciton of proxies that are commonly used.
 * For now, only the Bayer intranet proxy is in this collection.
 * 
 * @author penpen926
 *
 */
public class ProxyCollection {
	public static Proxy BAYER_PROXY = null;
	static {
		BAYER_PROXY = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.185.190.100", 8080));
	}
	
	
}
