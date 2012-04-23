package com.xebia.fitnesse.restfulservice;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.minidev.json.JSONArray;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XmlResponseParser implements ResponseParser {

	DocumentBuilderFactory documentBuilderFactory;
    private XPathFactory xpathFactory	;
    
	private Document document;

	public XmlResponseParser() {
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true); // never forget this!
		xpathFactory = XPathFactory.newInstance();
	}
	
	// TODO: do same for file.
	@Override
	public void parse(String content) {
		try {
			DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.parse(new ByteArrayInputStream(content.getBytes("UTF-8")));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getValue(String path) {
	    XPath xpath = xpathFactory.newXPath();
	    NodeList nodes;
		try {
			XPathExpression expr = xpath.compile(path);
		    nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}

		switch (nodes.getLength()) {
		case 0:
			return null;
		case 1:
			return nodes.item(0).getNodeValue();
	    default:
		    List<String> vals = new ArrayList<String>();
		    for (int i = 0; i < nodes.getLength(); i++) {
		        vals.add(nodes.item(i).getNodeValue()); 
		    }
		    return JSONArray.toJSONString(vals);
		}
	}

}
