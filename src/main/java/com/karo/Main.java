package com.karo;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Check if a filename was provided as a command-line argument
        if (args.length == 0) {
            System.err.println("Please provide the path to the JSON test case file.");
            System.err.println("Usage: To run, provide the filename like: testcase1.json");
            return;
        }
        String fileName = args[0]; // e.g., "testcase1.json" or "testcase2.json"

        System.out.println("Attempting to solve for file: " + fileName);

        try {
            // 1. Parse JSON and decode points using the SecretFinder class
            Map<String, Object> jsonData = SecretFinder.parseJsonFile(fileName);
            int k = ((Double) ((Map<String, Object>) jsonData.get("keys")).get("k")).intValue();
            List<SecretFinder.Point> points = SecretFinder.decodePoints(jsonData);

            System.out.println("Successfully parsed " + points.size() + " points.");
            System.out.println("Minimum roots required (k): " + k);

            // 2. Find the secret using Lagrange Interpolation from the SecretFinder class
            BigInteger secret = SecretFinder.findSecret(points, k);

            // 3. Print the final result
            System.out.println("\n---------------------------------");
            System.out.println("The calculated secret (c) is: " + secret);
            System.out.println("---------------------------------");

        } catch (Exception e) {
            System.err.println("\nAn error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}