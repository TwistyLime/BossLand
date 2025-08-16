package com.twistylime.bossLand.utility;

import java.util.Random;

public class UtilityCalc {

    public static int getIntFromString(String setAmountString) {
        int setAmount = 1;
        if (setAmountString.contains("-")) {
            String[] split = setAmountString.split("-");
            try {
                int minSetAmount = Integer.parseInt(split[0]);
                int maxSetAmount = Integer.parseInt(split[1]);
                setAmount = new Random().nextInt(maxSetAmount - minSetAmount + 1) + minSetAmount;
            } catch (Exception e) {
                System.out.println("getIntFromString: " + e);
            }
        } else {
            setAmount = Integer.parseInt(setAmountString);
        }
        return setAmount;
    }

    public static double rand(double mind, double maxd) {
        int min = (int) (mind * 10.0D);
        int max = (int) (maxd * 10.0D);
        int r = rand(min, max);
        return r / 10.0D;
    }

    public static int rand(int min, int max) {
        return min + (int) (Math.random() * (1 + max - min));
    }
}
