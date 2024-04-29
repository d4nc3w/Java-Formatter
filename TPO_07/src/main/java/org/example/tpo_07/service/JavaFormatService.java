package org.example.tpo_07.service;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import org.springframework.stereotype.Service;

@Service
public class JavaFormatService {
    private Formatter formatter;

    public JavaFormatService(){
        formatter = new Formatter();
    }

    public String format(String code) throws FormatterException {
        return formatter.formatSource(code);
    }

    /*public void printCodes(Code code) {
        System.out.println("Original:");
        System.out.println(code.getCode());

        String formattedCode;
        try {
            formattedCode = format(code.getCode());
        } catch (FormatterException e) {
            System.out.println("There was an error while formatting " + e.getMessage());
            return;
        }
        System.out.println("Formatted: ");
        System.out.println(formattedCode);
    }*/
}
