package org.tenbitworks.controllers;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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
	
	@RequestMapping(value="/trainings/{id}/members/", method=RequestMethod.GET)
	@ResponseBody
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public List<MemberTrainings> getMembersForTrainingType(
			@PathVariable Long id,
			SecurityContextHolderAwareRequestWrapper security) {
		TrainingType trainingType = trainingTypeRepository.findById(id).get();
		List<MemberTrainings> trainings = memberTrainingRepository.findAllByTrainingType(trainingType);
		
		if (!security.isUserInRole("ADMIN")) {
			trainings.forEach(member -> {
				member.setMember(new Member(member.getMember().getMemberName(), null));
			});
		}
		
		trainings.sort((t1, t2) -> t1.getMember().getMemberName().compareTo(t2.getMember().getMemberName()));
		
		return trainings;
	}
	
	@RequestMapping(value="/trainings/{id}/members/{memberId}", method=RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Long> addMemberToTraining(
			@PathVariable Long id, 
			@PathVariable UUID memberId,
			SecurityContextHolderAwareRequestWrapper security) {
		TrainingType trainingType = trainingTypeRepository.findById(id).get();
		Member member = memberRepository.findById(memberId).get();
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
				.addedBy(userRepository.findById(security.getUserPrincipal().getName()).get())
				.build();
		memberTraining = memberTrainingRepository.save(memberTraining);
		
		return new ResponseEntity<>(memberTraining.getId(), HttpStatus.CREATED);
	}
	
	@RequestMapping(value="/trainings/{id}/members/", method=RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN')") //TODO Add or haspermission to add members to training for teachers
	public ResponseEntity<List<UUID>> setMembersForTraining(
			@PathVariable Long id, 
			@RequestBody List<UUID> memberIds,
			SecurityContextHolderAwareRequestWrapper security) {
		
		TrainingType trainingType = trainingTypeRepository.findById(id).get();

		List<MemberTrainings> currentTrainings = memberTrainingRepository.findAllByTrainingType(trainingType);
		List<UUID> existingIds = new ArrayList<>();
		for (MemberTrainings mt : currentTrainings) {
			if (!memberIds.contains(mt.getMember().getId())) {
				memberTrainingRepository.delete(mt);
			} else {
				existingIds.add(mt.getMember().getId());
			}
		}
		
		List<UUID> uuidsAdded = new ArrayList<>();
		for (UUID memberId : memberIds) {
			if (!existingIds.contains(memberId)) {
				if (addOneMemberToTraining(trainingType, memberId, security.getUserPrincipal().getName())) {
					uuidsAdded.add(memberId);
				}
			}
		}
		
		return new ResponseEntity<>(uuidsAdded, HttpStatus.CREATED);
	}
	
	private boolean addOneMemberToTraining(TrainingType trainingType, UUID memberId, String addedBy) {
		Member member = memberRepository.findById(memberId).get();
		if (member == null) {
			return false;
		}
		
		List<MemberTrainings> trainedInList = memberTrainingRepository.findAllByMember(member);
		for (MemberTrainings training : trainedInList) {
			if (training.getTrainingType().equals(trainingType)) {
				return true;
			}
		}
		
		MemberTrainings memberTraining = MemberTrainings.builder()
				.member(member)
				.trainingType(trainingType)
				.trainingDate(Calendar.getInstance().getTime())
				.addedBy(userRepository.findById(addedBy).get())
				.build();
		memberTraining = memberTrainingRepository.save(memberTraining);
		
		return true;
	}
}
