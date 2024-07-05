package com.todaysroom.global.util;

import java.util.Random;

public class Utils {

    public static String convertPrice(String amount){
        StringBuilder result = new StringBuilder();
        int price = Integer.parseInt(amount.replace(",",""));

        int billions = price / 10000;
        int thousands = price % 10000;

        result.append(billions > 0 ? billions + "억" : "");
        result.append(billions > 0 && thousands > 0 ? " " : "");
        result.append(thousands);

        return result.toString();
    }

    public static String getImage(){
        Random random = new Random();
        int max = 30;
        int min = 1;
        int value = random.nextInt(max) + min;

        return "/images/apt-image" + value + ".jpg";
    }
}
