package org.tenbitworks.interlock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tenbitworks.model.Asset;
import org.tenbitworks.model.InterlockLogEntry;
import org.tenbitworks.model.Member;
import org.tenbitworks.repositories.AssetRepository;
import org.tenbitworks.repositories.InterlockLogRepository;
import org.tenbitworks.repositories.MemberRepository;

@Controller
public class InterlockLogController {

	@Autowired
	InterlockLogRepository logRepo;
	
	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	AssetRepository assetRepository;
	
	public void addLog(String rfid, Asset asset, Member member, String event) {
		InterlockLogEntry logEntry = new InterlockLogEntry(rfid, asset, member, event);
		
		logRepo.save(logEntry);
	}
	
	@RequestMapping(value = "/logs", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ADMIN')")
	public String logList(Model model, SecurityContextHolderAwareRequestWrapper security) {
		model.addAttribute("logs", logRepo.findAll(new Sort(Direction.DESC, "logDate")));

		return "logs";
	}
}
