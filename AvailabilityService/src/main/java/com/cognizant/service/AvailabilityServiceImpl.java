package com.cognizant.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cognizant.dto.AvailabilityDTO;
import com.cognizant.dto.TimeSlotDTO;
import com.cognizant.entity.Availability;
import com.cognizant.entity.TimeSlot;
import com.cognizant.entity.User;
import com.cognizant.exceptions.AvailabilityNotFoundException;
import com.cognizant.exceptions.AvailabilitySaveException;
import com.cognizant.exceptions.AvailabilityUpdateException;
import com.cognizant.exceptions.DoctorNotFoundException;
import com.cognizant.exceptions.InvalidTimeRangeException;
import com.cognizant.exceptions.TimeSlotNotFoundException;
import com.cognizant.repository.AvailabilityRepository;
import com.cognizant.repository.UserRepository;

@Service
public class AvailabilityServiceImpl implements IAvailabilityService{

	@Autowired
    private AvailabilityRepository availabilityRepository;
	@Autowired
    private UserRepository userRepository;
	@Autowired
	private ModelMapper modelMapper = new ModelMapper();

 

    public List<TimeSlot> generateTimeSlots(LocalTime startTime, LocalTime endTime, Availability availability) {
        if (!endTime.isAfter(startTime)) {
            throw new InvalidTimeRangeException(startTime, endTime);
        }
        List<TimeSlot> slots = new ArrayList<>();
        LocalTime current = startTime;
        while (current.isBefore(endTime)) {
            LocalTime slotEnd = current.plusMinutes(30);
            if (slotEnd.isAfter(endTime)) {
                slotEnd = endTime;
            }
            if (!current.equals(slotEnd)) {
                TimeSlot slot = new TimeSlot();
                slot.setStartTime(current);
                slot.setEndTime(slotEnd);
                slot.setAvailability(availability);
                slots.add(slot);
            }
            current = current.plusMinutes(30);
            if (!current.isBefore(endTime) && !current.equals(endTime)) {
                break;
            }
        }
        return slots;
    }

    @Transactional
    public AvailabilityDTO saveAvailability(AvailabilityDTO dto) {
        User doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException(dto.getDoctorId()));
        Availability availability = modelMapper.map(dto, Availability.class);
        availability.setDoctor(doctor);
        availability.setId(null);
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            List<TimeSlot> timeSlots = generateTimeSlots(dto.getStartTime(), dto.getEndTime(), availability);
            availability.setTimeSlots(timeSlots);
        } else if (dto.getTimeSlots() != null && !dto.getTimeSlots().isEmpty()) {
            List<TimeSlot> timeSlots = dto.getTimeSlots().stream()
                    .map(timeSlotDTO -> {
                        TimeSlot timeSlot = modelMapper.map(timeSlotDTO, TimeSlot.class);
                        timeSlot.setAvailability(availability);
                        return timeSlot;
                    })
                    .collect(Collectors.toList());
            availability.setTimeSlots(timeSlots);
        } else {
            availability.setTimeSlots(new ArrayList<>());
        }
        try {
            Availability saved = availabilityRepository.save(availability);
            return modelMapper.map(saved, AvailabilityDTO.class);
        } catch (Exception e) {
            throw new AvailabilitySaveException("Failed to save availability", e);
        }
    }

    @Transactional
    public AvailabilityDTO updateAvailabilityByDoctorAndDate(Long doctorId, LocalDate date, AvailabilityDTO updatedDto) {
        Availability existingAvailability = availabilityRepository.findByDoctorUserIdAndDate(doctorId, date)
                .orElseThrow(() -> new AvailabilityNotFoundException(doctorId, date));
 
        boolean timeRangeChanged = false;
 
        if (updatedDto.getStartTime() != null) {
            if (!updatedDto.getStartTime().equals(existingAvailability.getStartTime())) {
                existingAvailability.setStartTime(updatedDto.getStartTime());
                timeRangeChanged = true;
            }
        }
 
        if (updatedDto.getEndTime() != null) {
            if (!updatedDto.getEndTime().equals(existingAvailability.getEndTime())) {
                existingAvailability.setEndTime(updatedDto.getEndTime());
                timeRangeChanged = true;
            }
        }
 
        if (updatedDto.getTimeSlots() != null && !updatedDto.getTimeSlots().isEmpty()) {
            for (TimeSlotDTO incomingSlotDto : updatedDto.getTimeSlots()) {
                Optional<TimeSlot> foundSlot = Optional.empty();
 
                if (incomingSlotDto.getTimeSlotId() != null) {
                    foundSlot = existingAvailability.getTimeSlots().stream()
                        .filter(slot -> incomingSlotDto.getTimeSlotId().equals(slot.getTimeSlotId()))
                        .findFirst();
                }
 
                if (foundSlot.isEmpty() && incomingSlotDto.getStartTime() != null && incomingSlotDto.getEndTime() != null) {
                    foundSlot = existingAvailability.getTimeSlots().stream()
                        .filter(slot -> incomingSlotDto.getStartTime().equals(slot.getStartTime()) &&
                                         incomingSlotDto.getEndTime().equals(slot.getEndTime()))
                        .findFirst();
                }
 
                TimeSlot slotToUpdate = foundSlot.orElseThrow(() ->
                new TimeSlotNotFoundException("Time slot (ID: " + incomingSlotDto.getTimeSlotId() +
                                              " or Time: " + incomingSlotDto.getStartTime() + "-" + incomingSlotDto.getEndTime() +
                                              ") not found for update."));
            slotToUpdate.setAvailable(incomingSlotDto.isAvailable());

            }
        }
        else if (timeRangeChanged) {
            if (!existingAvailability.getEndTime().isAfter(existingAvailability.getStartTime())) {
                 throw new InvalidTimeRangeException(existingAvailability.getStartTime(), existingAvailability.getEndTime());
            }
 
            existingAvailability.getTimeSlots().clear();
            List<TimeSlot> newTimeSlots = generateTimeSlots(
                    existingAvailability.getStartTime(),
                    existingAvailability.getEndTime(),
                    existingAvailability
            );
            existingAvailability.setTimeSlots(newTimeSlots);
        }
 
        try {
            Availability updatedAvailability = availabilityRepository.save(existingAvailability);
            return mapAvailabilityToDto(updatedAvailability);
        } catch (Exception e) {
            throw new AvailabilityUpdateException("Failed to update availability", e);
        }
    }
    
    

    public List<AvailabilityDTO> getAvailabilityByDoctor(Long doctorId) {
        List<Availability> availabilities = availabilityRepository.findByDoctorUserId(doctorId);
        if (availabilities.isEmpty()) {
            throw new AvailabilityNotFoundException(doctorId, null); // Or just with doctorId
        }
        return availabilities.stream()
                .map(availability -> {
                    AvailabilityDTO availabilityDTO = modelMapper.map(availability, AvailabilityDTO.class);
                    if (availability.getTimeSlots() != null) {
                        List<TimeSlotDTO> timeSlotDTOs = availability.getTimeSlots().stream()
                                .map(timeSlot -> modelMapper.map(timeSlot, TimeSlotDTO.class))
                                .collect(Collectors.toList());
                        availabilityDTO.setTimeSlots(timeSlotDTOs);
                    } else {
                        availabilityDTO.setTimeSlots(new ArrayList<>());
                    }
                    return availabilityDTO;
                })
                .collect(Collectors.toList());
    }
    
    private AvailabilityDTO mapAvailabilityToDto(Availability availability) {
        AvailabilityDTO availabilityDTO = modelMapper.map(availability, AvailabilityDTO.class);
        if (availability.getTimeSlots() != null) {
            List<TimeSlotDTO> timeSlotDTOs = availability.getTimeSlots().stream()
                    .map(timeSlot -> modelMapper.map(timeSlot, TimeSlotDTO.class))
                    .collect(Collectors.toList());
            availabilityDTO.setTimeSlots(timeSlotDTOs);
        } else {
            availabilityDTO.setTimeSlots(new ArrayList<>());
        }
        return availabilityDTO;
    }
}