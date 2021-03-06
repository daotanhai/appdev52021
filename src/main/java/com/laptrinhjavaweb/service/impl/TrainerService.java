package com.laptrinhjavaweb.service.impl;

import com.laptrinhjavaweb.converter.TrainerConverter;
import com.laptrinhjavaweb.converter.TrainerCourseConverter;
import com.laptrinhjavaweb.dto.TrainerCourseDTO;
import com.laptrinhjavaweb.dto.TrainerDTO;
import com.laptrinhjavaweb.dto.TrainerForTrainingStaffDTO;
import com.laptrinhjavaweb.entity.*;
import com.laptrinhjavaweb.repository.*;
import com.laptrinhjavaweb.service.ITrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Dùng cho Trainer và Training staff, chỉ update các fields cơ bản của trainer
// Không liên quan tới các table khác
@Service
public class TrainerService implements ITrainerService {
    @Autowired
    ITrainerRepository ITrainerRepository;

    @Autowired
    TrainerConverter trainerConverter;

    @Autowired
    ICourseRepository ICourseRepository;

    @Autowired
    ITrainerCourseRepository ITrainerCourseRepository;

    @Autowired
    TrainerCourseConverter trainerCourseConverter;

    @Autowired
    IRoleRepository IRoleRepository;

    @Autowired
    IUserRepository IUserRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public List<TrainerDTO> findAll(Pageable pageable) {
        List<TrainerDTO> models = new ArrayList<TrainerDTO>();
        List<TrainerEntity> entities = ITrainerRepository.findAll(pageable).getContent();
        for (TrainerEntity item : entities) {
            TrainerDTO trainerDTO = trainerConverter.toDTO(item);
            models.add(trainerDTO);
        }
        return models;
    }

    @Override
    public TrainerDTO findById(Long id) {
        return trainerConverter.toDTO(ITrainerRepository.findOne(id));
    }

    @Override
    @Transactional
    public TrainerDTO saveTrainerForTrainer(TrainerDTO trainerDTO) {
        TrainerEntity trainerEntity = new TrainerEntity();
        UserEntity userEntity = new UserEntity();
        RoleEntity role = IRoleRepository.findOneByCode(trainerDTO.getRoleCode());
        if (trainerDTO.getId() != null) {
            TrainerEntity oldTrainer = ITrainerRepository.findOne(trainerDTO.getId());
            UserEntity oldUser = IUserRepository.findUserEntityByUserName(oldTrainer.getUserName());
            // update trong bang user
            oldUser.setPassword(bCryptPasswordEncoder.encode(trainerDTO.getPassWord()));
            oldUser.setFullName(trainerDTO.getName());
            oldUser.setUserName(trainerDTO.getUserName());
            IUserRepository.save(oldUser);
            // update trong bang trainer
            oldTrainer.setRoleTrainer(role);
            oldTrainer.setPassword(oldUser.getPassword());

            trainerEntity = trainerConverter.toEntity(oldTrainer, trainerDTO);
        }
        if (trainerDTO.getId() == null) {
            trainerEntity = trainerConverter.toEntity(trainerDTO);
            userEntity.setUserName(trainerEntity.getUserName());
            userEntity.setPassword(bCryptPasswordEncoder.encode(trainerDTO.getPassWord()));
            userEntity.setFullName(trainerDTO.getName());
            userEntity.setStatus(trainerDTO.getStatus());

            IUserRepository.save(userEntity);
            trainerEntity.setRoleTrainer(role);
            trainerEntity.setPassword(userEntity.getPassword());
        }
        return trainerConverter.toDTO(ITrainerRepository.save(trainerEntity));
    }

    @Override
    public TrainerForTrainingStaffDTO saveTrainerForTrainingStaff(TrainerForTrainingStaffDTO trainerForTrainingStaffDTO) {
        TrainerEntity oldTrainer = new TrainerEntity();
        oldTrainer = ITrainerRepository.findOne(trainerForTrainingStaffDTO.getId());
        oldTrainer.setName(trainerForTrainingStaffDTO.getName());
        oldTrainer.setWorkingPlace(trainerForTrainingStaffDTO.getWorkingPlace());
        oldTrainer.setEducation(trainerForTrainingStaffDTO.getEducation());
        oldTrainer.setTelephone(trainerForTrainingStaffDTO.getTelephone());
        oldTrainer.setEmail(trainerForTrainingStaffDTO.getEmail());
        oldTrainer.setExternalOrInternal(trainerForTrainingStaffDTO.getExternalOrInternal());
        ITrainerRepository.save(oldTrainer);
        TrainerForTrainingStaffDTO trainerTS = new TrainerForTrainingStaffDTO();
        trainerTS.setId(oldTrainer.getId());
        trainerTS.setName(oldTrainer.getName());
        trainerTS.setWorkingPlace(oldTrainer.getWorkingPlace());
        trainerTS.setEducation(oldTrainer.getEducation());
        trainerTS.setTelephone(oldTrainer.getTelephone());
        trainerTS.setEmail(oldTrainer.getEmail());
        trainerTS.setExternalOrInternal(oldTrainer.getExternalOrInternal());
        return trainerTS;
    }

    @Override
    public int getTotalTrainers() {
        return (int) ITrainerRepository.count();
    }

    @Override
    public Map<String, String> findAll() {
        Map<String, String> result = new HashMap<String, String>();
        List<TrainerEntity> entities = ITrainerRepository.findAll();
        for (TrainerEntity item : entities) {
            result.put(item.getName(), item.getName());
        }
        return result;
    }

    @Override
    public void deleteTrainer(long[] ids) {
        for (long id : ids) {
            ITrainerRepository.delete(id);
        }
    }
    // gan khoa hoc vao trainer
    @Override
    @Transactional
    public void saveCourseAssign(long[] ids) {
        long trainerId = ids[0];
        for (int i = 1; i < ids.length; i++) {
            long courseIdOld = ids[i];
            // tìm thử trong database đã tồn tại cặp trainer id và course id chưa?
            if (ITrainerCourseRepository.findTrainerCourseEntitiesByTrainerEntity_IdAndCourseEntityForTrainer_Id(trainerId,courseIdOld).size()==0){
                TrainerCourseEntity trainerCourseEntity = new TrainerCourseEntity();
                TrainerEntity trainerEntity = ITrainerRepository.findOne(trainerId);
                CourseEntity courseEntity = ICourseRepository.findOne(courseIdOld);
                trainerCourseEntity.setTrainerEntity(trainerEntity);
                trainerCourseEntity.setCourseEntityForTrainer(courseEntity);
                ITrainerCourseRepository.save(trainerCourseEntity);
            }
        }
    }

    @Override
    public List<TrainerCourseDTO> findCourseOnTrainerId(long id) {
        List<TrainerCourseEntity> trainerCourseEntities = ITrainerCourseRepository.findTrainerCourseEntitiesByTrainerEntity_Id(id);
        TrainerCourseDTO trainerCourseDTO;
        List<TrainerCourseDTO> models = new ArrayList<>();
        for (TrainerCourseEntity item : trainerCourseEntities) {
            trainerCourseDTO = trainerCourseConverter.toDTO(item);
            models.add(trainerCourseDTO);
        }
        return models;
    }

    @Override
    public void deleteTrainerCourse(long[] ids) {
        for (long id : ids) {
            TrainerCourseEntity trainerCourseEntity = ITrainerCourseRepository.findOne(id);
            ITrainerCourseRepository.delete(trainerCourseEntity);
            // trainerCourseRepository.delete(id);
        }
    }
}
