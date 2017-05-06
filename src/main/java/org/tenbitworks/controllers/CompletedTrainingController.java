package org.tenbitworks.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tenbitworks.model.CompletedTraining;
import org.tenbitworks.model.Member;
import org.tenbitworks.model.Training;
import org.tenbitworks.repositories.CompletedTrainingRepository;
import org.tenbitworks.repositories.MemberRepository;
import org.tenbitworks.repositories.TrainingRepository;

@Controller
public class CompletedTrainingController {

    @Autowired
    CompletedTrainingRepository completedTrainingRepository;

    @Autowired
    TrainingRepository trainingRepository;

    @Autowired
    MemberRepository memberRepository;


    @RequestMapping("/completedtraining/{id}")
    public String product(@PathVariable Long id, Model model) {
        model.addAttribute("completedtraining", completedTrainingRepository.findOne(id));
        return "completedtraining";
    }

    @RequestMapping("/getcompletedtraining")
    @ResponseBody
    public CompletedTraining getCompletedtraining(@RequestParam Long Id, Model model){
        CompletedTraining completedTraining = completedTrainingRepository.findOne(Id);
        return completedTraining;
    }

    @RequestMapping(value = "/completedtrainings", method = RequestMethod.GET)
    public String trainingsList(Model model) {
        Iterable<Training> trainingIterable =  trainingRepository.findAll();
        Iterable<Member> memberIterable =  memberRepository.findAll();
        Iterable<CompletedTraining> completedTrainingIterable =  completedTrainingRepository.findAll();
        //TODO replace this with a Join SQL for performance
        for(CompletedTraining completedTraining : completedTrainingIterable){
            UUID memberId = completedTraining.getMemberId();
            long trainingId = completedTraining.getTrainingId();
            for(Member member : memberIterable){
                UUID id = member.getId();
                if(memberId == id){
                    completedTraining.setMemberName(member.getMemberName());
                    break;
                }
            }

            for(Training training : trainingIterable){
                long id = training.getId();
                if(trainingId == id){
                    completedTraining.setTrainingTitle(training.getTitle());
                    break;
                }
            }
        }
        model.addAttribute("trainings", trainingIterable);
        model.addAttribute("members",memberIterable);
        model.addAttribute("completedtrainings", completedTrainingRepository.findAll());

        return "completedtrainings";
    }

    @RequestMapping(value = "/savecompletedtraining", method = RequestMethod.POST)
    @ResponseBody
    public Long saveTraining(@RequestBody CompletedTraining completedTraining) {
        completedTrainingRepository.save(completedTraining);
        return completedTraining.getId();
    }

    @RequestMapping(value = "/removecompletedtraining", method = RequestMethod.POST)
    @ResponseBody
    public String removeMemeber(@RequestParam Long Id) {
        completedTrainingRepository.delete(Id);
        return Id.toString();
    }


}
