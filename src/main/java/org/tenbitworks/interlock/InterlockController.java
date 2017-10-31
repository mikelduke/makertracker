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
import org.tenbitworks.repositories.AssetRepository;
import org.tenbitworks.repositories.MemberRepository;

@Controller
public class InterlockController {
	@Autowired
	AssetRepository assetRepository;

	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	InterlockLogController log;
	
	@RequestMapping(
			value="/api/interlock/{tenbitId}/rfids/{rfid}", 
			method=RequestMethod.GET)
	@ResponseBody
	@PreAuthorize("hasRole('ROLE_API')")
	public ResponseEntity<Long> checkAccessToAsset( //TODO Change to use 10bit friendly id
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
		//TODO Look up trainings list for member
//		if (asset.isTrainingRequired()) {
//			for (Member m : asset.getTrainingType().getMembers()) {
//				rfidList.add(m.getRfid());
//			}
//		} else {
//			for (Member m : memberRepository.findAll()) {
//				rfidList.add(m.getRfid());
//			}
//		}
		
		iad.setRfidList(rfidList);
		
		return new ResponseEntity<>(iad, HttpStatus.OK);
	}
	
	private static boolean checkAccess(Asset asset, Member member) {
		//TODO Check new trainings list
//		if (asset.isTrainingRequired() && asset.getTrainingType().getMembers().contains(member)) {
//			return true;
//		} else if (!asset.isTrainingRequired()) {
			return true;
//		} else {
//			return false;
//		}
	}
}
