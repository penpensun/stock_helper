package org.stockhelper.quantdataextractor.finanzenextractor;

/**
 * The network environment decides whether a proxy is required.
 * If it is BAYER_NETWORK, a proxy will be needed. 
 * @author Peng 
 *
 */
public enum NetworkEnv {
	OUTER_NETWORK,
	BAYER_NETWORK,
	NO_CONNECTION
}
