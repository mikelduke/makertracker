package org.tenbitworks.controllers;

import javax.validation.Valid;

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
import org.tenbitworks.model.Asset;
import org.tenbitworks.repositories.AssetRepository;
import org.tenbitworks.repositories.MemberRepository;
import org.tenbitworks.repositories.TrainingTypeRepository;

@Controller
public class AssetController {

    @Autowired
    AssetRepository assetRepository;
    
    @Autowired
    MemberRepository memberRepository;
    
    @Autowired
    TrainingTypeRepository trainingTypeRepository;

    @RequestMapping(value="/assets/{id}", method=RequestMethod.GET)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public String getAsset(@PathVariable Long id, Model model){
        model.addAttribute("members",memberRepository.findAll());
        model.addAttribute("asset", assetRepository.findOne(id));
        model.addAttribute("assets", assetRepository.findAll());
        model.addAttribute("assetcount", assetRepository.count());
        model.addAttribute("trainingTypes", trainingTypeRepository.findAll());
        return "assets";
    }
    
    @RequestMapping(value="/assets/{id}", method=RequestMethod.GET, produces={"application/json"})
    @ResponseBody
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public Asset getAssetJson(@PathVariable Long id, Model model, SecurityContextHolderAwareRequestWrapper security){
        return assetRepository.findOne(id);
    }

    @RequestMapping(value = "/assets",method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public String assetsList(Model model){
        model.addAttribute("members",memberRepository.findAll());
        model.addAttribute("assets", assetRepository.findAll());
        model.addAttribute("assetcount", assetRepository.count());
        model.addAttribute("trainingTypes", trainingTypeRepository.findAll());
        return "assets";
    }
    
    @RequestMapping(value = "/assets", method = RequestMethod.POST)
    @ResponseBody
    @Secured({"ROLE_ADMIN"})
    public Long saveAsset(@RequestBody @Valid Asset asset) {
        assetRepository.save(asset);
        return asset.getId();
    }

    @RequestMapping(value = "/assets/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @Secured({"ROLE_ADMIN"})
    public String removeAsset(@PathVariable Long id){
        assetRepository.delete(id);
        return id.toString();
    }
}
