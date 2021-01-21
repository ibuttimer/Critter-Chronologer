package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.common.AbstractRepository;
import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.Pet_;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.*;
import java.util.List;

import static com.udacity.jdnd.course3.critter.common.Creature.NAME_COL;
import static com.udacity.jdnd.course3.critter.user.Customer.PHONE_NUMBER_COL;

@Repository
@Transactional
public class CustomerRepository extends AbstractRepository<Customer> {

    @PostConstruct
    private void init() {
        setEntityClass(Customer.class);
    }

    public Customer findById(long id) {
        return super.find(id);
    }

    public List<Customer> findByName(String name) {
        return super.find(NAME_COL, name);
    }

    public List<Customer> findByPhoneNumber(String phoneNumber) {
        return super.find(PHONE_NUMBER_COL, phoneNumber);
    }


    /**
     * Find by pet
     * @param petId - id of pet
     * @return
     * See <a href="https://javaee.github.io/tutorial/persistence-criteria003.html">Using the Criteria API and Metamodel API to Create Basic Typesafe Queries</a>
     */
    public Customer findOwnerByPet(long petId) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Customer> query = builder.createQuery(entityClass);
        Root<Customer> root = query.from(entityClass);
        Join<Customer, Pet> pets = root.join(Customer_.pets);

        query.select(root)
                .where(
                    builder.equal(pets.get(Pet_.id), petId)
                );

        return entityManager.createQuery(query).getSingleResult();
    }
}
