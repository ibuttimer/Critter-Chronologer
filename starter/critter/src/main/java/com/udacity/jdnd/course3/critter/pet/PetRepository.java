package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.common.AbstractRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Repository
@Transactional
public class PetRepository extends AbstractRepository<Pet> {

    @PostConstruct
    private void init() {
        setEntityClass(Pet.class);
    }

}
