package com.trackit.admin.controller;

import com.trackit.admin.dto.*;
import com.trackit.admin.service.AdminAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Admin Analytics", description = "Admin dashboard analytics APIs")
public class AdminAnalyticsController {

    private final AdminAnalyticsService adminAnalyticsService;
    
    @GetMapping("/dashboard")
    @Operation(summary = "Get Complete Dashboard Data", description = "Retrieve all dashboard data in a single API call")
    public ResponseEntity<AdminDashboardDTO> getDashboardData() {
        log.info("Fetching complete dashboard data");
        AdminDashboardDTO dashboardData = adminAnalyticsService.getDashboardData();
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/kpi")
    @Operation(summary = "Get KPI Metrics", description = "Retrieve key performance indicators including on-time rate, active shipments, and revenue")
    public ResponseEntity<KPIMetricsDTO> getKPIMetrics() {
        log.info("Fetching KPI metrics");
        KPIMetricsDTO metrics = adminAnalyticsService.getKPIMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/support-agents")
    @Operation(summary = "Get Support Agents", description = "Retrieve all users with role 'support' from authentication table")
    public ResponseEntity<List<SupportAgentDTO>> getSupportAgents() {
        log.info("Fetching support agents");
        List<SupportAgentDTO> agents = adminAnalyticsService.getSupportAgents();
        return ResponseEntity.ok(agents);
    }

    @GetMapping("/order-status")
    @Operation(summary = "Get Order Status Distribution", description = "Retrieve order counts grouped by status for pie chart")
    public ResponseEntity<List<OrderStatusDTO>> getOrderStatusDistribution() {
        log.info("Fetching order status distribution");
        List<OrderStatusDTO> distribution = adminAnalyticsService.getOrderStatusDistribution();
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/heatmap")
    @Operation(summary = "Get Heatmap Data", description = "Retrieve location data with lat/long for geographic heatmap visualization")
    public ResponseEntity<List<HeatmapDataDTO>> getHeatmapData() {
        log.info("Fetching heatmap data");
        List<HeatmapDataDTO> heatmapData = adminAnalyticsService.getHeatmapData();
        return ResponseEntity.ok(heatmapData);
    }

    @GetMapping("/delivery-performance")
    @Operation(summary = "Get Delivery Performance", description = "Retrieve monthly on-time vs delayed delivery percentages")
    public ResponseEntity<List<DeliveryPerformanceDTO>> getDeliveryPerformance() {
        log.info("Fetching delivery performance");
        List<DeliveryPerformanceDTO> performance = adminAnalyticsService.getDeliveryPerformance();
        return ResponseEntity.ok(performance);
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get Revenue Data", description = "Retrieve daily revenue data for specified number of days")
    public ResponseEntity<List<RevenueDTO>> getRevenueData(
            @RequestParam(defaultValue = "30") int days) {
        log.info("Fetching revenue data for last {} days", days);
        List<RevenueDTO> revenueData = adminAnalyticsService.getRevenueData(days);
        return ResponseEntity.ok(revenueData);
    }

    @GetMapping("/revenue/total")
    @Operation(summary = "Get Total Revenue (30 Days)", description = "Retrieve total revenue from completed payments in last 30 days")
    public ResponseEntity<Double> getLast30DaysRevenue() {
        log.info("Fetching last 30 days total revenue");
        Double revenue = adminAnalyticsService.getLast30DaysRevenue();
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/active-shipments")
    @Operation(summary = "Get Active Shipments", description = "Retrieve count of orders with status IN_TRANSIT or OUT_FOR_DELIVERY")
    public ResponseEntity<Integer> getActiveShipments() {
        log.info("Fetching active shipments count");
        KPIMetricsDTO metrics = adminAnalyticsService.getKPIMetrics();
        return ResponseEntity.ok(metrics.getActiveShipments());
    }
}