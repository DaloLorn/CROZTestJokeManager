package com.dalolorn.croztest.jokemanager

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

interface JokeRepository extends PagingAndSortingRepository<Joke, Integer>, JokeRepositoryCustom {
    List<Joke> findByCategory(Integer category)
    List<Joke> findByCategory(Integer category, Pageable pageable)
    @Query('SELECT j FROM Joke j ORDER BY j.likes - j.dislikes DESC')
    List<Joke> findAllOrderByNetLikes(Pageable pageable)
}