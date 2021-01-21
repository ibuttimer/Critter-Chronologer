package com.udacity.jdnd.course3.critter.common;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Base class for all entities
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Creature implements ICreature, IEntity {

    public static final String ID_COL = "id";
    public static final String NAME_COL = "name";
    public static final String PROXY_COL = "proxy";

    @Id
    @JsonView(Views.Public.class)
    @Column(name = ID_COL)
    @GeneratedValue
    long id;

    @NotNull
    @JsonView(Views.Public.class)
    @Column(name = NAME_COL)
    @Nationalized
    String name;

    @JsonView(Views.Internal.class)
    @Transient
    protected boolean proxy;

    public Creature() {
        proxy = false;
    }

    public Creature(long id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    public static Creature of() {
        return new Creature();
    }

    public static Creature proxy(long id) {
        Creature creature = new Creature();
        creature.proxy = true;
        creature.setId(id);
        return creature;
    }

    public static Creature of(long id, String name) {
        return new Creature(id, name);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isProxy() {
        return proxy;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", proxy=" + proxy +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Creature)) return false;

        Creature creature = (Creature) o;

        if (getId() != creature.getId()) return false;
        if (isProxy() != creature.isProxy()) return false;
        return getName() != null ? getName().equals(creature.getName()) : creature.getName() == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (isProxy() ? 1 : 0);
        return result;
    }
}
