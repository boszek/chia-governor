package pl.thatisit.plotter.systemtask;

import java.util.HashMap;
import java.util.Map;

public class Arguments {
    private final Map<String, String> tokens = new HashMap<>();

    private Arguments(String value) {
        char[] characters = value.toCharArray();

        for (int i = 0; i < characters.length; ) {
            char next = characters[i];
            if (next == '\"') {
                i++;
                var sb = new StringBuilder();
                while (characters[i] != '\"') {
                    sb.append(characters[i]);
                    i++;
                }
                parseToken(sb.toString(), tokens);
                i++;
            } else if (Character.isWhitespace(next)) {
                i++;
            } else if (Character.isAlphabetic(next)) {
                var sb = new StringBuilder();
                while (Character.isAlphabetic(characters[i]) || Character.isDigit(characters[i]) || characters[i] == '/' || characters[i] == '-') {
                    sb.append(characters[i]);
                    i++;
                }
                parseToken(sb.toString(), tokens);
            } else if (characters[i] == '-' && characters[i - 1] == ' ') {
                var sb = new StringBuilder();
                while (i < characters.length && !((characters[i] == ' ' || characters[i] == '\"') && i < characters.length - 1 && characters[i + 1] == '-')) {
                    sb.append(characters[i]);
                    i++;
                }
                parseToken(sb.toString(), tokens);
            } else {
                i++;
            }
        }
    }

    private void parseToken(String value, Map<String, String> tokens) {
        if (value.startsWith("-")) {
            tokens.put(value.substring(1, 2), value.substring(2));
        } else {
            tokens.put(value, value);
        }
    }

    public static Arguments of(String value) {
        return new Arguments(value);
    }

    public String get(String key) {
        return tokens.get(key);
    }

    public boolean contains(String key) {
        return tokens.containsKey(key);
    }
}
