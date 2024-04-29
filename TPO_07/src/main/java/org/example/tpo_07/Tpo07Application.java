package org.example.tpo_07;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Tpo07Application {

    public static void main(String[] args) {
        SpringApplication.run(Tpo07Application.class, args);

    /*        String sourceCode = "public class CodeController {\n" +
                "\n" +
                "}\n";

        try {
            String formattedSource = new Formatter().formatSource(sourceCode);
            System.out.println(formattedSource);
        } catch(FormatterException e) {
            //...
        }*/
    }

}
