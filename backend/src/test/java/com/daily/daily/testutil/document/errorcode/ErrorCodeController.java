package com.daily.daily.testutil.document.errorcode;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorCodeController {
    @GetMapping("/all-error-code")
    public String forDocumentation() {
        return "";
    }
}
