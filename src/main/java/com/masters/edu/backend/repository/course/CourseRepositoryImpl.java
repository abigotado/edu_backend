package com.masters.edu.backend.repository.course;

import java.util.List;

import com.masters.edu.backend.domain.course.Course;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CourseRepositoryImpl implements CourseRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Course> search(String text, Pageable pageable) {
        var query = entityManager.createQuery("select c from Course c where lower(c.title) like lower(:text) or lower(c.summary) like lower(:text)", Course.class);
        query.setParameter("text", "%" + text + "%");
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        var countQuery = entityManager.createQuery("select count(c) from Course c where lower(c.title) like lower(:text) or lower(c.summary) like lower(:text)", Long.class);
        countQuery.setParameter("text", "%" + text + "%");

        var content = query.getResultList();
        var total = countQuery.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<Course> findByTeacherWithModules(Long teacherId) {
        return entityManager.createQuery(
                "select distinct c from Course c " +
                        "left join fetch c.modules m " +
                        "left join fetch m.lessons " +
                        "where c.teacher.id = :teacherId", Course.class)
                .setParameter("teacherId", teacherId)
                .getResultList();
    }
}


