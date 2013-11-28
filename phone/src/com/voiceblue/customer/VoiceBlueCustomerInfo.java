package com.voiceblue.customer;

import android.content.Context;

import com.csipsimple.R;

public class VoiceBlueCustomerInfo {

	private String mName	= "unable to fetch";
	private String mCompany	= "--";
	private String mAccountType = "--";
	private String mCountry = "";
	private Double mCredit = 0d;
	private String mCreditCurrency = "";
	private String mExpirationDate = "";
	private String mStatus = "";
	
	public String getName() {
		return mName;
	}
	public VoiceBlueCustomerInfo setName(String mName) {
		this.mName = mName;
		return this;
	}
	public String getCompany() {
		return mCompany;
	}
	public VoiceBlueCustomerInfo setCompany(String mCompany) {
		this.mCompany = mCompany;
		return this;
	}
	public String getAccountType() {
		return mAccountType;
	}
	public VoiceBlueCustomerInfo setAccountType(String mAccountType) {
		this.mAccountType = mAccountType;
		return this;
	}
	public String getCountry() {
		return mCountry;
	}
	public VoiceBlueCustomerInfo setCountry(String country) {
		this.mCountry = country;
		return this;
	}
	public Double getCredit() {
		return mCredit;
	}
	public VoiceBlueCustomerInfo setCredit(Double mCredit) {
		this.mCredit = mCredit;
		return this;
	}
	public String getExpirationDate() {
		return mExpirationDate;
	}
	public VoiceBlueCustomerInfo setExpirationDate(String mExpirationDate) {
		this.mExpirationDate = mExpirationDate;
		return this;
	}
	
	public boolean isPrepaid() {
		return mAccountType.equals("PP");
	}
	
	public int getFlagResource() {
		
		if (mCountry.equals("PY")) 
			return R.drawable.ic_flag_paraguay;
		else if (mCountry.equals("DE")) 
			return R.drawable.ic_flag_germany;
		else if (mCountry.equals("BR"))
			return R.drawable.ic_flag_brazil;
		else if (mCountry.equals("UK"))
			return R.drawable.ic_flag_uk;
		else
			return -1;
	}
	
	public String getCreditCurrency() {
		return mCreditCurrency;
	}
	public VoiceBlueCustomerInfo setCreditCurrency(String creditCurrency) {
		this.mCreditCurrency = creditCurrency;
		return this;
	}
	public String getStatus() {
		return mStatus;
	}
	public VoiceBlueCustomerInfo setStatus(String status) {
		this.mStatus = status;
		return this;
	}
	
	public boolean isActive() {
		return mStatus.toLowerCase().equals("active");
	}

	public String getAccountTypeDescription(Context cxtx) {
		if (mAccountType.equals("PP"))
			return cxtx.getString(R.string.voiceblue_prepaid_account);
		else if (mAccountType.equals("I"))
			return cxtx.getString(R.string.voiceblue_invoice_account);
		else if (mAccountType.equals("DD"))
			return cxtx.getString(R.string.voiceblue_direct_debit_account);
		else
			return "Unknown";
	}
}
