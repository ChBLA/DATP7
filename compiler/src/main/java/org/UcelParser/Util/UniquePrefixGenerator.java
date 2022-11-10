package org.UcelParser.Util;

public class UniquePrefixGenerator {
    private static int currentNumber = 0;

    public String getNewPrefix() {
        return generateStringFromInt(currentNumber++);
    }

    public static void resetCounter() {
        currentNumber = 0;
    }

    private static String generateStringFromInt(int num) {
        var builder = new StringBuilder("aaaaaa");
        for (int i = 5; i >= 0 && num > 0 ; i--) {
            char c = (char) ('a' + num % 26);
            builder.setCharAt(i, c);
            num = num / 26;
        }

        return builder.toString();
    }
}
