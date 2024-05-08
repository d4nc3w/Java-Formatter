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
}
