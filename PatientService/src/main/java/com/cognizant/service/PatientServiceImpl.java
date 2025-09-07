package com.cognizant.service;

import java.util.List;

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
import com.cognizant.mapper.AppointmentMapper;
import com.cognizant.mapper.UserMapper;
import com.cognizant.repository.AppointmentRepository;
import com.cognizant.repository.UserRepository;
import com.cognizant.exceptions.UserNotFoundException; 
import com.cognizant.exceptions.RoleMismatchException; 
import com.cognizant.exceptions.NoUsersFoundException; 
import com.cognizant.exceptions.NoAppointmentsFoundException; 


@Service
public class PatientServiceImpl implements IPatientService {

	private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	APIClient apiClient;

	@Autowired
	AppointmentRepository appointmentRepository;
	
	@Autowired
	JwtService jwtService;

	/**
	 * Adds a new patient user to the system.
	 * It ensures the user's role is PATIENT, converts the DTO to an entity, saves it,
	 * converts the saved entity back to DTO, and then sends a registration email via APIClient.
	 * Returns the newly created patient's DTO.
	 * @param user The UserDTO object containing the patient's details.
	 * @return The UserDTO object of the newly added patient.
	 */
	@Override
	public UserDTO addUser(UserDTO user) {
		logger.info("Entering addUser method with user: {}", user.getEmail());
		UserDTO responseUser = null;
		if(!user.getRole().equals(Role.PATIENT)) {
			throw new RoleMismatchException("You cannot login as a doctor here");
		}
		try {
			// Convert DTO to entity
			User savedUser = UserMapper.convertToEntity(user);
			logger.info("Converted UserDTO to User entity. Setting role to PATIENT.");
			savedUser.setRole(Role.PATIENT);

			// Save user to repository
			savedUser = userRepository.save(savedUser);
			logger.info("User saved successfully with ID: {}", savedUser.getUserId());

			// Convert saved entity back to DTO
			responseUser = UserMapper.convertToDto(savedUser);
            logger.info("Converted saved User entity back to UserDTO.");

			// Send mail for user registration
			logger.info("Calling APIClient to send registration mail for user: {}", responseUser.getEmail());
			apiClient.sendMailForUserRegistry(responseUser);
			logger.info("Registration mail sent successfully for user: {}", responseUser.getEmail());

		} catch (Exception e) {
			logger.error("Error occurred while adding user: {}", user.getEmail(), e);
			// Optionally rethrow or handle the exception based on business requirements
			throw e;
		}
		logger.info("Exiting addUser method. Returning responseUser: {}", responseUser.getEmail());
		return responseUser;
	}
	
	/**
	 * Updates the details of an existing patient user.
	 * It finds the patient by their ID, updates their email, name, and phone number from the provided DTO.
	 * The updated user is then saved back to the repository.
	 * Returns the DTO of the updated patient.
	 * @param id The unique ID of the patient to update.
	 * @param user The UserDTO object containing the updated patient details.
	 * @return The UserDTO object of the updated patient.
	 */

	@Override
	public UserDTO updateUser(Long id, UserDTO user) {
		logger.info("Entering updateUser method for user ID: {} with new data: {}", id, user.getEmail());
		UserDTO savedResponse = null;
		try {
			// Find user by ID
			User foundUser = userRepository.findById(id)
					.orElseThrow(() -> {
						logger.info("User with ID: {} not found for update operation.", id);
						return new UserNotFoundException("User with ID " + id + " not found for update.");
					});
			logger.info("Found user with ID: {}. Updating details.", id);

			// Update user details
			foundUser.setEmail(user.getEmail());
			foundUser.setName(user.getName());
			foundUser.setPhone(user.getPhone());
			logger.info("User details updated in entity. Saving to repository.");

			// Save updated user to repository
			User savedUser = userRepository.save(foundUser);
			logger.info("User with ID: {} updated successfully.", savedUser.getUserId());

			// Convert saved entity back to DTO
			savedResponse = UserMapper.convertToDto(savedUser);
            logger.info("Converted updated User entity back to UserDTO.");

		} catch (Exception e) {
			logger.error("Error occurred while updating user with ID: {}", id, e);
			throw e;
		}
		logger.info("Exiting updateUser method. Returning savedResponse for user ID: {}", id);
		return savedResponse;
	}

	/**
	 * Retrieves a list of all users who have the PATIENT role.
	 * It queries the user repository to find all users specifically marked as PATIENT.
	 * Throws a NoUsersFoundException if no patient users are found in the system.
	 * @return A list of UserDTO objects representing all patients.
	 */
	
	@Override
	public List<UserDTO> viewAllUsers() {
		logger.info("Entering viewAllUsers method to retrieve all patients.");
		List<UserDTO> userDTOList = null;
		try {
			// Find all users with PATIENT role
			List<User> users = userRepository.findByRole(Role.PATIENT)
					.orElseThrow(() -> {
						logger.info("No users found with role PATIENT.");
						return new NoUsersFoundException("No patient users found in the system.");
					});
            logger.info("Found {} users with role PATIENT. Converting to DTOs.", users.size());

			// Convert list of entities to list of DTOs
			userDTOList = UserMapper.convertToDtoList(users);
            logger.info("Converted list of User entities to UserDTOs.");

		} catch (Exception e) {
			logger.error("Error occurred while viewing all users (patients).", e);
			throw e;
		}
		logger.info("Exiting viewAllUsers method. Returning list of {} patient users.", userDTOList != null ? userDTOList.size() : 0);
		return userDTOList;
	}

	/**
	 * Retrieves all appointments associated with a specific patient.
	 * It first verifies that the provided ID belongs to a PATIENT role user.
	 * If the user is a patient, it fetches their associated appointments from the user entity.
	 * @param patientId The unique ID of the patient whose appointments are to be viewed.
	 * @return A list of AppointmentDTO objects for the specified patient.
	 */
	
	@Override
	public List<AppointmentDTO> viewAllPatientAppointments(Long patientId) {
		logger.info("Entering viewAllPatientAppointments method for patient ID: {}", patientId);
		List<AppointmentDTO> appointmentDTOList = null;
		try {
			// Verify user role and retrieve appointments
			User user = userRepository.findById(patientId)
					.orElseThrow(() -> {
						logger.info("Patient with ID: {} not found.", patientId);
						return new UserNotFoundException("Patient with ID " + patientId + " not found.");
					});

			if (user.getRole() == Role.PATIENT) {
				logger.info("User with ID: {} is a PATIENT. Retrieving appointments.", patientId);
				List<Appointment> patientAppts = user.getPatientAppointments();
				if (patientAppts == null || patientAppts.isEmpty()) {
					logger.info("No appointments found for patient ID: {}.", patientId);
					throw new NoAppointmentsFoundException("No appointments found for patient with ID " + patientId + ".");
				}
				logger.info("Found {} appointments for patient ID: {}. Converting to DTOs.", patientAppts.size(), patientId);
				appointmentDTOList = AppointmentMapper.convertToDtoList(patientAppts);
                logger.info("Converted list of Appointment entities to AppointmentDTOs.");
			} else {
				logger.info("User with ID: {} is not a PATIENT. Role is: {}", patientId, user.getRole());
				throw new RoleMismatchException("User with ID " + patientId + " is not a patient.");
			}
		} catch (Exception e) {
			logger.error("Error occurred while viewing all appointments for patient ID: {}", patientId, e);
			throw e;
		}
		logger.info("Exiting viewAllPatientAppointments method. Returning list of {} appointments for patient ID: {}.", appointmentDTOList != null ? appointmentDTOList.size() : 0, patientId);
		return appointmentDTOList;
	}

	/**
	 * Retrieves the details of a patient user by their unique ID.
	 * It queries the user repository to find the patient by the given ID.
	 * If the patient is not found, a UserNotFoundException is thrown.
	 * @param patientId The unique ID of the patient to retrieve.
	 * @return The UserDTO object containing the patient's details.
	 */
	
	@Override
	public UserDTO viewPatientById(Long patientId) {
        logger.info("Entering viewPatientById method for patient ID: {}", patientId);
		UserDTO userDTO = null;
		try {
			// Find user by ID
			User user = userRepository.findById(patientId)
					.orElseThrow(() -> {
						logger.info("Patient with ID: {} not found.", patientId);
						return new UserNotFoundException("Patient with ID " + patientId + " not found.");
					});
			logger.info("Found patient with ID: {}. Converting to DTO.", patientId);

			// Convert entity to DTO
			userDTO = UserMapper.convertToDto(user);
            logger.info("Converted User entity to UserDTO.");

		} catch (Exception e) {
			logger.error("Error occurred while viewing patient by ID: {}", patientId, e);
			throw e;
		}
		logger.info("Exiting viewPatientById method. Returning user details for ID: {}.", patientId);
		return userDTO;
	}

	/**
	 * Authenticates a patient user by their email and generates a JWT token.
	 * It fetches the user from the repository using the provided email.
	 * If no user is found with the given email, a UserNotFoundException is thrown.
	 * @param email The email of the patient attempting to log in.
	 * @return A JWT token string for the authenticated patient.
	 */
	
	@Override
	public String checkLogin(String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> 
	        new UserNotFoundException("User with email " + email + " not found."));
		String token=jwtService.generateToken(email, UserMapper.convertToDto(user));
		return token;		
	}
	
}