package org.stockhelper.quantdataextractor;
import structure.Stock;


public interface WebStockExtractor {
	public void parseWebpage(String urlString);
	public float[] getRevenue();
	public float[] getEbit();
	public int[] getEmployeeNum();
	public float[] getPrices();
	public int[] getYears();
}
