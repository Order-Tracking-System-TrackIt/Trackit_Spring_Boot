package com.trackit.tracking.controller;

import com.trackit.tracking.dto.TrackingEventDto;
import com.trackit.tracking.service.TrackingEventService; // Removed Entity import as it's not needed here

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/tracking-events")
@RequiredArgsConstructor
public class TrackingEventController {

    private final TrackingEventService service;

  

    // ✅ Get All Events by Order ID (With Pagination)
    @GetMapping("/{orderId}")
    public ResponseEntity<Page<TrackingEventDto>> getEvents(
            @PathVariable String orderId,
            Pageable pageable) {

        Page<TrackingEventDto> events = service.getEventsByOrderId(orderId, pageable);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/email")
    public ResponseEntity<Page<TrackingEventDto>> getByEmail(
            @RequestParam String email,
            Pageable pageable) {

        return ResponseEntity.ok(
                service.getEventsByEmail(email, pageable)
        );
    }

    // ✅ Delete Event
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        service.deleteEvent(id);
        return ResponseEntity.noContent().build(); // 204
    }
}