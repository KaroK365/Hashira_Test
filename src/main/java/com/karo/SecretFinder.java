package com.karo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.io.FileNotFoundException;
public class SecretFinder {

    // Record to hold a point (x, y) using BigInteger. It's public by default.
    public record Point(BigInteger x, BigInteger y) {}

    // REMOVED the main method from this file.

    /**
     * Finds the constant term 'c' of the polynomial using Lagrange Interpolation.
     * The constant term is the value of the polynomial at x = 0.
     * CHANGED from private to public
     */
    public static BigInteger findSecret(List<Point> allPoints, int k) {
        // We only need k points to determine the polynomial
        List<Point> points = allPoints.subList(0, k);

        Rational totalSum = new Rational(BigInteger.ZERO, BigInteger.ONE);

        for (int j = 0; j < k; j++) {
            Point currentPoint = points.get(j);
            BigInteger y_j = currentPoint.y();

            // Calculate the j-th Lagrange basis polynomial at x=0, L_j(0)
            Rational basisPolynomial = new Rational(BigInteger.ONE, BigInteger.ONE);
            for (int i = 0; i < k; i++) {
                if (i == j) continue;

                BigInteger x_i = points.get(i).x();
                BigInteger x_j = currentPoint.x();

                BigInteger numerator = x_i.negate();
                BigInteger denominator = x_j.subtract(x_i);

                basisPolynomial = basisPolynomial.multiply(new Rational(numerator, denominator));
            }

            Rational term = basisPolynomial.multiply(new Rational(y_j, BigInteger.ONE));
            totalSum = totalSum.add(term);
        }

        if (!totalSum.getDenominator().equals(BigInteger.ONE)) {
            throw new IllegalStateException("The final result is not an integer: " + totalSum);
        }

        return totalSum.getNumerator();
    }

    /**
     * Parses the input JSON file into a Map.
     * CHANGED from private to public
     */
    public static Map<String, Object> parseJsonFile(String fileName) throws Exception {
        Gson gson = new Gson();

        // Get the file as a resource stream from the classpath
        InputStream inputStream = SecretFinder.class.getClassLoader().getResourceAsStream(fileName);

        // Check that the file was actually found
        if (inputStream == null) {
            throw new FileNotFoundException("Resource file not found in classpath: " + fileName);
        }

        // Use a try-with-resources block to ensure the reader is closed
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            return gson.fromJson(reader, type);
        }
    }

    /**
     * Decodes the points from the parsed JSON map.
     * CHANGED from private to public
     */
    public static List<Point> decodePoints(Map<String, Object> data) {
        List<Point> points = new ArrayList<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getKey().equals("keys")) continue;

            BigInteger x = new BigInteger(entry.getKey());
            Map<String, String> valueMap = (Map<String, String>) entry.getValue();
            int base = Integer.parseInt(valueMap.get("base"));
            String encodedValue = valueMap.get("value");
            BigInteger y = new BigInteger(encodedValue, base);

            points.add(new Point(x, y));
        }
        return points;
    }
}