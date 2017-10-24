package org.stockhelper.quantdataextractor;
import org.stockhelper.structure.Company;


public interface FundamentalDataMiner {
	/**
	 * This defines the method which extracts quant data of a company 
	 * for the fundamental analytics.
	 * @param companyID
	 * @return
	 */
	public Company extractCompanyFundamentalData(String companyID);
}
