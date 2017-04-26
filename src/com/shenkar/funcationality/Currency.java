package com.shenkar.funcationality;

public class Currency {
    private String name;
    private String currencyCode;
    private String country;
    private float rate;

    Currency(String name, String currencyCode, String country, float rate){
        this.setName(name);
        this.setCountry(country);
        this.setCurrencyCode(currencyCode);
        this.setRate(rate);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}