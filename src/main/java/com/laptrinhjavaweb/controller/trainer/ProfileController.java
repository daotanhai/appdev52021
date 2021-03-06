package com.laptrinhjavaweb.controller.trainer;

import com.laptrinhjavaweb.converter.TrainerConverter;
import com.laptrinhjavaweb.dto.TrainerDTO;
import com.laptrinhjavaweb.entity.TrainerEntity;
import com.laptrinhjavaweb.entity.UserEntity;
import com.laptrinhjavaweb.repository.ITrainerRepository;
import com.laptrinhjavaweb.repository.IUserRepository;
import com.laptrinhjavaweb.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller(value = "trainerProfileController")
public class ProfileController {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    IUserRepository IUserRepository;

    @Autowired
    ITrainerRepository ITrainerRepository;

    @Autowired
    TrainerConverter trainerConverter;

    @RequestMapping(value = "/trainer/profile/view", method = RequestMethod.GET)
    public ModelAndView profileView() {
        ModelAndView mav = new ModelAndView("trainer/view-profile");
        UserEntity userEntity = new UserEntity();
        TrainerEntity trainerEntity = new TrainerEntity();
        long id = customUserDetailsService.getUserId();
        userEntity = IUserRepository.findOne(id);
        trainerEntity = ITrainerRepository.findTrainerEntityByUserName(userEntity.getUserName());
        TrainerDTO trainerDTO = trainerConverter.toDTO(trainerEntity);
        mav.addObject("model", trainerDTO);
        return mav;
    }
}
