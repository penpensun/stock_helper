package org.stockhelper.quantdataextractor.finanzenextractor.test;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Before;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;


import org.stockhelper.quantdataextractor.finanzenextractor.*;

public class NetworkEnvCheckerTest {
	private static Logger logger = Logger.getLogger(NetworkEnvChecker.class);
	NetworkEnvChecker netChecker;
	@Before
	public void init() {
		BasicConfigurator.configure();
		netChecker = new NetworkEnvChecker();
	}
	@Test
	public void testOuterNetwork() {
		logger.debug("Start testOuterNetwork.");
		//System.out.println("Start testOuterNetwork.");
		assertTrue(NetworkEnv.OUTER_NETWORK == netChecker.checkEnv());
		logger.debug("End testOuterNetwork");
	}
}
