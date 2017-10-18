package org.stockhelper.quantdataextractor.finanzenextractor;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class is responsible for pre-parse. 
 * The class FinanzenWebpageExtractor receives the Document object (contains GuV webpage) and returns
 * a list of <tbody>. 
 * 
 * @author penpen926
 *
 */
public class FinanzenWebpageExtractor {
	/**
	 * This method extracts the contentBox in the webpage.
	 * The method receives the root element of the webpage and returns a list of 
	 * <div class = "content"> elements.
	 * @param doc
	 * @return
	 */
	ArrayList<Element> extractContentBoxList(Document doc){
		ArrayList<Element> contentBoxList;
		// Parse the webpage and init the tbodylist
        Element bguvFormElement = doc.getElementById("bguvform");
        Elements mainElementList = bguvFormElement.getElementsByClass("main");
        if(mainElementList.isEmpty())
            throw new IllegalArgumentException("No main class found");
        
        // Get the list of "content tableQuote" elements.
        contentBoxList = mainElementList.get(0).getElementsByClass("contentBox tableQuotes");
        // Check the number of elements in contentTableQuoteElementList
        if(contentBoxList.isEmpty())
            throw new IllegalArgumentException("No content tableQuote class element found.");
       
        return contentBoxList;
	}
	
	/**
	 * This method extracts the tbody element within a contentBox.
	 * The method receives the element of <div class = "content"> and returns the <tbody> within the element.
	 * @param contentBox
	 * @return
	 */
	Element extractTbodyElement(Element contentBox){
		// First get the content element
    	Elements contentList = contentBox.getElementsByClass("content");
    	// Check the content element list.
    	// check if the contentList is empty.
    	if(contentList.isEmpty())
            throw new IllegalArgumentException("No content class element found.");
        // check if the contentList contains more than one "content" element. 
    	if(contentList.size()>1)
    		throw new IllegalArgumentException("More than one content class element are found.");
    	
    	// For the content element, we go further to the table element.
    	Elements tableElementList = contentList.get(0).getElementsByTag("table");
    	
    	// check the table elemnent list
    	// check if the table elemetn list is empty.
    	if(tableElementList.isEmpty())
    		throw new IllegalArgumentException("No table class found.");
    	// check if the tableElementList contains more than one "table" element.
    	if(tableElementList.size()>1)
    		throw new IllegalArgumentException("More than one table element are found.");
    	
    	//Go further to tbody
    	Elements tbodyElementList = tableElementList.get(0).getElementsByTag("tbody");
    	
    	// check the tbody element list
    	// check if the tbody element list is empty.
    	if(tbodyElementList.isEmpty())
            throw new IllegalArgumentException ("No table tag element found.");
        
    	if(tbodyElementList.size()>1)
    		throw new IllegalArgumentException("More than one table element are found.");
    	
    	return tbodyElementList.get(0);
	}
}
