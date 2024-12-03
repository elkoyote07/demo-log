package com.example.demo.service;

import com.example.demo.util.logger.LogMethod;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class ExampleServiceImpl implements ExampleService {

    private static final Map<String, String> TRANSLATIONS = Map.of(
            "en", "Hello",
            "es", "Hola",
            "fr", "Bonjour",
            "de", "Hallo",
            "it", "Ciao"
    );

    private static final String ERROR_MESSAGE = "ERROR: Invalid input";

    @LogMethod
    @Override
    public String getGreeting(String name) {
        if (isInvalidName(name)) {
            return getErrorMessage();
        }

        StringBuilder result = new StringBuilder();

        // Add translations
        appendAllTranslations(result, name);

        // Add Base64 and machine code for the English message
        appendBase64AndMachineCode(result, name);

        return result.toString();
    }

    private boolean isInvalidName(String name) {
        return name == null || name.isBlank();
    }

    private String getErrorMessage() {
        return ERROR_MESSAGE;
    }

    private void appendAllTranslations(StringBuilder result, String name) {
        TRANSLATIONS.forEach((language, greeting) -> {
            String translatedGreeting = formatGreeting(greeting, name);
            appendTranslation(result, language, translatedGreeting);
        });
    }

    private void appendTranslation(StringBuilder result, String language, String greeting) {
        result.append("[").append(language.toUpperCase()).append("] ")
                .append(greeting).append("\n");
    }

    private String formatGreeting(String greeting, String name) {
        return greeting + ", " + name + "!";
    }

    private void appendBase64AndMachineCode(StringBuilder result, String name) {
        String englishGreeting = formatGreeting(TRANSLATIONS.get("en"), name);

        result.append("\n[BASE64] ").append(toBase64(englishGreeting))
                .append("\n[MACHINE CODE] ").append(toMachineCode(englishGreeting));
    }

    private String toBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    private String toMachineCode(String input) {
        StringBuilder machineCode = new StringBuilder();
        for (char c : input.toCharArray()) {
            machineCode.append(Integer.toBinaryString(c)).append(" ");
        }
        return machineCode.toString().trim();
    }
}
