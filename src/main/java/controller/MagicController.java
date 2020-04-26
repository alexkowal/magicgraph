package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import starter.MagicPermutationFinder;

@Controller
public class MagicController {
    private MagicPermutationFinder magicPermutationFinder;

    @Autowired
    public MagicController(MagicPermutationFinder magicPermutationFinder) {
        this.magicPermutationFinder = magicPermutationFinder;
    }

    @PostMapping("/start")
    public void discover(@RequestParam(value = "file") MultipartFile file) throws Exception {
        magicPermutationFinder.readGraphsFromFile(file.getInputStream());
    }
}
