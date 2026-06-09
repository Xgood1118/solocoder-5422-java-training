package com.company.training.course.repository;

import com.company.training.common.enums.CourseStatus;
import com.company.training.common.enums.CourseType;
import com.company.training.course.entity.Course;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CourseRepository {

    private final ConcurrentHashMap<String, Course> storage = new ConcurrentHashMap<>();

    public Course save(Course course) {
        storage.put(course.getId(), course);
        return course;
    }

    public Course findById(String id) {
        return storage.get(id);
    }

    public Collection<Course> findAll() {
        return storage.values();
    }

    public List<Course> findByCourseType(CourseType courseType) {
        List<Course> result = new ArrayList<>();
        for (Course c : storage.values()) {
            if (c.getCourseType() == courseType) {
                result.add(c);
            }
        }
        return result;
    }

    public List<Course> findByDepartmentId(String departmentId) {
        List<Course> result = new ArrayList<>();
        for (Course c : storage.values()) {
            if (departmentId.equals(c.getDepartmentId())) {
                result.add(c);
            }
        }
        return result;
    }

    public List<Course> findByStatus(CourseStatus status) {
        List<Course> result = new ArrayList<>();
        for (Course c : storage.values()) {
            if (c.getStatus() == status) {
                result.add(c);
            }
        }
        return result;
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    public void deleteById(String id) {
        storage.remove(id);
    }
}
