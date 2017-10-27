package org.tenbitworks.controllers;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tenbitworks.model.Member;
import org.tenbitworks.model.MemberTrainings;
import org.tenbitworks.model.TrainingType;
import org.tenbitworks.repositories.MemberRepository;
import org.tenbitworks.repositories.MemberTrainingsRepository;
import org.tenbitworks.repositories.TrainingTypeRepository;
import org.tenbitworks.repositories.UserRepository;

@Controller
public class MemberTrainingController {

	@Autowired
	TrainingTypeRepository trainingTypeRepository;

	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MemberTrainingsRepository memberTrainingRepository;

	@RequestMapping(value="/trainings/{id}/members/{memberId}", method=RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Long> addTraining( //TODO Add way to add a list of members at once
			@PathVariable Long id, 
			@PathVariable UUID memberId,
			SecurityContextHolderAwareRequestWrapper security) {
		TrainingType trainingType = trainingTypeRepository.findOne(id);
		Member member = memberRepository.findOne(memberId);
		if (member == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		List<MemberTrainings> trainedInList = memberTrainingRepository.findAllByMember(member);
		for (MemberTrainings training : trainedInList) {
			if (training.getTrainingType().equals(trainingType)) {
				return new ResponseEntity<>(HttpStatus.OK);
			}
		}
		
		MemberTrainings memberTraining = MemberTrainings.builder()
				.member(member)
				.trainingType(trainingType)
				.trainingDate(Calendar.getInstance().getTime())
				.addedBy(userRepository.findOne(security.getUserPrincipal().getName()))
				.build();
		memberTraining = memberTrainingRepository.save(memberTraining);
		
		return new ResponseEntity<>(memberTraining.getId(), HttpStatus.CREATED);
	}
}
