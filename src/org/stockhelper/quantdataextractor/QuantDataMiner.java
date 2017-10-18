package org.stockhelper.quantdataextractor;
import org.stockhelper.structure.Company;


public interface QuantDataMiner {
	public void parseWebpage(String urlString);
	public float[] getRevenue();
	public float[] getEbit();
	public int[] getEmployeeNum();
}
