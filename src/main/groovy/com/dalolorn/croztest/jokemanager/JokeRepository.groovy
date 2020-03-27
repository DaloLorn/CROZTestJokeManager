package com.dalolorn.croztest.jokemanager

import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository

interface JokeRepository extends PagingAndSortingRepository<Joke, Integer>, JokeRepositoryCustom {
    List<Joke> findByCategory(Integer category)
    List<Joke> findByCategory(Integer category, Pageable pageable)
}