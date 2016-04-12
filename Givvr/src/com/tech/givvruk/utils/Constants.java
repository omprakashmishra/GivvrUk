package com.tech.givvruk.utils;

public final class Constants {
	private Constants() {
	}
	public static final String SERVER_URL = "http://api.teamgivvr.com/register_device.php";
   public static final String GOOGLE_SENDER_ID = "906655716885"; 
   public static final String FLURRY_ID = "9FYCJ9N8D2CWMBN78W9Q"; 
   public static final String TAG = "Mydata";
   //public static final String EmailValidate="[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +"\\@" +"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +"(" +"\\." +"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +")+";
   public static final String DISPLAY_MESSAGE_ACTION = "com.tech.givvr.DISPLAY_MESSAGE";
   public static final String WELCOME_MESSAGE = "These are all the awesome Charity projects you can support. Just click on one you like and start watching videos. Every view triggers a donation to the charity - at no cost to you!";
   public static final String EXTRA_MESSAGE = "message";
	public static final String FACEBOOK_SHARE_MESSAGE = "Make a Difference. Press Play.";
	public static final String FACEBOOK_SHARE_LINK = "https://givvr.com";
	public static final String FACEBOOK_SHARE_LINK_NAME = "The more we givv. The bigger we get.";
	public static final String FACEBOOK_SHARE_LINK_DESCRIPTION = " Watch sponsored videos on your smart phone and automatically generate money for your favorite charity. For every video you watch, the advertiser donates to a charity of your choice. It's the first donation app that doesn't cost you a penny. You can feed people, fight poverty, and rescue animals, all from the convenience of your phone. Join the movement, watch a 30 second sponsored video, and feel good about giving back without the hassle of putting down a credit card.";
	public static final String FACEBOOK_SHARE_PICTURE = "https://fbcdn-photos-g-a.akamaihd.net/hphotos-ak-xap1/t39.2081-0/p128x128/10540980_1682716725286709_1507822338_n.png";
	public static final String FACEBOOK_SHARE_ACTION_NAME = "Givvr";
	public static final String FACEBOOK_SHARE_ACTION_LINK = "https://givvr.com";
	public static final String FACEBOOK_SHARE_IMAGE_CAPTION = "Givvr";
	public static final String FACEBOOK_SHARE_VIDEO_1 = "Hey! I'm raising money for ";
	public static final String FACEBOOK_SHARE_VIDEO_2 = " using the Givvr App. Every time someone watches this video, ";
	public static final String FACEBOOK_SHARE_VIDEO_3 = ". So please do me a favor and watch and share the video! If there's a cause you want to raise money for, download the Givvr app. Every video on there is sponsored by a brand, so you can raise money for free just by watching and sharing videos.";
	
	public static final String EMAIL_TEXT_BODY_1 = "I need to ask you for a favor. It's free, takes 30 seconds, and helps an amazing cause!";
	public static final String EMAIL_TEXT_BODY_2 = "I'm raising money for ";
	public static final String EMAIL_TEXT_BODY_3 = " on GIVVR. Here's how it works: Download the app, choose a project, and watch a 30 second sponsored video. The money from the advertiser goes to support ";
	public static final String EMAIL_TEXT_BODY_4 = " Please join me and download the GIVVR app now. Thanks!";
	public static final String EMAIL_TEXT_BODY_5 = "http://www.givvr.com";
	
	public static final String TW_TEXT_BODY_1 = "Every time you watch this video ";
	public static final String TW_TEXT_BODY_2 = " by watching and sharing this video. Every view raises ";
	public static final String TW_TEXT_BODY_3 = "! Download Givvr to raise for your cause!";
	
	
	public static final class Extra {
		public static final String POST_MESSAGE = "com.tech.givvr.extra.POST_MESSAGE";
		public static final String POST_LINK = "com.tech.givvr.extra.POST_LINK";
		public static final String POST_LINK_NAME = "com.tech.givvr.extra.POST_LINK_NAME";
		public static final String POST_LINK_CAPTION = "com.tech.givvr.extra.POST_LINK_CAPTION";
		public static final String POST_LINK_DESCRIPTION = "com.tech.givvr.extra.POST_LINK_DESCRIPTION";
		public static final String POST_PICTURE_URL = "com.tech.givvr.extra.POST_PICTURE";
		public static final String GOOGLE_LOGIN_USER_NAME = "com.tech.givvr.extra.GOOGLE_LOGIN_USER_NAME";
		public static final String GOOGLE_LOGIN_USER_EMAIL = "com.tech.givvr.extra.GOOGLE_LOGIN_USER_EMAIL";
		public static final String GOOGLE_LOGIN_USER_GENDER = "com.tech.givvr.extra.GOOGLE_LOGIN_USER_GENDER";
		public static final String GOOGLE_LOGIN_USER_PROFILE_PIC = "com.tech.givvr.extra.GOOGLE_LOGIN_USER_PROFILE_PIC";
		
	}
	
	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}

	public static class Action {
		public static final String ACTION_START_ACTIVITY = "com.tech.start.activity";
		public static final String ACTION_SETTINGS_ACTIVITY = "com.tech.settings.activity";
	}
}
