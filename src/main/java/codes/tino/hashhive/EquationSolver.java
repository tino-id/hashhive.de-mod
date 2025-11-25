package codes.tino.hashhive;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EquationSolver {
    private static final Pattern EQUATION_PATTERN = Pattern.compile("Equation:\\s*([\\d\\s+\\-*]+)");

    /**
     * Extracts and solves a mathematical equation from a chat message
     * @param message The chat message containing the equation
     * @return The solution as a string, or null if no valid equation found
     */
    public static String solveFromMessage(String message) {
        Matcher matcher = EQUATION_PATTERN.matcher(message);
        if (matcher.find()) {
            String equation = matcher.group(1).trim();
            try {
                int result = evaluateExpression(equation);
                return String.valueOf(result);
            } catch (Exception e) {
                HashHiveLogger.LOGGER.error("Failed to solve equation: " + equation, e);
                return null;
            }
        }
        return null;
    }

    /**
     * Evaluates a simple mathematical expression with +, -, * operators
     * Follows standard operator precedence (* before + and -)
     */
    private static int evaluateExpression(String expression) {
        expression = expression.replaceAll("\\s+", "");

        // First, handle multiplication
        while (expression.contains("*")) {
            Pattern multPattern = Pattern.compile("(\\d+)\\*(\\d+)");
            Matcher matcher = multPattern.matcher(expression);
            if (matcher.find()) {
                int a = Integer.parseInt(matcher.group(1));
                int b = Integer.parseInt(matcher.group(2));
                int result = a * b;
                expression = expression.substring(0, matcher.start()) + result + expression.substring(matcher.end());
            }
        }

        // Then handle addition and subtraction from left to right
        int result = 0;
        int currentNumber = 0;
        char operation = '+';

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c)) {
                currentNumber = currentNumber * 10 + (c - '0');
            }

            if (c == '+' || c == '-' || i == expression.length() - 1) {
                if (operation == '+') {
                    result += currentNumber;
                } else if (operation == '-') {
                    result -= currentNumber;
                }

                if (i < expression.length() - 1) {
                    operation = c;
                    currentNumber = 0;
                }
            }
        }

        return result;
    }
}
