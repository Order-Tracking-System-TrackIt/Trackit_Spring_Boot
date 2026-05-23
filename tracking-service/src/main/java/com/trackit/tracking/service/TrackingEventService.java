package com.trackit.tracking.service;

import com.trackit.tracking.dto.TrackingEventDto;
import com.trackit.tracking.entity.TrackingEventDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrackingEventService {

	
    
    List<TrackingEventDto> getEventsByOrderId(String orderId);
    
    Page<TrackingEventDto> getEventsByOrderId(String orderId, Pageable pageable);
    
    List<TrackingEventDto> getEventsByEmail(String email);
    
    List<TrackingEventDto> getEventsByOrderAndPhone(String orderId, String phonenumber);
    
    Page<TrackingEventDto> getEventsByEmail(String email, Pageable pageable);
    
    TrackingEventDto getLatestEventByOrderId(String orderId);
    
    void deleteEvent(String id);
}