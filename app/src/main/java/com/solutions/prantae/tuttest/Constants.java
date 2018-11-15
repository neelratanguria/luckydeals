package com.solutions.prantae.tuttest;

import com.android.volley.toolbox.StringRequest;

import java.util.regex.Pattern;

public class Constants {
    public static String signUpUrl = "http://www.foodsingh.com/app_files/offer/reg.php";
    public static String signInUrl = "http://www.foodsingh.com/app_files/offer/login.php";
    public static String subscriptionCheckUrl = "http://www.foodsingh.com/app_files/offer/sub_chk.php";
    public static final String SESSION_URL = "http://www.foodsingh.com/app_files/offer/login_token.php";
    public static final String DEALS_URL = "http://www.foodsingh.com/app_files/offer/get_deals.php";

    public static final String PACKAGE_NAME = "com.foodsingh.luckytest";

    public static final Pattern EMAIL_REGEX = Pattern
            .compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static  final Pattern NAME_REGEX = Pattern
            .compile("^[\\\\p{L} .'-]+$");
}
