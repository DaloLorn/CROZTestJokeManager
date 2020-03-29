package com.dalolorn.croztest.jokemanager

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * JPA entity class for categories.
 */
@Entity
class Category {
    @SuppressWarnings("GrFinalVariableAccess")
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
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
