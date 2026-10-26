package vn.fpoly.simplain.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.fpoly.simplain.entity.DanhMuc;
import vn.fpoly.simplain.repository.DanhMucRepository;

@Controller
@RequestMapping("/admin/danh-muc")
public class DanhMucController {

    @Autowired
    private DanhMucRepository danhMucRepository;

    @GetMapping("/hien-thi")
    public String hienThi(Model model, 
                          @RequestParam(defaultValue = "0") Integer page,
                          @RequestParam(required = false) String ten) {
        if (page < 0) page = 0;
        int size = 5;
        Page<DanhMuc> danhMucPage;
        
        if (ten == null || ten.trim().isEmpty()) {
            danhMucPage = danhMucRepository.findAll(PageRequest.of(page, size));
        } else {
            danhMucPage = danhMucRepository.findAllByTenDanhMucContainingIgnoreCase(ten, PageRequest.of(page, size));
        }

        model.addAttribute("listDanhMuc", danhMucPage.getContent());
        model.addAttribute("listTatCa", danhMucRepository.findAll()); // for parent category dropdown
        model.addAttribute("danhMuc", new DanhMuc());
        model.addAttribute("page", page);
        model.addAttribute("totalPages", danhMucPage.getTotalPages());
        model.addAttribute("ten", ten);
        
        return "admin/danh-muc/hien-thi";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("danhMuc") DanhMuc danhMuc, BindingResult result, Model model,
                      @RequestParam(defaultValue = "0") Integer page) {
        if (result.hasErrors()) {
            int size = 5;
            model.addAttribute("listDanhMuc", danhMucRepository.findAll(PageRequest.of(page, size)).getContent());
            model.addAttribute("listTatCa", danhMucRepository.findAll());
            model.addAttribute("page", page);
            model.addAttribute("totalPages", danhMucRepository.findAll(PageRequest.of(page, size)).getTotalPages());
            return "admin/danh-muc/hien-thi";
        }
        danhMucRepository.save(danhMuc);
        return "redirect:/admin/danh-muc/hien-thi";
    }

    @GetMapping("/view")
    public String view(Model model, @RequestParam Long id) {
        model.addAttribute("danhMuc", danhMucRepository.findById(id).orElse(null));
        model.addAttribute("listTatCa", danhMucRepository.findAll());
        return "admin/danh-muc/view";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("danhMuc") DanhMuc danhMuc, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("listTatCa", danhMucRepository.findAll());
            return "admin/danh-muc/view";
        }
        danhMucRepository.save(danhMuc);
        return "redirect:/admin/danh-muc/hien-thi";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam Long id) {
        danhMucRepository.deleteById(id);
        return "redirect:/admin/danh-muc/hien-thi";
    }
}
