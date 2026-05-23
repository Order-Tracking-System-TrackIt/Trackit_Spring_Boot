// service/AdminAnalyticsService.java
package com.trackit.admin.service;

import com.trackit.admin.dto.*;

import java.util.List;

public interface AdminAnalyticsService {
    KPIMetricsDTO getKPIMetrics();
    
    // Support Agents
    List<SupportAgentDTO> getSupportAgents();
    
    // Order Status Distribution
    List<OrderStatusDTO> getOrderStatusDistribution();
    
    // Heatmap Data
    List<HeatmapDataDTO> getHeatmapData();
    
    // Delivery Performance
    List<DeliveryPerformanceDTO> getDeliveryPerformance();
    
    // Revenue Data
    List<RevenueDTO> getRevenueData(int days);
    
    // Get Last 30 Days Revenue
    Double getLast30DaysRevenue();
    
    // Complete Dashboard Data
    AdminDashboardDTO getDashboardData();
}