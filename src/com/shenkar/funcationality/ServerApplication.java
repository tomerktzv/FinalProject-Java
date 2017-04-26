package com.shenkar.funcationality;

import com.shenkar.gui.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ServerApplication implements IExchangeRates, Runnable {
	private Logger _log = Logger.getLogger("ApplicationLogger");
	private static String _XML_Adress;
	private static final String[] _Fields = {"NAME", "CURRENCYCODE", "COUNTRY", "RATE"};
	private DocumentBuilderFactory _docBuilderFactory;
	private DocumentBuilder _docBuilder;
	private Document _doc;
	private File _parsedData;
	private Hashtable<String, Currency> _currency;
	private OutputStream _outputStream;
	private String _date;
	private boolean isReady = false;
	
	public ServerApplication(){
		setRatesTable(new Hashtable<String, Currency>());
		try{
			_docBuilderFactory = DocumentBuilderFactory.newInstance();
			_docBuilder = _docBuilderFactory.newDocumentBuilder();
			_parsedData = new File("ParsedData");
		}
		catch(ParserConfigurationException e){
			_log.debug(e.getStackTrace());
		}

		
	}
	

	@Override
	public String getDate() {
		return _date;
	}


	@Override
	public boolean isFileReady() {
		return this.isReady;
	}
	
	public void setRatesTable(Hashtable<String, Currency> currency){
		this._currency = currency;
	}

	@Override
	public void ParseXML() {
		try{
			_outputStream = new FileOutputStream(_parsedData);
			_doc = _docBuilder.parse(_XML_Adress);
			_doc.getDocumentElement().normalize(); 
		} catch(SAXException e){
			_log.debug("Could not parse data from XML file!!");
			_log.debug(e.getStackTrace());
		} catch(IOException e){
			_log.debug("Could not parse data from XML file!!");
			_log.debug(e.getStackTrace());
		}
		Node date = _doc.getElementsByTagName("LAST_UPDATE").item(0).getChildNodes().item(0);
		_date = date.getTextContent();
		try{
			_outputStream.write(("The last update time is: ").getBytes());
			_outputStream.write(date.getNodeValue().getBytes());
			_outputStream.write(("\n").getBytes());
		} catch(IOException e){
			_log.debug(e.getStackTrace());
		}
		NodeList curr = _doc.getElementsByTagName("CURRENCY");
		for (int i = 0; i < curr.getLength(); i++){
			Node currNode = curr.item(i);
			Element element = (Element) currNode;
			for (String name: _Fields){
				NodeList titleList = element.getElementsByTagName(name);
				Element titleHeader = (Element) titleList.item(0);
				Node titleNode = titleHeader.getChildNodes().item(0);
				try{
					_outputStream.write((titleNode.getNodeValue() + ", ").getBytes());
				} catch(Exception e){
					_log.debug(e.getStackTrace());
				}
			}
			try{
				_outputStream.write(("\n").getBytes());
			} catch(IOException e){
				_log.debug(e.getStackTrace());
			}
		}
		this.isReady = true;
		setCurrencies();
	}
	
	
	private void setCurrencies(){
        /*enter NIS to the currencies map*/
        _currency.put("NIS", new Currency("Shekel", "NIS", "Israel", 1));
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(_parsedData));
            String line;
            br.readLine();
            while((line = br.readLine()) != null){
                String[] values = line.split(",");
                Currency currency = new Currency(values[0], values[1], values[2], Float.parseFloat(values[3]));
                String name = values[1].trim();
                _currency.put(name, currency);
            }
        } catch (FileNotFoundException e) {
            _log.debug(e.getStackTrace());
        } catch (IOException e) {
            _log.debug(e.getStackTrace());
        }
        finally{
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
    }

	@Override
	public void run(){
		try{
			_XML_Adress = "http://www.boi.org.il/currency.xml";
			if (isServerReachable(_XML_Adress))
			{
				if(!isServerReachable("http://www.abelski.com/currencies.xml"))
				{
					_XML_Adress = "http://www.abelski.com/currencies.xml";
				}			
				else throw (new RatesException("Could not reach requested adress!"));
			}
			
		}
		catch(RatesException e){
			System.out.println("Could not reach requested adress!");
			_log.debug(e.getStackTrace());
		}	
		ParseXML();
	}
	public boolean isServerReachable(String XML_Adress) // To check if server is reachable
    {
        try {
            InetAddress.getByName(XML_Adress).isReachable(2000); 
            return true;
        } catch (Exception e) {
            return false;
        }
    }
	
	public Hashtable<String, Currency> getCurrency()
	{
		return _currency;	
	}
	
	public String CalculateRates(String currA, String currB){
		Currency a = _currency.get(currA);
		Currency b = _currency.get(currB);
		DecimalFormat formRates = new DecimalFormat("#.####");
		float aCurrency = a.getRate();
		float bCurrency = b.getRate();
		float result = aCurrency / bCurrency;
		return formRates.format(result);
	}
	
	public float calculateResult(String from, String to, float amount){
		return Float.parseFloat(CalculateRates(from,to))*amount;
	}

	
}



