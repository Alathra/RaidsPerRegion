package io.github.alathra.raidsperregion.utility;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomUtil {

    public static @Nullable <T> T getRandomElementInSet(Set<T> set) {
        if (set == null || set.isEmpty()) {
            return null;
        }

        // Generate a random index
        Random random = new Random();
        int randomIndex = random.nextInt(set.size());

        // Iterate through the set to find the random element
        int currentIndex = 0;
        for (T element : set) {
            if (currentIndex == randomIndex) {
                return element;
            }
            currentIndex++;
        }

        // This line is unreachable but added for compiler's satisfaction
        return null;
    }

    public static @Nullable <T> T getRandomElementInList(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        // Generate a random index
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());

        // Return the element at the random index
        return list.get(randomIndex);
    }
}
