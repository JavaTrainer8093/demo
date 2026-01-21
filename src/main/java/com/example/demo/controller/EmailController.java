package com.example.demo.controller;


import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-email")
    public String sendEmail(
            @RequestParam String email,
            @RequestParam String appPassword,
            @RequestParam String subject,
            @RequestParam String message,
            @RequestParam("recipients") MultipartFile recipientsFile,
            @RequestParam(value="attachment", required=false) MultipartFile attachmentFile,
            RedirectAttributes redirectAttributes
    ) {
        try {
            emailService.sendEmails(email, appPassword, subject, message, recipientsFile, attachmentFile);
            redirectAttributes.addFlashAttribute("success", "Emails sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error sending emails: " + e.getMessage());
        }
        return "redirect:/?success=true";
    }
}
