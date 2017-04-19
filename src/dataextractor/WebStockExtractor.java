package dataextractor;
import structure.Stock;


public interface WebStockExtractor {
	public void parseWebpage(String url);
	public Stock initStockObj();
	public float[] getRevenue();
	public float[] getEquity();
	public float[] getEbit();
	public float[] getEmployeeNum();
	public float[] getPriceArray();
	public int[] getYearArray();
	
	public Stock getStockObj();
	
	public void updateCompanyInfo(String id, StockIDEnum idType);
}
