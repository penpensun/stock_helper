package org.stockhelper.quantdataextractor.finanzenextractor.test;


import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.stockhelper.quantdataextractor.finanzenextractor.FinanzenQuantDataMiner;
import org.stockhelper.quantdataextractor.finanzenextractor.NetworkEnvChecker;
import org.junit.*;

/**
 * This is the test class for FinanzenQuantDataMiner
 * @author Peng Sun
 *
 */
public class FinanzenQuantDataMinerTest {
	
	private float[] revenueCorr = {228.51f, 271.62f, 324.80f, 394.60f, 481.74f, 601.03f, 771.34f};
	private int[] yearsCorr = {2009, 2010, 2011, 2012, 2013, 2014, 2015};
	private int[] employNumCorr = {501,500,498,674,1025,1750,2300};
	private float[] ebitCorr = {53.49f,66.09f,75.38f, 91.01f, 92.22f, 136.82f, 177.88f};
	private float[] totalAssetCorr = {550f, 707f, 1128f, 1431f, 2004f, 2948f, 3495f};
	
	private String companyID ="wirecard";

	/**
	 * Test for 
	 */
	public 
}
