package infoextractor;
import structure.Stock;


public interface WebStockExtractor {
	public void parseWebpage(String url);
	public Stock initStockObj();
	public float[] getRevenue();
	public float[] getEquity();
	public float[] getEbit();
	public float[] getEmployeeNum();
	public float[] getPrices();
	public int[] getYears();
	
	public Stock getStockObj();
	
}
