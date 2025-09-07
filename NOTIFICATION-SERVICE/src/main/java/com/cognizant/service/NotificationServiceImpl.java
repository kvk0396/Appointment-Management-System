package com.cognizant.service;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import com.cognizant.dto.AppointmentResponseDTO;
import com.cognizant.dto.UserDTO;
import com.cognizant.enums.Role;
import com.cognizant.exception.UserNotFoundException;
import com.cognizant.model.EmailEntity;
import com.cognizant.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;

@Service
@Slf4j
public class NotificationServiceImpl implements INotificationService {

	@Autowired
	private JavaMailSender mailSender;

//	@Autowired
//	UserRepository userRepository;
	@Override
	public String sendMail(String mail, String subject, String body) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			
			message.setTo(mail);
			message.setSubject(subject);
			message.setText(body);
			message.setFrom("kvps2org@gmail.com");
			mailSender.send(message);
			return "Email sent successfully to " + mail;
		} catch (Exception e) {
			return "Failed to send email: " + e.getMessage();
		}
	}

	
	
	
	@Override
	public String sendMailToPatient(AppointmentResponseDTO appointment) throws Exception {
		UserDTO patient = appointment.getPatient();
		// Patient does not exist exception
		Optional<UserDTO> patientOpptional = Optional.ofNullable(patient);
		if (patientOpptional.isEmpty()) {
			throw new UserNotFoundException("Patient does not exist.");
		}
        
		String mail = patient.getEmail();
		log.info("patient mail is called"+ mail);
		String name = patient.getName();
		String phoneNo = patient.getPhone();
		String doctorName = appointment.getDoctor().getName();

		LocalTime startTime = appointment.getTimeSlot().getStartTime();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm a"); // Customize format if needed
		String slotTime = startTime.format(formatter);

		String status = appointment.getStatus().toString();

		String subject = "!!!Appointment Status Update!!!";
		String body = "Dear " + name + ",\n\n" + "We would like to inform you that your appointment has been " + status
				+ ".\n\n" + "Appointment Details:\n" +"----------------------------------\n"+ "Patient Name: " + name + "\n" + "Phone Number: " + phoneNo
				+ "\n" + "Status: " + status + "\n\n" + "Doctor Assigned: " + doctorName + "\n\n" + "Appointment date: "
				+appointment.getDateOfAppointment()+"\n"+"Slot Time: "+ slotTime + "\n\n" + "If you have any questions or need to reschedule, please contact us.\n\n"
				+ "Thank you,\n" + "Your Healthcare Team";
       
		EmailEntity notification= new EmailEntity();
        notification.setToEmail(mail);
        notification.setBody(body);
        notification.setSubject(subject);
        log.info(notification+"Email is stored in database");
        
		return sendMail(mail, subject, body);

	}

	@Override
	public List<String> sendMailToUsers(AppointmentResponseDTO appointment) throws Exception {
		String doctor = sendMailToDoctor(appointment);
		String patient = sendMailToPatient(appointment);

		return Arrays.asList(doctor, patient);
	}

	@Override
	public String sendMailToDoctor(AppointmentResponseDTO appointment) throws UserNotFoundException {
		UserDTO doctor = appointment.getDoctor();
		// Doctor does not exist exception
		Optional<UserDTO> doctorOpptional = Optional.ofNullable(doctor);
		if (doctorOpptional.isEmpty()) {
			throw new UserNotFoundException("Doctor does not exist.");
		}
		String doctorName = doctor.getName();
		String doctorEmail = doctor.getEmail();
		// Long doctorId=doctor.getUserId();
		String patientName = appointment.getPatient().getName();

		LocalTime startTime = appointment.getTimeSlot().getStartTime();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm a"); // Customize format if needed
		String slotTime = startTime.format(formatter);

		String status = appointment.getStatus().toString();

		String subject = "!!!Appointment Status Update!!!";

		String body = "Dear Dr. " + doctorName + ",\n\n"
				+ "We would like to inform you that the appointment with the following patient has been " + status
				+ ".\n\n" + "Appointment Details:\n"+"----------------------------------\n" + "Patient Name: " + patientName + "\n" + "Scheduled date: "
			  +appointment.getDateOfAppointment()+"\n"	+"Slot Time: "+ slotTime + "\n" + "Status: " + status + "\n\n"
				+ "Please update your schedule accordingly. If you have any questions or need further information, feel free to reach out.\n\n"
				+ "Thank you,\n" + "Your Healthcare Team";
		
		EmailEntity notification= new EmailEntity();
        notification.setToEmail(doctorEmail);
        notification.setBody(body);
        notification.setSubject(subject);
        log.info(notification+"Email is stored in database");


		return sendMail(doctorEmail, subject, body);
	}

	@Override
	public String userCreationAckMail(UserDTO user) throws Exception{
		Optional<UserDTO> userOpptional =Optional.ofNullable(user);
		if(userOpptional.isEmpty()) {
			throw new UserNotFoundException("User is not registeration failed");
		}
	    String userMail = user.getEmail();
	    String userName = user.getName();
	    String userPhno = user.getPhone();
	    Long userId = user.getUserId();
	    Role userRole = user.getRole();
	    String subject = "User Account Creation Confirmation";
	    if(userRole==Role.DOCTOR) {
	    	  String body = String.format(
	    		        "Dear Dr. %s,\n\n" +
	    		        "We are pleased to inform you that your account has been successfully created. Below are your account details:\n\n" +
	    		        "User ID: %d\n" +
	    		        "Email: %s\n" +
	    		        "Phone: %s\n" +
	    		        "Role: %s\n\n" +
	    		        "If you have any questions or need further assistance, please do not hesitate to contact our support team.\n\n" +
	    		        "Thank you for choosing our services.\n\n" +
	    		        "Best regards,\n" +
	    		        "Your Company Name",
	    		        userName, userId, userMail, userPhno, userRole
	    		    );
	    		EmailEntity notification= new EmailEntity();
	            notification.setToEmail(userMail);
	            notification.setBody(body);
	            notification.setSubject(subject);
	            log.info(notification+"Email is stored in database");

	    	  sendMail(userMail, subject, body);

	    }
	    else {
	    	 String body = String.format(
	    		        "Dear Mr/Mrs. %s,\n\n" +
	    		        "We are pleased to inform you that your account has been successfully created. Below are your account details:\n\n" +
	    		        "User ID: %d\n" +
	    		        "Email: %s\n" +
	    		        "Phone: %s\n" +
	    		        "Role: %s\n\n" +
	    		        "If you have any questions or need further assistance, please do not hesitate to contact our support team.\n\n" +
	    		        "Thank you for choosing our services.\n\n" +
	    		        "Best regards,\n" +
	    		        "Your Company Name",
	    		        userName, userId, userMail, userPhno, userRole
	    		    );
	    		EmailEntity notification= new EmailEntity();
	            notification.setToEmail(userMail);
	            notification.setBody(body);
	            notification.setSubject(subject);
	            log.info(notification+"Email is stored in database");

	   	   sendMail(userMail, subject, body);

	    }
	    
	   
	    
	    return "Email sent successfully to " + userMail;
	}

}
