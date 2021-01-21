package com.udacity.jdnd.course3.critter.common;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Creature.class)
public class Creature_ {

    public static volatile SingularAttribute<Creature, Long> id;
    public static volatile SingularAttribute<Creature, String> name;
}
