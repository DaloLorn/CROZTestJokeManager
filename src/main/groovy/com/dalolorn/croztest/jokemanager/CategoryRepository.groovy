package com.dalolorn.croztest.jokemanager

import org.springframework.data.repository.CrudRepository

/**
 * JPA repository definition for categories.
 */
interface CategoryRepository extends CrudRepository<Category, Integer> {
    Optional<Category> findByName(String name)
    Optional<Category> findByNameIgnoreCase(String name)
}