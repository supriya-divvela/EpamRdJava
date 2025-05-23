package com.epam.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.dto.Credentials;
import com.epam.dto.EmailDto;
import com.epam.dto.TrainerDto;
import com.epam.dto.request.TraineeRegistrationRequest;
import com.epam.dto.request.UpdateTraineeRequest;
import com.epam.dto.request.UpdateTraineesTrainerListRequest;
import com.epam.dto.response.TraineeProfileResponse;
import com.epam.dto.response.UpdatedTraineeResponse;
import com.epam.exceptions.TraineeException;
import com.epam.exceptions.UserException;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.User;
import com.epam.repository.TraineeRepository;
import com.epam.repository.TrainerRepository;
import com.epam.repository.TrainingRepository;
import com.epam.repository.UserRepository;
import com.epam.service.interfaces.TraineeService;
import com.epam.utilities.RandomPasswordGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TraineeServiceImpl implements TraineeService {

	private final TraineeRepository traineeRepository;
	private final UserRepository userRepository;
	private final TrainerRepository trainerRepository;
	private final TrainingRepository trainingRepository;
//	private final KafkaTemplate<String, EmailDto> emailKafkaService;
	private final PasswordEncoder passwordEncoder;
	private String ccEmail = "supriyadivvela8187@gmail.com";
	private String kafkaEmailTopicName = "email-messages";

	@Override
	public TraineeProfileResponse getTraineeProfile(String username) {
		log.info("TraineeServiceImpl : getTraineeProfile ");
		Trainee trainee = traineeRepository.findByUserUsername(username)
				.orElseThrow(() -> new TraineeException("Trainee did not found with this username " + username));
		List<TrainerDto> listOfTrainerDto = trainee.getListOfTrainers().stream()
				.map(trainers -> TrainerDto.builder().username(trainers.getUser().getUsername())
						.firstname(trainers.getUser().getFirstname()).lastname(trainers.getUser().getLastname())
						.specialization(trainers.getTrainingType().getTrainingTypeName()).build())
				.collect(Collectors.toList());
		return new TraineeProfileResponse(trainee.getUser().getFirstname(), trainee.getUser().getLastname(),
				trainee.getDateOfBirth(), trainee.getAddress(), trainee.getUser().isActive(), listOfTrainerDto);
	}

	@Override
	public Credentials traineeRegistration(TraineeRegistrationRequest traineeRegistrationRequest) {
		log.info("TraineeServiceImpl : traineeRegistration ");
		String username = traineeRegistrationRequest.getEmail()
				.subSequence(0, traineeRegistrationRequest.getEmail().indexOf('@')).toString();
		String password = RandomPasswordGenerator.generateRandomPassword();
//		emailKafkaService.send(kafkaEmailTopicName, EmailDto.builder().toEmail(traineeRegistrationRequest.getEmail())
//				.ccEmail(ccEmail).subject("Trainee Registration Succesful").status("Not Sent")
//				.body("Hi " + traineeRegistrationRequest.getFirstname() + ",\nSuccesfully Registered..").build());
		log.info("emailfeignclient");
		User user = userRepository.save(User.builder().firstname(traineeRegistrationRequest.getFirstname())
				.lastname(traineeRegistrationRequest.getLastname()).email(traineeRegistrationRequest.getEmail())
				.username(username).password(password).build());
		log.info("hi" + user);
		traineeRepository.save(Trainee.builder().address(traineeRegistrationRequest.getAddress())
				.dateOfBirth(traineeRegistrationRequest.getDateOfBirth()).user(user).build());
		return Credentials.builder().username(username).password(passwordEncoder.encode("1234")).build();

	}

	@Transactional
	@Override
	public void deleteTraineeProfile(String traineeUsername) {
		log.info("TraineeServiceImpl : deleteTraineeProfile ");
		Trainee trainee = traineeRepository.findByUserUsername(traineeUsername)
				.orElseThrow(() -> new TraineeException("Trainee not found Exception.."));
		trainingRepository.deleteByTraineeId(trainee.getId());
		trainee.getListOfTrainers().stream().forEach(trainer -> {
			trainer.getListOfTrainees().remove(trainee);
			trainerRepository.save(trainer);
		});
		traineeRepository.delete(trainee);
		userRepository.deleteByUsername(traineeUsername);
	}

	@Override
	public UpdatedTraineeResponse updateTraineeProfile(UpdateTraineeRequest updateTraineeRequest) {
		log.info("TraineeServiceImpl : updateTraineeProfile ");
		User user = userRepository.findByUsername(updateTraineeRequest.getUsername())
				.orElseThrow(() -> new UserException("User Not Found Exception.."));
		user.setActive(updateTraineeRequest.isActive());
		user.setFirstname(updateTraineeRequest.getFirstname());
		user.setLastname(updateTraineeRequest.getLastname());
		user = userRepository.save(user);
		Trainee trainee = traineeRepository.findByUser(user)
				.orElseThrow(() -> new TraineeException("User not found.."));
		trainee.setDateOfBirth(updateTraineeRequest.getDateOfBirth());
		trainee.setAddress(updateTraineeRequest.getAddress());
		trainee = traineeRepository.save(trainee);
		List<TrainerDto> listOfTrainerDto = trainee.getListOfTrainers().stream()
				.map(trainers -> TrainerDto.builder().username(trainers.getUser().getUsername())
						.firstname(trainers.getUser().getFirstname()).lastname(trainers.getUser().getLastname())
						.specialization(trainers.getTrainingType().getTrainingTypeName()).build())
				.collect(Collectors.toList());
//		emailKafkaService.send(kafkaEmailTopicName,
//				EmailDto.builder().toEmail(updateTraineeRequest.getUsername() + "@gmail.com").ccEmail(ccEmail)
//						.subject("Trainee Updation Succesful").remarks("Have to reset Password")
//						.body("Hi " + updateTraineeRequest.getFirstname() + ",\n Updated Succesfully..").build());
		return new UpdatedTraineeResponse(updateTraineeRequest.getUsername(), user.getFirstname(), user.getLastname(),
				trainee.getDateOfBirth(), trainee.getAddress(), user.isActive(), listOfTrainerDto);
	}

	@Override
	public List<TrainerDto> updateTraineesTrainers(UpdateTraineesTrainerListRequest updateTraineesTrainerListRequest) {
		log.info("TraineeServiceImpl : updateTraineesTrainers ");
		Trainee trainee = traineeRepository.findByUserUsername(updateTraineesTrainerListRequest.getTraineeUsername())
				.orElseThrow(() -> new TraineeException("User Not Found.."));
		List<Trainer> trainers = updateTraineesTrainerListRequest.getListOfTrainerUsernames().stream()
				.map(trainer -> trainerRepository.findByUserUsername(trainer).get()).collect(Collectors.toList());
		trainee.getListOfTrainers().forEach(trainer -> trainer.getListOfTrainees().remove(trainee));
		trainers.forEach(trainer -> trainer.getListOfTrainees().add(trainee));
		traineeRepository.save(trainee);
//		emailKafkaService.send(kafkaEmailTopicName,
//				EmailDto.builder().toEmail(trainee.getUser().getEmail()).ccEmail(ccEmail)
//						.subject("Trainee Updation Succesful").remarks("Have to reset Password")
//						.body("Hi " + trainee.getUser().getFirstname() + ",\n Updated Trainees trainers Succesfully..")
//						.build());
		return trainers.stream()
				.map(trainer -> TrainerDto.builder().firstname(trainer.getUser().getUsername())
						.lastname(trainer.getUser().getLastname()).username(trainer.getUser().getUsername())
						.specialization(trainer.getTrainingType().getTrainingTypeName()).build())
				.collect(Collectors.toList());

	}

	@Override
	public List<TrainerDto> getNonActiveTrainersOnTrainee(String username) {
		log.info("TraineeServiceImpl : getNonActiveTrainersOnTrainee ");
		Trainee trainee = traineeRepository.findByUserUsername(username)
				.orElseThrow(() -> new TraineeException("User not found exception.."));
		return trainerRepository.findAll().stream().filter(trainer -> !trainer.getListOfTrainees().contains(trainee))
				.map(trainers -> TrainerDto.builder().lastname(trainers.getUser().getLastname())
						.specialization(trainers.getTrainingType().getTrainingTypeName())
						.firstname(trainers.getUser().getFirstname()).username(trainers.getUser().getUsername())
						.build())
				.collect(Collectors.toList());
	}

}
