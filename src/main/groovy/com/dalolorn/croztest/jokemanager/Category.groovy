package com.dalolorn.croztest.jokemanager

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Category {
    @SuppressWarnings("GrFinalVariableAccess")
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    final int id
    String name

    protected Category() {}

    Category(String name) {
        this.name = name
    }

    @Override
    String toString() {
        "Category[id=${id}, name='${name}']"
    }
}
