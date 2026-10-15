package vn.fpoly.simplain.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.fpoly.simplain.entity.BoSuuTap;
import vn.fpoly.simplain.repository.BoSuuTapRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/admin/bo-suu-tap")
public class BoSuuTapController {

    @Autowired
    private BoSuuTapRepository boSuuTapRepository;

    @GetMapping("/hien-thi")
    public String hienThi(Model model, 
                          @RequestParam(defaultValue = "0") Integer page,
                          @RequestParam(required = false) String ten) {
        if (page < 0) page = 0;
        int size = 5;
        Page<BoSuuTap> boSuuTapPage;
        
        if (ten == null || ten.trim().isEmpty()) {
            boSuuTapPage = boSuuTapRepository.findAll(PageRequest.of(page, size));
        } else {
            boSuuTapPage = boSuuTapRepository.findAllByTenBoSuuTapContainingIgnoreCase(ten, PageRequest.of(page, size));
        }

        model.addAttribute("listBoSuuTap", boSuuTapPage.getContent());
        model.addAttribute("boSuuTap", new BoSuuTap());
        model.addAttribute("page", page);
        model.addAttribute("totalPages", boSuuTapPage.getTotalPages());
        model.addAttribute("ten", ten);
        
        return "admin/bo-suu-tap/hien-thi";
    }

    private void handleFileUpload(BoSuuTap boSuuTap, MultipartFile fileBanner) {
        if (fileBanner != null && !fileBanner.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + fileBanner.getOriginalFilename();
                Path path = Paths.get("uploads/");
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                Files.copy(fileBanner.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                boSuuTap.setHinhBannerUrl("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("boSuuTap") BoSuuTap boSuuTap, BindingResult result, Model model,
                      @RequestParam(defaultValue = "0") Integer page,
                      @RequestParam(value = "fileBanner", required = false) MultipartFile fileBanner) {
        if (result.hasErrors()) {
            int size = 5;
            model.addAttribute("listBoSuuTap", boSuuTapRepository.findAll(PageRequest.of(page, size)).getContent());
            model.addAttribute("page", page);
            model.addAttribute("totalPages", boSuuTapRepository.findAll(PageRequest.of(page, size)).getTotalPages());
            return "admin/bo-suu-tap/hien-thi";
        }
        
        handleFileUpload(boSuuTap, fileBanner);
        boSuuTapRepository.save(boSuuTap);
        return "redirect:/admin/bo-suu-tap/hien-thi";
    }

    @GetMapping("/view")
    public String view(Model model, @RequestParam Long id) {
        model.addAttribute("boSuuTap", boSuuTapRepository.findById(id).orElse(null));
        return "admin/bo-suu-tap/view";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("boSuuTap") BoSuuTap boSuuTap, BindingResult result, Model model,
                         @RequestParam(value = "fileBanner", required = false) MultipartFile fileBanner) {
        if (result.hasErrors()) {
            return "admin/bo-suu-tap/view";
        }
        
        if (fileBanner != null && !fileBanner.isEmpty()) {
            handleFileUpload(boSuuTap, fileBanner);
        } else {
            // Keep the old banner if no new file is uploaded
            BoSuuTap existing = boSuuTapRepository.findById(boSuuTap.getId()).orElse(null);
            if (existing != null) {
                boSuuTap.setHinhBannerUrl(existing.getHinhBannerUrl());
            }
        }
        
        boSuuTapRepository.save(boSuuTap);
        return "redirect:/admin/bo-suu-tap/hien-thi";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam Long id) {
        boSuuTapRepository.deleteById(id);
        return "redirect:/admin/bo-suu-tap/hien-thi";
    }
}
