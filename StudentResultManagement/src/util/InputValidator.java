package util;

import java.util.Scanner;

/** Safe console input with re-prompting on invalid values. */
public class InputValidator {
    private final Scanner sc;

    public InputValidator(Scanner sc) { this.sc = sc; }

    public String text(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("  Value cannot be empty.");
        }
    }

    public String optional(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    public int intInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int v = Integer.parseInt(sc.nextLine().trim());
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) { }
            System.out.printf("  Enter a whole number between %d and %d.%n", min, max);
        }
    }

    public double doubleInRange(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                double v = Double.parseDouble(sc.nextLine().trim());
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) { }
            System.out.printf("  Enter a number between %.1f and %.1f.%n", min, max);
        }
    }

    public boolean yes(String prompt) {
        return optional(prompt + " (y/n): ").equalsIgnoreCase("y");
    }
}
