package com.laptrinhjavaweb.controller.trainee;

import com.laptrinhjavaweb.converter.TraineeConverter;
import com.laptrinhjavaweb.dto.TraineeDTO;
import com.laptrinhjavaweb.entity.TraineeEntity;
import com.laptrinhjavaweb.entity.UserEntity;
import com.laptrinhjavaweb.repository.ITraineeRepository;
import com.laptrinhjavaweb.repository.IUserRepository;
import com.laptrinhjavaweb.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller(value = "traineeProfileController")
public class ProfileController {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    IUserRepository IUserRepository;

    @Autowired
    ITraineeRepository ITraineeRepository;

    @Autowired
    TraineeConverter traineeConverter;

    @RequestMapping(value = "/trainee/profile/view", method = RequestMethod.GET)
    public ModelAndView profileView() {
        ModelAndView mav = new ModelAndView("trainee/view-profile");
        UserEntity userEntity = new UserEntity();
        TraineeEntity traineeEntity = new TraineeEntity();
        long id = customUserDetailsService.getUserId();
        userEntity = IUserRepository.findOne(id);
        traineeEntity = ITraineeRepository.findTraineeEntityByUserName(userEntity.getUserName());
        TraineeDTO traineeDTO = traineeConverter.toDTO(traineeEntity);
        mav.addObject("model", traineeDTO);
        return mav;
    }
}
