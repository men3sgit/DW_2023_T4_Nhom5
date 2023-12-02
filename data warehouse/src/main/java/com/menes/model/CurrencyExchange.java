package com.menes.model;

public class CurrencyExchange {
    private String code;
    private String name;
    private String cashBuying;
    private String telegraphicBuying;
    private String selling;
    private String time;
    private String date;
    private String bankName;
    private String crawledDate;
    private String crawledTime;

    // Constructors
    public CurrencyExchange() {
    }

    public CurrencyExchange(String code, String name, String cashBuying, String telegraphicBuying, String selling,
                            String time, String date, String bankName, String crawledDate, String crawledTime) {
        this.code = code;
        this.name = name;
        this.cashBuying = cashBuying;
        this.telegraphicBuying = telegraphicBuying;
        this.selling = selling;
        this.time = time;
        this.date = date;
        this.bankName = bankName;
        this.crawledDate = crawledDate;
        this.crawledTime = crawledTime;
    }

    @Override
    public String toString() {
        return "CurrencyExchange{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", cashBuying='" + cashBuying + '\'' +
                ", telegraphicBuying='" + telegraphicBuying + '\'' +
                ", selling='" + selling + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", bankName='" + bankName + '\'' +
                ", crawledDate='" + crawledDate + '\'' +
                ", crawledTime='" + crawledTime + '\'' +
                '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCashBuying() {
        return cashBuying;
    }

    public void setCashBuying(String cashBuying) {
        this.cashBuying = cashBuying;
    }

    public String getTelegraphicBuying() {
        return telegraphicBuying;
    }

    public void setTelegraphicBuying(String telegraphicBuying) {
        this.telegraphicBuying = telegraphicBuying;
    }

    public String getSelling() {
        return selling;
    }

    public void setSelling(String selling) {
        this.selling = selling;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCrawledDate() {
        return crawledDate;
    }

    public void setCrawledDate(String crawledDate) {
        this.crawledDate = crawledDate;
    }

    public String getCrawledTime() {
        return crawledTime;
    }

    public void setCrawledTime(String crawledTime) {
        this.crawledTime = crawledTime;
    }
}
