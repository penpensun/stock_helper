package org.stockhelper.quantdataextractor.finanzenextractor.test;


import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.stockhelper.quantdataextractor.finanzenextractor.FinanzenStockExtractor;
import org.stockhelper.quantdataextractor.finanzenextractor.NetworkEnvChecker;
import org.junit.*;

public class FinanzenStockExtractorTest {
	private static Logger logger = Logger.getLogger(NetworkEnvChecker.class);
	private String url = "http://www.finanzen.net/bilanz_guv/Wirecard";
	private FinanzenStockExtractor fse = null;
	
	private String proxy = null;
	private int port = -1;
	
	private float[] revenueCorr = {228.51f, 271.62f, 324.80f, 394.60f, 481.74f, 601.03f, 771.34f};
	private int[] yearsCorr = {2009, 2010, 2011, 2012, 2013, 2014, 2015};
	private int[] employNumCorr = {501,500,498,674,1025,1750,2300};
	private float[] ebitCorr = {53.49f,66.09f,75.38f, 91.01f, 92.22f, 136.82f, 177.88f};
	
	@Before
	public void preparseWebpage(){
		BasicConfigurator.configure();
		fse = new FinanzenStockExtractor(url, proxy, port);
		fse.parseWebpage(url);
	}
	
	@Test
	public void testGetRevenue() {
		System.out.println("Test get revenue");
		float[] revenue = fse.getRevenue();
		for(int i=0;i<revenue.length;i++)
			System.out.print(revenue[i]+"  ");
		System.out.println();
		for(int i=0;i<revenue.length;i++)
			assertEquals(revenueCorr[i], revenue[i], 0.001f);
	}

	
	@Test
	public void testGetEmployeeNum() {
		System.out.println("Test get employee numbers");
		int[] employNum = fse.getEmployeeNum();
		for(int i=0;i<employNum.length;i++)
			System.out.print(employNum[i]+"  ");
		System.out.println();
		for(int i=0;i<employNum.length;i++)
			assertEquals(employNumCorr[i], employNum[i]);
	}

	@Test
	public void testGetEbit() {
		System.out.println("Test get ebit");
		float[] ebit = fse.getEbit();
		for(int i=0;i<ebit.length;i++)
			System.out.print(ebit[i]+"  ");
		System.out.println();
		for(int i=0;i<ebit.length;i++)
			assertEquals(ebitCorr[i], ebit[i], 0.001f);
	}

	

	public void testGetPrices(){
		
	}

}
