package org.example.loja.util;

public class CPFValidator {

    public static boolean isValidCPF(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", "");

        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }

        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int firstCheckDigit = 11 - (sum % 11);
            if (firstCheckDigit >= 10) firstCheckDigit = 0;

            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int secondCheckDigit = 11 - (sum % 11);
            if (secondCheckDigit >= 10) secondCheckDigit = 0;

            return cpf.charAt(9) == Character.forDigit(firstCheckDigit, 10)
                    && cpf.charAt(10) == Character.forDigit(secondCheckDigit, 10);

        } catch (Exception e) {
            return false;
        }
    }
}
