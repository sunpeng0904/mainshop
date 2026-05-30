from typing import Dict, Any


class CodeGenerator:
    def generate_controller(self, entity_name: str) -> str:
        return f"""package com.online.mall.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/{entity_name.lower()}")
public class {entity_name}Controller {{

    @Autowired
    private {entity_name}Service {entity_name.lower()}Service;

    @GetMapping
    public Result getAll() {{
        return Result.success({entity_name.lower()}Service.findAll());
    }}

    @GetMapping("/{{id}}")
    public Result getById(@PathVariable Long id) {{
        return Result.success({entity_name.lower()}Service.findById(id));
    }}
}}
"""

    def generate_service(self, entity_name: str) -> str:
        return f"""package com.online.mall.service;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class {entity_name}Service {{

    public List<{entity_name}> findAll() {{
        // Implementation
        return List.of();
    }}

    public {entity_name} findById(Long id) {{
        // Implementation
        return null;
    }}
}}
"""
