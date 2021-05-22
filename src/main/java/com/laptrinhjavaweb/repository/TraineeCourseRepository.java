package com.laptrinhjavaweb.repository;

import com.laptrinhjavaweb.entity.TraineeCourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TraineeCourseRepository extends JpaRepository<TraineeCourseEntity, Long> {
    List<TraineeCourseEntity> findTraineeCourseEntitiesByTraineeEntity_Id(long id);
}