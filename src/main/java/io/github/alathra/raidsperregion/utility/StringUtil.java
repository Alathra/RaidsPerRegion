package io.github.alathra.raidsperregion.utility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class StringUtil {
    public static int getNumCharsInComponent(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component).length();
    }

    public static String generateRepeatingCharString(int n, char c) {
        // Ensure the input is non-negative
        if (n < 0) {
            throw new IllegalArgumentException("Input must be a non-negative integer.");
        }
        // Generate the string of dashes
        return String.valueOf(c).repeat(n);
    }
}
