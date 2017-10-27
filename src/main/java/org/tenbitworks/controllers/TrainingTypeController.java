package org.tenbitworks.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.tenbitworks.model.TrainingType;
import org.tenbitworks.repositories.AssetRepository;
import org.tenbitworks.repositories.MemberRepository;
import org.tenbitworks.repositories.TrainingTypeRepository;

@Controller
public class TrainingTypeController {

	@Autowired
	TrainingTypeRepository trainingTypeRepository;

	@Autowired
	AssetRepository assetRepository;

	@Autowired
	MemberRepository memberRepository;

	@RequestMapping(value="/trainingtypes/{id}", method=RequestMethod.GET)
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public String getTrainingType(@PathVariable Long id, Model model){
		model.addAttribute("trainingTypes", trainingTypeRepository.findAll());
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
		trainingTypeRepository.delete(id);
		return id.toString();
	}
}
