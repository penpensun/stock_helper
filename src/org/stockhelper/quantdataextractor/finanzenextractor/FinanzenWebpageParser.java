package org.stockhelper.quantdataextractor.finanzenextractor;

import java.util.ArrayList;

import org.jsoup.nodes.Element;

/**
 * This class contains the method parsing the content of the <tbody> element.
 * @author penpen926
 *
 */
public class FinanzenWebpageParser {
	/**
	 * This method receives the tbody element, parses the tbody element and returns
	 * the float array of revenue.
	 * Revenue:
	 * @param tbody
	 * @return
	 */
	public float[] parseRevenue(Element tbody){
		//Get the tr element list.
		ArrayList<Element> trElements = tbody.getElementsByTag("tr");
		// Revenue (Umsatzerloese) is in the second row
		Element revenueTr = trElements.get(1);
		
		//Get the list of td
		ArrayList<Element> tdElements = revenueTr.getElementsByTag("td");
		float[] revenue = new float[tdElements.size()-2];
		// Starting from the third element, start parsing the revenue
		for(int i=0;i<revenue.length;i++){
			String revenueString = tdElements.get(i+2).text();
			revenueString = revenueString.replaceAll("\\.", "");
			revenueString = revenueString.replaceAll(",", "\\.");
			revenue[i] = Float.parseFloat(revenueString);
		}
		return revenue;
	}
	
	/**
	 * This method receives the tbody element, parses the tbody element and returns the
	 * integer array of years;
	 * @param tbody
	 * @return
	 */
	public int[] parseYears(Element tbody){
		// Get the tr element list
		ArrayList<Element> trElements = tbody.getElementsByTag("tr");
		// The years are in the first row.
		Element yearTr = trElements.get(0);
		
		// Get the list of td
		ArrayList<Element> tdElements = yearTr.getElementsByTag("th");
		System.out.println(tdElements.size());
		int[] years = new int[tdElements.size()-2];
		// Starting from the third element, start parsing the years.
		for(int i=0;i<years.length;i++){
			String yearTextString = tdElements.get(i+2).text();
			years[i] = Integer.parseInt(yearTextString);
		}
		
		return years;
	}
	
	/**
	 * Ebit. German word: Operatives Ergebnis
	 * @param tbody
	 * @return
	 */
	public float[] parseEbit(Element tbody){
		// Get the tr element list
		ArrayList<Element> trElements = tbody.getElementsByTag("tr");
		
		// The EBIT (Operative Ergebnis) is in the 6th row.
		Element ebitTr = trElements.get(5);
		
		// Get the list of td
		ArrayList<Element> tdElements = ebitTr.getElementsByTag("td");
		float[] ebit = new float[tdElements.size()-2];
		// Starting from the third element, start parsing the ebits;
		for(int i=0;i<ebit.length;i++){
			String ebitTextString = tdElements.get(i+2).text();
			ebitTextString = ebitTextString.replaceAll("\\.", "");
			ebitTextString = ebitTextString.replaceAll(",", "\\.");
			ebit[i] = Float.parseFloat(ebitTextString);
		}
		return ebit;
	}
	
	public float[] parsePrices(Element tbody){
		float[] prices = null;
		return prices;
	}
	
	/**
	 * This method receives the tbody element, parses the tbody element and returns the
	 * integer array of employee numbers;
	 * @param tbody
	 * @return
	 */
	public int[] parseEmployeeNum(Element tbody){
		// Get the tr element list
		ArrayList<Element> trElements = tbody.getElementsByTag("tr");
		// The employ number (Anzahl Mitarbeiter) is in the 8th row
		Element employeeNumTr = trElements.get(7);
		
		//Get the list of td
		ArrayList<Element> tdElements = employeeNumTr.getElementsByTag("td");
		int[] employNum = new int[tdElements.size()-2];
		//Starting from the third element, start parsing the employNum
		for(int i=0;i<employNum.length;i++){
			String employNumString  = tdElements.get(i+2).text();
			employNumString = employNumString.replaceAll("\\.", "");
			employNum[i] = Integer.parseInt(employNumString);
		}
		return employNum;
	}
}
