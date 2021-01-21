package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.common.AbstractRepository;
import com.udacity.jdnd.course3.critter.schedule.Schedule;
import com.udacity.jdnd.course3.critter.schedule.Schedule_;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.udacity.jdnd.course3.critter.common.Creature.NAME_COL;
import static com.udacity.jdnd.course3.critter.user.Employee.DAYS_AVAILABLE_COL;
import static com.udacity.jdnd.course3.critter.user.Employee.SKILLS_COL;

@Repository
@Transactional
public class EmployeeRepository extends AbstractRepository<Employee> {

    @PostConstruct
    private void init() {
        setEntityClass(Employee.class);
    }

    public Employee findById(long id) {
        return find(id);
    }

    public List<Employee> findByName(String name) {
        return find(NAME_COL, name);
    }

    public List<Employee> findBySkill(EmployeeSkill skill) {
        return findInCollection(SKILLS_COL, skill);
    }

    public List<Employee> findBySkills(List<EmployeeSkill> skills) {
        return findInCollection(SKILLS_COL, skills);
    }

    public List<Employee> findBySkills(Set<EmployeeSkill> skills) {
        return findInCollection(SKILLS_COL, skills);
    }

    public List<Employee> findByDaysAvailable(DayOfWeek day) {
        return findInCollection(DAYS_AVAILABLE_COL, day);
    }

    public List<Employee> findByDaysAvailable(List<DayOfWeek> days) {
        return findInCollection(DAYS_AVAILABLE_COL, days);
    }

    public List<Employee> findByDaysAvailable(Set<DayOfWeek> days) {
        return findInCollection(DAYS_AVAILABLE_COL, days);
    }


    public List<Employee> findEmployeesForService(EmployeeRequest employeeRequest) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Employee> query = builder.createQuery(entityClass);
        Root<Employee> root = query.from(entityClass);

        CriteriaQuery<Schedule> scheduleQuery = builder.createQuery(Schedule.class);
        Root<Schedule> scheduleRoot = scheduleQuery.from(Schedule.class);

        List<Employee> employeePool;

        // employees with required skills (must match all required skills)
        Predicate hasSkillsPredicate = builder.and(
                employeeRequest.getSkills().stream()
                        .map(s -> builder.isMember(s, root.get(Employee_.skills)))
                        .toArray(Predicate[]::new));

//        query.select(root).where(hasSkillsPredicate);
//        employeePool = entityManager.createQuery(query).getResultList();

        // employees who work required day of week
        Predicate workOnDayPredicate =
                builder.isMember(employeeRequest.getDate().getDayOfWeek(), root.get(Employee_.daysAvailable));

//        query.select(root).where(workOnDayPredicate);
//        employeePool = entityManager.createQuery(query).getResultList();

        // possible employees, who have skill and work on the day
        Predicate employeePoolPredicate = builder.and(workOnDayPredicate, hasSkillsPredicate);

        query.select(root).where(employeePoolPredicate);
        employeePool = entityManager.createQuery(query).getResultList();


        // schedules for the requested date
        Predicate daySchedulesPredicate =
            builder.equal(
                scheduleRoot.get(Schedule_.date), employeeRequest.getDate());

        scheduleQuery.select(scheduleRoot).where(daySchedulesPredicate);
        List<Schedule> scheduleList = entityManager.createQuery(scheduleQuery).getResultList();

        // employees in schedule for the requested date
        List<Employee> scheduledEmployees = scheduleList.stream()
                .flatMap(s -> s.getEmployees().stream())
                .distinct()
                .collect(Collectors.toList());


        List<Employee> possibleEmployees = employeePool.stream()
                .filter(e -> !scheduledEmployees.contains(e))
                .collect(Collectors.toList());

        return possibleEmployees;
    }

}
