package com.task.generator.controller;

import com.task.generator.model.Id;
import com.task.generator.service.GeneratorIdService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/generate/")
public class GeneratorRestController {

    private final GeneratorIdService service;

    public GeneratorRestController(GeneratorIdService service) {
        this.service = service;
    }

    @GetMapping(path = "id")
    @ResponseBody
    public Id generateId() throws ExecutionException, InterruptedException {
        return service.generateId();
    }
}
