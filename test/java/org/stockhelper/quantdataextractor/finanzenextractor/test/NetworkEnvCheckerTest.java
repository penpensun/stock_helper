package org.stockhelper.quantdataextractor.finanzenextractor.test;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Before;
import static org.junit.Assert.*;
import org.stockhelper.quantdataextractor.finanzenextractor.*;

public class NetworkEnvCheckerTest {

	NetworkEnvChecker netChecker;
	@BeforeClass
	public void init() {
		netChecker = new NetworkEnvChecker();
	}
	@Test
	public void testOuterNetwork() {
		assertTrue(NetworkEnv.OUTER_NETWORK == netChecker.checkEnv());
		
	}
}
