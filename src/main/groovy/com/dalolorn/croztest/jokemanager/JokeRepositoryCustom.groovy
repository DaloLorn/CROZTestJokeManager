package com.dalolorn.croztest.jokemanager

import org.springframework.data.domain.Pageable

interface JokeRepositoryCustom {
    List<Joke> findByCategoryName(String name)
    List<Joke> findByCategoryName(String name, Pageable pageable)
    List<Joke> findByCategoryNameIgnoreCase(String name)
    List<Joke> findByCategoryNameIgnoreCase(String name, Pageable pageable)
}