package com.cognizant.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.config.APIClient;
import com.cognizant.dto.AppointmentDTO;
import com.cognizant.dto.UserDTO;
import com.cognizant.entity.Appointment;
import com.cognizant.entity.User;
import com.cognizant.enums.Role;
import com.cognizant.exceptions.NoAppointmentsFoundException;
import com.cognizant.exceptions.NoUsersFoundException;
import com.cognizant.exceptions.RoleMismatchException;
import com.cognizant.exceptions.UserNotFoundException;
import com.cognizant.mapper.AppointmentMapper;
import com.cognizant.mapper.UserMapper;
import com.cognizant.repository.UserRepository;

@Service
public class DoctorServiceImpl implements IDoctorService {

	private static final Logger logger = LoggerFactory.getLogger(DoctorServiceImpl.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	JwtService jwtService;
	// @Autowired
	// private ModelMapper modelMapper; // Commented out in original code, keeping as is

	@Autowired
	private APIClient apiClient;

	/**
	 * Adds a new doctor user to the system.
	 * It first validates if the user's role is indeed DOCTOR.
	 * After saving the user, it calls the APIClient to send a registration confirmation email.
	 * @param user The UserDTO object containing the doctor's details.
	 * @return The saved UserDTO object.
	 */
	@Override
	public UserDTO addUser(UserDTO user) {
		logger.info("Entering addUser method for doctor with user email: {}", user.getEmail());
		if(!user.getRole().equals(Role.DOCTOR)) {
			throw new RoleMismatchException("You cannot login as a patient here");
		}
		UserDTO responseUser = null;
		try {
			User savedUser = userRepository.save(UserMapper.convertToEntity(user));
			logger.info("Doctor user saved successfully with ID: {}", savedUser.getUserId());

			responseUser = UserMapper.convertToDto(savedUser);
			logger.info("Converted saved Doctor user entity to DTO.");

			logger.info("Calling APIClient to send registration mail for doctor: {}", responseUser.getEmail());
			apiClient.sendMailForUserRegistry(responseUser);
			logger.info("Registration mail sent successfully for doctor: {}", responseUser.getEmail());

		} catch (Exception e) {
			logger.error("Error occurred while adding doctor user with email: {}", user.getEmail(), e);
			throw e; // Re-throwing exception after logging
		}
		logger.info("Exiting addUser method. Returning responseUser for doctor: {}", responseUser.getEmail());
		return responseUser;
	}

	/**
	 * Updates the details of an existing doctor user.
	 * It finds the doctor by ID and then updates their role, email, name, and phone number.
	 * The updated user is saved back to the repository and then converted to a DTO.
	 * @param id The unique ID of the doctor to update.
	 * @param user The UserDTO object containing the updated doctor details.
	 * @return The updated UserDTO object.
	 */
	@Override
	public UserDTO updateUser(Long id, UserDTO user) {
		logger.info("Entering updateUser method for doctor ID: {} with new data: {}", id, user.getEmail());
		UserDTO savedResponse = null;
		try {
			User foundUser = userRepository.findById(id).orElseThrow(() -> {
				logger.info("User with ID: {} not found for update operation.", id);
				return new UserNotFoundException("Doctor with ID " + id + " not found for update.");
			});;
			if (foundUser == null) { // This null check is redundant because orElseThrow already handles it.
				logger.info("Doctor user with ID: {} not found for update operation.", id); 
				return null;
			}
			logger.info("Found doctor user with ID: {}. Updating details.", id);

			foundUser.setRole(user.getRole());
			foundUser.setEmail(user.getEmail());
			foundUser.setName(user.getName()); // Changed from user.getEmail() to user.getName() for consistency
			foundUser.setPhone(user.getPhone());
			logger.info("Doctor user details updated in entity. Saving to repository.");

			User savedUser = userRepository.save(foundUser);
			logger.info("Doctor user with ID: {} updated successfully.", savedUser.getUserId());

			savedResponse = UserMapper.convertToDto(savedUser);
			logger.info("Converted updated Doctor user entity back to DTO.");

		} catch (Exception e) {
			logger.error("Error occurred while updating doctor user with ID: {}", id, e);
			throw e; // Re-throwing exception after logging
		}
		logger.info("Exiting updateUser method. Returning savedResponse for doctor ID: {}", id);
		return savedResponse;
	}

	/**
	 * Retrieves a list of all users with the DOCTOR role.
	 * It queries the repository for users having the DOCTOR role.
	 * If no doctors are found, a NoUsersFoundException is thrown.
	 * @return A list of UserDTO objects representing all doctors.
	 */
	@Override
	public List<UserDTO> viewAllUsers() {
		logger.info("Entering viewAllUsers method to retrieve all doctors.");
		List<UserDTO> doctorDTOList = null;
		try {
			List<User> users = userRepository.findByRole(Role.DOCTOR).orElseThrow(() -> {
				logger.info("No users found with role PATIENT."); // Log message seems to be for PATIENT role
				return new NoUsersFoundException("No doctor users found in the system."); // Exception message for doctors
			});
			if (users == null || users.isEmpty()) { // This null/empty check is redundant because orElseThrow handles it.
				logger.info("No users found with role DOCTOR."); 
				return null;
			}
			logger.info("Found {} users with role DOCTOR. Converting to DTOs.", users.size());

			doctorDTOList = UserMapper.convertToDtoList(users);
			logger.info("Converted list of User entities to Doctor UserDTOs.");

		} catch (Exception e) {
			logger.error("Error occurred while viewing all users (doctors).", e);
			throw e; // Re-throwing exception after logging
		}
		logger.info("Exiting viewAllUsers method. Returning list of {} doctor users.", doctorDTOList != null ? doctorDTOList.size() : 0);
		return doctorDTOList;
	}

	/**
	 * Retrieves all appointments associated with a specific doctor.
	 * It first verifies that the provided ID belongs to a DOCTOR role user.
	 * If the user is a doctor, it fetches their associated appointments.
	 * @param doctorId The unique ID of the doctor whose appointments are to be viewed.
	 * @return A list of AppointmentDTO objects for the specified doctor.
	 */
	@Override
	public List<AppointmentDTO> viewAllDoctorAppointments(Long doctorId) {
		logger.info("Entering viewAllDoctorAppointments method for doctor ID: {}", doctorId);
		List<AppointmentDTO> appointmentDTOList = null;
		try {
			User user = userRepository.findById(doctorId).orElseThrow(() -> {
				logger.info("Doctor with ID: {} not found.", doctorId); // Log message is for Doctor
				return new UserNotFoundException("Doctor with ID " + doctorId + " not found.");
			});
			if (user == null) { // This null check is redundant because orElseThrow handles it.
				logger.info("Doctor user with ID: {} not found for appointments retrieval.", doctorId); 
				return null;
			}
			if (user.getRole() == Role.DOCTOR) {
				logger.info("User with ID: {} is a DOCTOR. Retrieving appointments.", doctorId);
				List<Appointment> doctorAppts = user.getDoctorAppointments();
				if (doctorAppts == null || doctorAppts.isEmpty()) {
					logger.info("No appointments found for doctor ID: {}.", doctorId);
					throw new NoAppointmentsFoundException("No appointments found for doctor with ID " + doctorId + ".");
				}
				logger.info("Found {} appointments for doctor ID: {}. Converting to DTOs.", doctorAppts.size(), doctorId);
				appointmentDTOList = AppointmentMapper.convertToDtoList(doctorAppts);
				logger.info("Converted list of Appointment entities to AppointmentDTOs.");
			} else {
				logger.info("User with ID: {} is not a DOCTOR. Role is: {}", doctorId, user.getRole());
				throw new RoleMismatchException("User with ID " + doctorId + " is not a Doctor.");
			}
		} catch (Exception e) {
			logger.error("Error occurred while viewing all appointments for doctor ID: {}", doctorId, e);
			throw e; // Re-throwing exception after logging
		}
		logger.info("Exiting viewAllDoctorAppointments method. Returning list of {} appointments for doctor ID: {}.", appointmentDTOList != null ? appointmentDTOList.size() : 0, doctorId);
		return appointmentDTOList;
	}
	
	/**
	 * Retrieves the details of a doctor user by their unique ID.
	 * It queries the repository to find the user by the given ID.
	 * If the user is not found, a UserNotFoundException is thrown.
	 * @param doctorId The unique ID of the doctor to retrieve.
	 * @return The UserDTO object containing the doctor's details.
	 */
	@Override
	public UserDTO viewDoctorById(Long doctorId) {
		UserDTO userDTO = null;
		try {
			// Find user by ID
			User user = userRepository.findById(doctorId).orElseThrow(() -> {
				logger.info("Doctor with ID: {} not found.", doctorId);
				return new UserNotFoundException("Doctor with ID " + doctorId + " not found.");
			});
			if (user == null) { // This null check is redundant because orElseThrow handles it.
				logger.info("Doctor with ID: {} not found.", doctorId); 
				return null;
			}

			// Convert entity to DTO
			userDTO = UserMapper.convertToDto(user);

		} catch (Exception e) {
			logger.error("Error occurred while viewing Doctor by ID: {}", doctorId, e);
			throw e;
		}
		logger.info("Exiting viewDoctorById method. Returning user details for ID: {}.", doctorId);
		return userDTO;
	}

	/**
	 * Authenticates a doctor user by their email and generates a JWT token.
	 * It fetches the user from the repository using the provided email.
	 * If the user is not found, a UserNotFoundException is thrown.
	 * @param email The email of the doctor attempting to log in.
	 * @return A JWT token string for the authenticated doctor.
	 */
	@Override
	public String checkLogin(String email) {
	    User user = userRepository.findByEmail(email).orElseThrow(
	            () -> {
	                
	                return new UserNotFoundException("Doctor with email " + email + " not found.");
	            });
	    // This line will only execute if a User is found (i.e., no exception thrown above)
	    return jwtService.generateToken(email, UserMapper.convertToDto(user));
	}
}