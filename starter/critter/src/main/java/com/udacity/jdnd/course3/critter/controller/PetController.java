package com.udacity.jdnd.course3.critter.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import com.udacity.jdnd.course3.critter.common.Views;
import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.user.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.udacity.jdnd.course3.critter.common.Creature.NAME_COL;
import static com.udacity.jdnd.course3.critter.config.Config.*;
import static com.udacity.jdnd.course3.critter.pet.Pet.*;

/**
 * Handles web requests related to Pets.
 */
@RestController
@RequestMapping(PET_URL)
public class PetController extends AbstractController<Pet, PetDTO> {

    @Autowired
    PetService petService;

    @PostConstruct
    void init() {
        setService(petService);
    }

    @JsonView(Views.Public.class)
    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) {
        return save(petDTO);
    }

    @JsonView(Views.Public.class)
    @PutMapping(PET_ID_URL)
    public PetDTO updatePet(@RequestBody PetDTO petDTO, @PathVariable long petId) {
        return update(petDTO, petId);
    }

    @JsonView(Views.Public.class)
    @GetMapping(PET_ID_URL)
    public PetDTO getPet(@PathVariable long petId) {
        return get(petId);
    }

    @DeleteMapping(PET_ID_URL)
    public int deletePet(@PathVariable long petId) {
        return delete(petId);
    }

    @JsonView(Views.Public.class)
    @GetMapping
    public List<PetDTO> getPets(){
        return getAll();
    }

    @JsonView(Views.Public.class)
    @GetMapping(PET_GET_BY_OWNER_URL)
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {
        return convertEntityToDto(
                petService.getPetsForOwner(ownerId));
    }


    @Override
    protected Pet getEntityInstance() {
        return new Pet();
    }

    @Override
    protected PetDTO getDtoInstance() {
        return new PetDTO();
    }

    @Override
    public Pet convertDtoToEntity(PetDTO dto) {
        // convert id to owner
        return dtoToEntityTranslate(dto, super.convertDtoToEntity(dto));
    }

    @Override
    public PetDTO convertEntityToDto(Pet entity) {
        PetDTO dto = super.convertEntityToDto(entity);
        // convert owner to id
        Customer owner = entity.getOwner();
        if (owner != null) {
            dto.setOwnerId(
                owner.getId()
            );
        }
        return dto;
    }

    @Override
    protected Pet copyUpdateProperties(PetDTO dto, Pet entity) {
        super.copyUpdateProperties(dto, entity);
        return dtoToEntityTranslate(dto, entity);
    }

    protected Pet dtoToEntityTranslate(PetDTO dto, Pet entity) {
        // convert owner id to owner proxy
        if (dto.getOwnerId() != 0) {
            entity.setOwner(
                Customer.proxy(dto.getOwnerId())
            );
        }
        return entity;
    }

    @Override
    public List<String> getUpdatePropertiesToIgnore(PetDTO dto) {
        List<String> list = Lists.newArrayList(super.getUpdatePropertiesToIgnore(dto));
        if (dto.getType() == null) {
            list.add(TYPE_COL);
        }
        if (dto.getName() == null) {
            list.add(NAME_COL);
        }
        if (dto.getBirthDate() == null) {
            list.add(BIRTH_DATE_COL);
        }
        if (dto.getNotes() == null) {
            list.add(NOTES_COL);
        }
        return list;
    }

}
