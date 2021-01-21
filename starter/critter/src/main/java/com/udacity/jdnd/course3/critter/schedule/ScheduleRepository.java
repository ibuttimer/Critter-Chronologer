package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.common.AbstractRepository;
import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.Pet_;
import com.udacity.jdnd.course3.critter.user.Employee;
import com.udacity.jdnd.course3.critter.user.Employee_;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.List;

import static com.udacity.jdnd.course3.critter.schedule.Schedule.*;

@Repository
@Transactional
public class ScheduleRepository extends AbstractRepository<Schedule> {

    @PostConstruct
    private void init() {
        setEntityClass(Schedule.class);
    }

    public Schedule findById(long id) {
        return find(id);
    }

    public List<Schedule> findByDate(LocalDate date) {
        return find(DATE_COL, date);
    }

    public List<Schedule> findByEmployee(Employee employee) {
        return findInCollection(EMPLOYEES_COL, employee);
    }

    public List<Schedule> findByPet(Pet pet) {
        return findInCollection(PETS_COL, pet);
    }

    /**
     * Find by pet
     * @param petId - id of pet
     * @return
     */
    public List<Schedule> findScheduleByPet(long petId) {
        return findScheduleByPets(List.of(petId));
    }

    /**
     * Find by pet ids
     * @param petIds - ids of pets
     * @return
     */
    public List<Schedule> findScheduleByPets(List<Long> petIds) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Schedule> query = builder.createQuery(entityClass);
        Root<Schedule> root = query.from(entityClass);
        Join<Schedule, Pet> pets = root.join(Schedule_.pets);

        query.select(root)
                .where(
                        pets.get(Pet_.id).in(petIds)
                );

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Find by employee
     * @param employeeId - id of employee
     * @return
     */
    public List<Schedule> findScheduleByEmployee(long employeeId) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Schedule> query = builder.createQuery(entityClass);
        Root<Schedule> root = query.from(entityClass);
        Join<Schedule, Employee> employees = root.join(Schedule_.employees);

        query.select(root)
                .where(
                        employees.get(Employee_.id).in(employeeId)
                );

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Find by customer
     * @param customerId - id of customer
     * @return
     */
    public List<Schedule> findScheduleByCustomer(long customerId) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Schedule> query = builder.createQuery(entityClass);
        Root<Schedule> root = query.from(entityClass);
        Join<Schedule, Pet> pets = root.join(Schedule_.pets);

        query.select(root)
                .where(
                    builder.equal(
                            pets.get(Pet_.owner), customerId)
                )
                .distinct(true);    // in case multiple pets have same schedule

        return entityManager.createQuery(query).getResultList();
    }

}
