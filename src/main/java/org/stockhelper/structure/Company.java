package org.stockhelper.structure;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.time.YearMonth;

/**
 * 
 * @author Peng Sun
 *
 */
public class Company {
	private List<Map.Entry<YearMonth,Float>> revenue;
	private List<Map.Entry<YearMonth, Float>> kgv;
	private List<Map.Entry<YearMonth, Float>> ebit;
	private List<Map.Entry<YearMonth, Integer>> employeeNum;
	private List<Map.Entry<YearMonth, Float>> totalAsset;
	
	public List<Map.Entry<YearMonth,Float>> getAllRevenue(){
		return revenue;
	}
	
	public List<Map.Entry<YearMonth,Float>> getAllKgv(){
		return kgv;
	}
	
	public List<Map.Entry<YearMonth, Float>> getAllEbit(){
		return ebit;
	}
	
	public List<Map.Entry<YearMonth, Integer>> getAllEmployeeNum(){
		return employeeNum;
	}
	
	public List<Map.Entry<YearMonth, Float>> getAllTotalAsset(){
		return totalAsset;
	}
}
