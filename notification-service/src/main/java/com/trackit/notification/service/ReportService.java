package com.trackit.notification.service;

import com.trackit.notification.entity.Report;
import com.trackit.notification.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportRepository reportRepository;

    public Report submitReport(Report report) {
        report.setCreatedAt(LocalDateTime.now());
        report.setStatus("LOGGED");
        Report savedReport = reportRepository.save(report);

        // Notify Admins (Mock logic for now, could be email or push)
        notifyAdmins(savedReport);

        return savedReport;
    }

    private void notifyAdmins(Report report) {
        log.info("NOTIFICATION: New Incident Report #{} logged with severity {}. Subject: {}",
                report.getId(), report.getSeverity(), report.getSubject());
        // In a real system, this would trigger an email or a websocket notification to
        // admin dashboards
    }
}
