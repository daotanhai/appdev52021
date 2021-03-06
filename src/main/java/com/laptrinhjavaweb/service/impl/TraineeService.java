package com.laptrinhjavaweb.service.impl;

import com.laptrinhjavaweb.controller.trainingStaff.AssignCourseForTraineeController;
import com.laptrinhjavaweb.converter.CourseConverter;
import com.laptrinhjavaweb.converter.TraineeConverter;
import com.laptrinhjavaweb.converter.TraineeCourseConverter;
import com.laptrinhjavaweb.dto.TraineeCourseDTO;
import com.laptrinhjavaweb.dto.TraineeDTO;
import com.laptrinhjavaweb.entity.*;
import com.laptrinhjavaweb.repository.*;
import com.laptrinhjavaweb.service.ITraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class TraineeService implements ITraineeService {
    @Autowired
    AssignCourseForTraineeController assignCourseForTraineeController;
    @Autowired
    ITraineeRepository ITraineeRepository;
    @Autowired
    TraineeConverter traineeConverter;
    @Autowired
    IRoleRepository IRoleRepository;
    @Autowired
    ICourseRepository ICourseRepository;
    @Autowired
    CourseConverter courseConverter;
    @Autowired
    ITraineeCourseRepository ITraineeCourseRepository;
    @Autowired
    TraineeCourseConverter traineeCourseConverter;
    @Autowired
    IUserRepository IUserRepository;

    private long traineeId;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public long getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(long traineeId) {
        this.traineeId = traineeId;
    }

    @Override
    public List<TraineeDTO> findAll(Pageable pageable) {
        List<TraineeDTO> models = new ArrayList<>();
        List<TraineeEntity> entities = ITraineeRepository.findAll(pageable).getContent();
        for (TraineeEntity items : entities) {
            TraineeDTO traineeDTO = traineeConverter.toDTO(items);
            models.add(traineeDTO);
        }
        return models;
    }

    @Override
    public TraineeDTO saveTrainee(TraineeDTO traineeDTO) {
        TraineeEntity traineeEntity = new TraineeEntity();
        UserEntity userEntity = new UserEntity();

        RoleEntity role = IRoleRepository.findOneByCode(traineeDTO.getRoleCode());
        // update neu id tu DTO khac null
        if (traineeDTO.getId() != null) {
            TraineeEntity oldTrainee = ITraineeRepository.findOne(traineeDTO.getId());
            UserEntity oldUser = IUserRepository.findUserEntityByUserName(oldTrainee.getUserName());
            // update trong bang User
            oldUser.setPassword(bCryptPasswordEncoder.encode(traineeDTO.getPassword()));
            oldUser.setFullName(traineeDTO.getName());
            oldUser.setUserName(traineeDTO.getUserName());
            IUserRepository.save(oldUser);
            // update trong bang trainee
            oldTrainee.setRoleTrainee(role);
            oldTrainee.setPassword(oldUser.getPassword());

            traineeEntity = traineeConverter.toEntity(oldTrainee, traineeDTO);
        }
        // them moi trainee - n???u username b??? tr??ng th?? ko th??m v??o
        if (traineeDTO.getId() == null && ITraineeRepository.findTraineeEntityByUserName(traineeEntity.getUserName())==null) {
            traineeEntity = traineeConverter.toEntity(traineeDTO);
            // set username + password cho table user -> co the dang nhap duoc
            userEntity.setUserName(traineeEntity.getUserName());
            userEntity.setPassword(bCryptPasswordEncoder.encode(traineeDTO.getPassword()));
            userEntity.setRoleEntity(role);
            userEntity.setFullName(traineeDTO.getName());
            userEntity.setStatus(traineeDTO.getStatus());

            IUserRepository.save(userEntity);
            traineeEntity.setRoleTrainee(role);
            // SET ri??ng password cho trainee t??? User Entity. N???u kh??ng, th?? s??? kh??ng ????ng nh???p ???????c v?? password set cho user
            // b??? set tr?????c. ??i???u n??y s??? l??m password kh??ng c??n kh???p n???a.
            // N??n m??nh s??? set ri??ng password cho user sau ???? get password t??? user v?? set cho trainee
            traineeEntity.setPassword(userEntity.getPassword());
        }
        return traineeConverter.toDTO(ITraineeRepository.save(traineeEntity));
    }

    @Override
    public List<TraineeCourseDTO> findCourseOnTraineeId(long id) {
        List<TraineeCourseEntity> traineeCourseEntities = ITraineeCourseRepository.findTraineeCourseEntitiesByTraineeEntity_Id(id);
        TraineeCourseDTO traineeCourseDTO = new TraineeCourseDTO();
        List<TraineeCourseDTO> models = new ArrayList<>();
        for (TraineeCourseEntity item : traineeCourseEntities) {
            traineeCourseDTO = traineeCourseConverter.toDTO(item);
            models.add(traineeCourseDTO);
        }
        return models;
    }

    @Override
    public void deleteTraineeCourse(long[] ids) {
        for (long id : ids) {
            TraineeCourseEntity traineeCourseEntity = ITraineeCourseRepository.findOne(id);
            ITraineeCourseRepository.delete(traineeCourseEntity);
        }
    }

    // truyen vao user id, tim ra trainee DTO
    @Override
    public TraineeDTO findTraineeByUserId(long id) {
        UserEntity userEntity = IUserRepository.findOne(id);
        TraineeEntity traineeEntity = ITraineeRepository.findTraineeEntityByUserName(userEntity.getUserName());
        return traineeConverter.toDTO(traineeEntity);
    }

    @Override
    @Transactional
    public void deleteTrainee(long[] ids) {
        for (long id : ids) {
            ITraineeRepository.delete(id);
        }
    }

    @Override
    public int getTotalTrainees() {
        // count() do JpaRepository cung c???p
        return (int) ITraineeRepository.count();
    }

    @Override
    public void saveCourseAssign(long[] ids) {
        // Trong m???ng g???i v???, th?? ph???n t??? ?????u ti??n c???a ids l?? id c???a trainee
        long traineeId = ids[0];
        // i b???t ?????u = 1 v?? ph???i b??? qua gi?? tr??? c???a traineeId trong m???ng
        for (int i = 1; i < ids.length; i++) {
            long courseIdOld = ids[i];
            if (ITraineeCourseRepository.findTraineeCourseEntitiesByTraineeEntity_IdAndCourseEntityForTrainee_Id(traineeId,courseIdOld).size()==0){
                TraineeCourseEntity traineeCourseEntity = new TraineeCourseEntity();
                TraineeEntity traineeEntity = ITraineeRepository.findOne(traineeId);

                // T??m courseEntity d???a tr??n courseIdOle
                CourseEntity courseEntity = ICourseRepository.findOne(courseIdOld);
                // set trainee
                traineeCourseEntity.setTraineeEntity(traineeEntity);
                // set course
                traineeCourseEntity.setCourseEntityForTrainee(courseEntity);
                // set xong th?? l??u
                ITraineeCourseRepository.save(traineeCourseEntity);
            }

        }
        //traineeRepository.saveUser(traineeEntity);
    }

    @Override
    public TraineeDTO findById(Long id) {
        TraineeEntity traineeEntity = ITraineeRepository.findOne(id);
        return traineeConverter.toDTO(traineeEntity);
    }

    @Override
    public List<TraineeDTO> findAll() {
        List<TraineeEntity> entities = ITraineeRepository.findAll();
        List<TraineeDTO> models = new ArrayList<>();
        for (TraineeEntity item : entities) {
            models.add(traineeConverter.toDTO(item));
        }
        return models;
    }

    @Override
    public void saveTraineeEntity(TraineeEntity traineeEntity) {
        this.ITraineeRepository.save(traineeEntity);
    }


}
