package com.tech.givvruk.utils;

public class Videos{

	private String VIDEO_URL ;
	private String COMPANY_NAME;
	private String FB_LIKE_URL;
	private String COMPANY_WEBSITE;
	private String VIDEO_ID;
	private String VIDEO_COST ;
	private String THUMNAIL;
	private String VIDEO_FREQ;
	private String MAXIMUM_DONATION;
	private String LOGO_URL;
	private String LIKED;
	
	public Videos(String vIDEO_URL, String cOMPANY_NAME, String fB_LIKE_URL,
			String cOMPANY_WEBSITE, String vIDEO_ID, String vIDEO_COST,String tHUMNAIL,String vIDEO_FREQ,String mAXIMUM_DONATION,String lOGO_URL,String lIKED) {
		super();
		VIDEO_URL = vIDEO_URL;
		COMPANY_NAME = cOMPANY_NAME;
		FB_LIKE_URL = fB_LIKE_URL;
		COMPANY_WEBSITE = cOMPANY_WEBSITE;
		VIDEO_ID = vIDEO_ID;
		VIDEO_COST = vIDEO_COST;
		THUMNAIL = tHUMNAIL;
		VIDEO_FREQ = vIDEO_FREQ;
		MAXIMUM_DONATION = mAXIMUM_DONATION;
		LOGO_URL = lOGO_URL;
		LIKED = lIKED;
	}

	public String getLIKED() {
		return LIKED;
	}

	public void setLIKED(String lIKED) {
		LIKED = lIKED;
	}

	public String getMAXIMUM_DONATION() {
		return MAXIMUM_DONATION;
	}
	public void setMAXIMUM_DONATION(String mAXIMUM_DONATION) {
		MAXIMUM_DONATION = mAXIMUM_DONATION;
	}
	public String getLOGO_URL() {
		return LOGO_URL;
	}
	public void setLOGO_URL(String lOGO_URL) {
		LOGO_URL = lOGO_URL;
	}
	public String getVIDEO_FREQ() {
		return VIDEO_FREQ;
	}
	public void setVIDEO_FREQ(String vIDEO_FREQ) {
		VIDEO_FREQ = vIDEO_FREQ;
	}
	public String getVIDEO_URL() {
		return VIDEO_URL;
	}
	public void setVIDEO_URL(String vIDEO_URL) {
		VIDEO_URL = vIDEO_URL;
	}
	public String getCOMPANY_NAME() {
		return COMPANY_NAME;
	}
	public void setCOMPANY_NAME(String cOMPANY_NAME) {
		COMPANY_NAME = cOMPANY_NAME;
	}
	public String getFB_LIKE_URL() {
		return FB_LIKE_URL;
	}
	public void setFB_LIKE_URL(String fB_LIKE_URL) {
		FB_LIKE_URL = fB_LIKE_URL;
	}
	public String getCOMPANY_WEBSITE() {
		return COMPANY_WEBSITE;
	}
	public void setCOMPANY_WEBSITE(String cOMPANY_WEBSITE) {
		COMPANY_WEBSITE = cOMPANY_WEBSITE;
	}
	public String getVIDEO_ID() {
		return VIDEO_ID;
	}
	public void setVIDEO_ID(String vIDEO_ID) {
		VIDEO_ID = vIDEO_ID;
	}
	public String getVIDEO_COST() {
		return VIDEO_COST;
	}
	public void setVIDEO_COST(String vIDEO_COST) {
		VIDEO_COST = vIDEO_COST;
	}
	public String getTHUMNAIL() {
		return THUMNAIL;
	}
	public void setTHUMNAIL(String tHUMNAIL) {
		THUMNAIL = tHUMNAIL;
	}

}
