package com.snappay.taxforecaster.controller.model;

public class AbstractModel<T> {

    private T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }
}
