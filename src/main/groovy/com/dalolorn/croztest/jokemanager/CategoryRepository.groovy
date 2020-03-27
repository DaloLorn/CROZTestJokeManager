package com.dalolorn.croztest.jokemanager

import org.springframework.data.repository.CrudRepository

interface CategoryRepository extends CrudRepository<Category, Integer> {
    Optional<Category> findByName(String name)
    Optional<Category> findByNameIgnoreCase(String name)
}