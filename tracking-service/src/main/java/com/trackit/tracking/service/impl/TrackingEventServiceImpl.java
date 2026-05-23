package com.trackit.tracking.service.impl;

import com.trackit.tracking.dto.TrackingEventDto;
import com.trackit.tracking.entity.TrackingEventDocument;
import com.trackit.tracking.repository.TrackingEventRepository;
import com.trackit.tracking.service.TrackingEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackingEventServiceImpl implements TrackingEventService {

    private final TrackingEventRepository trackingRepository;

   

    @Override
    public List<TrackingEventDto> getEventsByOrderId(String orderId) {
        return trackingRepository.findByOrderIdOrderByScanTimeDesc(orderId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TrackingEventDto> getEventsByOrderId(String orderId, Pageable pageable) {
        return trackingRepository.findByOrderId(orderId, pageable)
                .map(this::mapToDto);
    }

    @Override
    public List<TrackingEventDto> getEventsByEmail(String email) {
        return trackingRepository.findByEmailOrderByScanTimeDesc(email)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TrackingEventDto> getEventsByEmail(String email, Pageable pageable) {
        return trackingRepository.findByEmail(email, pageable)
                .map(this::mapToDto);
    }

    /**
     * NEW METHOD: Find by Order ID AND Phone Number
     */
    @Override
    public List<TrackingEventDto> getEventsByOrderAndPhone(String orderId, String phonenumber) {
        // We use the new repository method we created
        return trackingRepository.findByOrderIdAndPhonenumberOrderByScanTimeDesc(orderId, phonenumber)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TrackingEventDto getLatestEventByOrderId(String orderId) {
        return trackingRepository.findTopByOrderIdOrderByScanTimeDesc(orderId)
                .map(this::mapToDto)
                .orElse(null);
    }

    @Override
    public void deleteEvent(String id) {
        trackingRepository.deleteById(id);
    }

    // =================================================================
    // MAPPERS (DTO <-> Entity)
    // =================================================================

    private TrackingEventDto mapToDto(TrackingEventDocument entity) {
        if (entity == null) return null;

        return TrackingEventDto.builder()
                .id(entity.getId())
                .orderId(entity.getOrderId())
                .email(entity.getEmail())
                .phonenumber(entity.getPhonenumber()) // Matches Entity field
                .status(entity.getStatus())
                .location(entity.getLocation())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .scanTime(entity.getScanTime())
                .estimatedDeliveryDate(entity.getEstimatedDeliveryDate())
                .build();
    }

    private TrackingEventDocument mapToEntity(TrackingEventDto dto) {
        if (dto == null) return null;

        return TrackingEventDocument.builder()
                .id(dto.getId())
                .orderId(dto.getOrderId())
                .email(dto.getEmail())
                .phonenumber(dto.getPhonenumber()) // Matches Entity field
                .status(dto.getStatus())
                .location(dto.getLocation())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .scanTime(dto.getScanTime())
                .estimatedDeliveryDate(dto.getEstimatedDeliveryDate())
                .build();
    }
}