package com.dalolorn.croztest.jokemanager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.Pageable

class JokeRepositoryCustomImpl implements JokeRepositoryCustom {
    @Autowired
    @Lazy
    private JokeRepository jokes
    @Autowired
    private CategoryRepository categories

    @Override
    List<Joke> findByCategoryName(String name) {
        Optional<Category> category = categories.findByName(name)
        category.isPresent() ? jokes.findByCategory(category.get().id) : []
    }

    @Override
    List<Joke> findByCategoryName(String name, Pageable pageable) {
        Optional<Category> category = categories.findByName(name)
        category.isPresent() ? jokes.findByCategory(category.get().id, pageable) : []
    }

    @Override
    List<Joke> findByCategoryNameIgnoreCase(String name) {
        Optional<Category> category = categories.findByNameIgnoreCase(name)
        category.isPresent() ? jokes.findByCategory(category.get().id) : []
    }

    @Override
    List<Joke> findByCategoryNameIgnoreCase(String name, Pageable pageable) {
        Optional<Category> category = categories.findByNameIgnoreCase(name)
        category.isPresent() ? jokes.findByCategory(category.get().id, pageable) : []
    }
}
