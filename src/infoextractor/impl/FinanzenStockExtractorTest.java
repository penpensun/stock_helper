package infoextractor.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.*;

public class FinanzenStockExtractorTest {
	protected static String url = "http://www.finanzen.net/bilanz_guv/Wirecard";
	protected static FinanzenStockExtractor fse = null;
	
	
	@BeforeClass
	public static void preparseWebpage(){
		fse = new FinanzenStockExtractor();
		fse.parseWebpage(url);
	}
	
	@Test
	public void testGetRevenue() {
		System.out.println("Test get revenue");
		float[] revenue = fse.getRevenue();
		System.out.println(revenue);
		for(int i=0;i<revenue.length;i++)
			System.out.print(revenue[i]+"  ");
	}

	@Test
	public void testGetEquity() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetEbit() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetEmployeeNum() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetYearArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPriceArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testParseYearArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStockObj() {
		fail("Not yet implemented");
	}

}
