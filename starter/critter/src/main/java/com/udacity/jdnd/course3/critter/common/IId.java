package com.udacity.jdnd.course3.critter.common;

public interface IId {

    /**
     * Check if this is a proxy object
     * @return
     */
    boolean isProxy();

    long getId();

    void setId(long id);
}
