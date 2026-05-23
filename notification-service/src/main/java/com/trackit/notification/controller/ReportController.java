package com.trackit.notification.controller;

import com.trackit.notification.entity.Report;
import com.trackit.notification.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Report> submitReport(@RequestBody Report report) {
        return ResponseEntity.ok(reportService.submitReport(report));
    }
}
