package com.shenkar.funcationality;

import java.util.Hashtable;

public interface IExchangeRates {
    public String getDate();
    public String CalculateRates(String currA, String currB);
    public boolean isFileReady();
    public void ParseXML();
    public Hashtable<String, Currency> getCurrency();
    public float calculateResult(String from, String to, float amount) throws RatesException;
}