package com.obilet.Methods;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class RandomDataGenerator {
    private static final String[] CITIES = {"İstanbul", "Ankara", "İzmir", "Antalya","Ağrı","Barcelona"};
    private static final Random random = new Random();


    public static String generateRandomEmail() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder mail = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            mail.append(chars.charAt(random.nextInt(chars.length())));
        }
        return  mail + "@autotest.obilet.com";
    }

    public static String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    public static String generateRandomOriginCity() {
        return CITIES[random.nextInt(CITIES.length)];
    }

    public static String generateRandomDestinationCity() {
        String origin = generateRandomOriginCity();
        String destination;
        do {
            destination = CITIES[random.nextInt(CITIES.length)];
        } while (destination.equals(origin));
        return destination;
    }

    public static String generateRandomFutureDate(int minDays, int maxDays) {
        LocalDate today = LocalDate.now();
        int randomDays = random.nextInt(maxDays - minDays + 1) + minDays;
        LocalDate futureDate = today.plusDays(randomDays);
        return futureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
} 