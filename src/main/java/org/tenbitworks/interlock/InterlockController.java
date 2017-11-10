package org.tenbitworks.interlock;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tenbitworks.model.Asset;
import org.tenbitworks.model.Member;
import org.tenbitworks.model.MemberStatus;
import org.tenbitworks.model.MemberTrainings;
import org.tenbitworks.repositories.AssetRepository;
import org.tenbitworks.repositories.MemberRepository;
import org.tenbitworks.repositories.MemberTrainingsRepository;
import org.tenbitworks.repositories.TrainingTypeRepository;

@Controller
public class InterlockController {
	@Autowired
	AssetRepository assetRepository;

	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	TrainingTypeRepository trainingTypeRepository;
	
	@Autowired
	MemberTrainingsRepository memberTrainingRepository;
	
	@Autowired
	InterlockLogController log;
	
	@RequestMapping(
			value="/api/interlock/{tenbitId}/rfids/{rfid}", 
			method=RequestMethod.GET)
	@ResponseBody
	@PreAuthorize("hasRole('ROLE_API')")
	public ResponseEntity<Long> checkAccessToAsset(
			@PathVariable String tenbitId, 
			@PathVariable String rfid){
		
		Asset asset = assetRepository.findOneByTenbitId(tenbitId);
		if (asset == null) {
			log.addLog(rfid, null, null, "Asset not found for id " + tenbitId);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		Member member = memberRepository.findOneByRfid(rfid);
		if (member == null) {
			log.addLog(rfid, asset, null, "RFID Not Found");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		if (checkAccess(asset, member)) {
			log.addLog(rfid, asset, member, "Access Granted");
			return new ResponseEntity<Long>(asset.getAccessControlTimeMS(), HttpStatus.OK);
		} else {
			log.addLog(rfid, asset, member, "Access Denied");
			return new ResponseEntity<Long>(HttpStatus.FORBIDDEN);
		}
	}
	
	@RequestMapping(
			value="/api/interlock/{tenbitId}/rfids", 
			method=RequestMethod.GET, 
			produces={"application/json"})
	@ResponseBody
	@PreAuthorize("hasRole('ROLE_API')")
	public ResponseEntity<InterlockAccessDTO> getValidRfidsForAsset(
			@PathVariable String tenbitId){
		
		Asset asset = assetRepository.findOneByTenbitId(tenbitId);
		if (asset == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		InterlockAccessDTO iad = new InterlockAccessDTO();
		iad.setAccessTimeMS(asset.getAccessControlTimeMS());
		iad.setTrainingRequired(asset.isTrainingRequired());
		iad.setTenbitId(tenbitId);
		
		List<String> rfidList = new ArrayList<>();
		if (asset.isTrainingRequired()) {
			memberTrainingRepository.findAllByTrainingType(asset.getTrainingType())
					.forEach(mt -> {
						if (isActive(mt.getMember())) {
							rfidList.add(mt.getMember().getRfid());
						}
					});
		} else {
			memberRepository.findAll().forEach(m -> rfidList.add(m.getRfid()));
		}

		iad.setRfidList(rfidList);
		
		return new ResponseEntity<>(iad, HttpStatus.OK);
	}
	
	private boolean checkAccess(Asset asset, Member member) {
		if (asset.isTrainingRequired()) {
			List<MemberTrainings> mtList = memberTrainingRepository.findAllByTrainingType(asset.getTrainingType());
			for (MemberTrainings mt : mtList) {
				if (mt.getMember().equals(member) && isActive(member)) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}

	private boolean isActive(Member member) {
		if (MemberStatus.MEMBER.equals(member.getStatus())) {
			return true;
		} else if (MemberStatus.OFFICER.equals(member.getStatus())) {
			return true;
		}
		
		return false;
	}
}
