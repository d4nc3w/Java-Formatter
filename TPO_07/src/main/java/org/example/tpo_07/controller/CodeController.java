package org.example.tpo_07.controller;

import com.google.googlejavaformat.java.FormatterException;
import org.example.tpo_07.model.Code;
import org.example.tpo_07.service.CodeService;
import org.example.tpo_07.service.JavaFormatService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.view.RedirectView;

@Controller
public class CodeController {
    private final CodeService codeService;
    private final JavaFormatService googleFormatService;

    public CodeController(CodeService codeService, JavaFormatService JavaFormatService) {
        this.codeService = codeService;
        this.googleFormatService = JavaFormatService;
    }

    @GetMapping("/newCode")
    public String showForm(Model model) {
        model.addAttribute("code", new Code());
        return "codeFormatter";
    }

    @PostMapping("/newCode")
    public String formatCode(@ModelAttribute Code code, Model model) {
        model.addAttribute("original", code.getCode());
        try {
            //String formatted = javaFormatService.format(code.getCode());
            //codeService.formatCode(code.getCode()););
            Code fCode = new Code();
            fCode.setId(code.getId());
            fCode.setCode(googleFormatService.format(code.getCode()));
            //javaFormatService.printCodes(code);
            model.addAttribute("formatted", fCode);
        } catch(FormatterException e){
            model.addAttribute("errorMsg", e.getMessage());
            return "formatFailed";
        }
        return "codeFormatter";
    }

   /*
    @PostMapping("/newCode")
    public String formatCode(@ModelAttribute Code code, Model model){
        model.addAttribute("original", code.getCode());
        Code fCode = new Code();
        fCode.setId(code.getId());
        fCode.setCode(code.getCode().toUpperCase());
        model.addAttribute("formatted", fCode);
        return "codeFormatter";
    }*/

    @PostMapping("/saveCode")
    public RedirectView saveCode(Code code, @RequestParam long duration, @RequestParam String unit) {
        long convertedDuration = codeService.convertDuration(duration, unit);
        if (codeService.saveCode(code, convertedDuration)) {
            return new RedirectView("/code?id=" + code.getId(), true, false);
        } else {
            return new RedirectView("/invalidID", true, false);
        }
    }

    @GetMapping("/code")
    public String getCode(@RequestParam String id, Model model) {
        codeService.findCodeById(id).ifPresent(code -> model.addAttribute("formatted", code));
            return "code";
    }

    @GetMapping("/invalidID")
    public String invalidID(){
        return "invalidID";
    }
}
