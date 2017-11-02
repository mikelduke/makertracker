package org.tenbitworks.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tenbitworks.model.Member;
import org.tenbitworks.model.TrainingTeacher;
import org.tenbitworks.model.TrainingType;
import org.tenbitworks.repositories.AssetRepository;
import org.tenbitworks.repositories.MemberRepository;
import org.tenbitworks.repositories.MemberTrainingsRepository;
import org.tenbitworks.repositories.TrainingTeacherRepository;
import org.tenbitworks.repositories.TrainingTypeRepository;
import org.tenbitworks.repositories.UserRepository;

@Controller
public class TrainingTypeController {

	@Autowired
	TrainingTypeRepository trainingTypeRepository;

	@Autowired
	AssetRepository assetRepository;

	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	MemberTrainingsRepository memberTrainingRepository;
	
	@Autowired
	TrainingTeacherRepository trainingTeacherRepository;
	
	@Autowired
	UserRepository userRepository;

	@RequestMapping(value="/trainingtypes/{id}", method=RequestMethod.GET)
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public String getTrainingType(@PathVariable Long id, Model model){
		model.addAttribute("trainingType", trainingTypeRepository.findOne(id));
		model.addAttribute("trainingTypes", trainingTypeRepository.findAll());
		model.addAttribute("members", memberRepository.findAll());
		
		return "trainingTypes";
	}

	@RequestMapping(value="/trainingtypes/{id}", method=RequestMethod.GET, produces={"application/json"})
	@ResponseBody
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public TrainingType getTrainingTypeJson(@PathVariable Long id, Model model, SecurityContextHolderAwareRequestWrapper security){
		return trainingTypeRepository.findOne(id);
	}

	@RequestMapping(value = "/trainingtypes",method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public String trainingTypeList(Model model){
		model.addAttribute("trainingTypes",trainingTypeRepository.findAll());
		model.addAttribute("members", memberRepository.findAll());
		
		return "trainingTypes";
	}

	@RequestMapping(value = "/trainingtypes", method = RequestMethod.POST)
	@ResponseBody
	@Secured({"ROLE_ADMIN"})
	public Long saveTrainingType(@RequestBody TrainingType trainingType) {
		trainingTypeRepository.save(trainingType);
		return trainingType.getId();
	}

	@RequestMapping(value = "/trainingtypes/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	@Secured({"ROLE_ADMIN"})
	public String removeTrainingType(@PathVariable Long id){
		TrainingType trainingType = trainingTypeRepository.findOne(id);
		
		memberTrainingRepository.findAllByTrainingType(trainingType).forEach(memberTrainingRepository::delete);
		trainingTypeRepository.delete(id);
		return id.toString();
	}
	
	@RequestMapping(value = "/trainingtypes/{id}/teachers/{memberId}", method = RequestMethod.POST)
	@ResponseBody
	@PreAuthorize("hasRole('ADMIN')")
	public Long addTeacherToTrainingType(
			@PathVariable Long id, 
			@PathVariable UUID memberId,
			SecurityContextHolderAwareRequestWrapper security) {
		
		TrainingType tType = trainingTypeRepository.findOne(id);
		
		List<TrainingTeacher> teacherList = trainingTeacherRepository.findAllByTrainingType(tType);
		for (TrainingTeacher teacher : teacherList) {
			if (teacher.getMember().getId().equals(memberId)) {
				return 0L;
			}
		}
			
		TrainingTeacher tt = TrainingTeacher.builder()
				.member(memberRepository.findOne(memberId))
				.trainingType(tType)
				.addedDate(Calendar.getInstance().getTime())
				.addedBy(userRepository.findOne(security.getUserPrincipal().getName()))
				.build();
		
		tt = trainingTeacherRepository.save(tt);
		
		return tt.getId();
	}
	
	@RequestMapping(value = "/trainingtypes/{id}/teachers/{memberId}", method = RequestMethod.DELETE)
	@ResponseBody
	@PreAuthorize("hasRole('ADMIN')")
	public String removeTeacherFromTrainingType(
			@PathVariable Long id, 
			@PathVariable UUID memberId,
			SecurityContextHolderAwareRequestWrapper security) {
		
		TrainingType tType = trainingTypeRepository.findOne(id);
		
		List<TrainingTeacher> teacherList = trainingTeacherRepository.findAllByTrainingType(tType);
		for (TrainingTeacher teacher : teacherList) {
			if (teacher.getMember().getId().equals(memberId)) {
				trainingTeacherRepository.delete(teacher);
				
				return memberId.toString();
			}
		}
		
		return null;
	}
	
	@RequestMapping(value = "/trainingtypes/{id}/teachers/", method = RequestMethod.GET)
	@ResponseBody
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public List<TrainingTeacher> getTeachersForTrainingType(
			@PathVariable Long id, 
			SecurityContextHolderAwareRequestWrapper security) {
		
		TrainingType tType = trainingTypeRepository.findOne(id);

		List<TrainingTeacher> teacherList = trainingTeacherRepository.findAllByTrainingType(tType);
		
		if (!security.isUserInRole("ADMIN")) {
			for (TrainingTeacher teacher : teacherList) {
				teacher.setMember(new Member(teacher.getMember().getMemberName(), null));
				teacher.setAddedBy(null);
			}
		}
		
		return teacherList;
	}
	
	@RequestMapping(value="/trainingtypes/{id}/teachers/", method=RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<UUID>> setTeachersForTraining(
			@PathVariable Long id, 
			@RequestBody List<UUID> memberIds,
			SecurityContextHolderAwareRequestWrapper security) {
		
		TrainingType trainingType = trainingTypeRepository.findOne(id);

		List<TrainingTeacher> currentTrainings = trainingTeacherRepository.findAllByTrainingType(trainingType);
		List<UUID> existingIds = new ArrayList<>();
		for (TrainingTeacher tt : currentTrainings) {
			if (!memberIds.contains(tt.getMember().getId())) {
				trainingTeacherRepository.delete(tt);
			} else {
				existingIds.add(tt.getMember().getId());
			}
		}
		
		List<UUID> uuidsAdded = new ArrayList<>();
		for (UUID memberId : memberIds) {
			if (!existingIds.contains(memberId)) {
				if (addOneTeacherToTraining(trainingType, memberId, security.getUserPrincipal().getName())) {
					uuidsAdded.add(memberId);
				}
			}
		}
		
		return new ResponseEntity<>(uuidsAdded, HttpStatus.CREATED);
	}
	
	private boolean addOneTeacherToTraining(TrainingType trainingType, UUID memberId, String addedBy) {
		
		Member member = memberRepository.findOne(memberId);
		if (member == null) {
			return false;
		}
		
		List<TrainingTeacher> trainedInList = trainingTeacherRepository.findAllByTrainingType(trainingType);
		for (TrainingTeacher training : trainedInList) {
			if (training.getMember().getId().equals(memberId)) {
				return true;
			}
		}

		TrainingTeacher trainingTeacher = TrainingTeacher.builder()
				.member(member)
				.trainingType(trainingType)
				.addedDate(Calendar.getInstance().getTime())
				.addedBy(userRepository.findOne(addedBy))
				.build();
		trainingTeacher = trainingTeacherRepository.save(trainingTeacher);

		return true;
	}
}
