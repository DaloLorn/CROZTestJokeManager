package com.dalolorn.croztest.jokemanager

import org.springframework.data.domain.Pageable

/**
 * Custom interface segment for the JPA joke repository.
 */
interface JokeRepositoryCustom {
    /**
     * Gets all jokes for a given joke category, identified by its name.
     * @param name The name of the joke category.
     * @return List of all matching jokes.
     */
    List<Joke> findByCategoryName(String name)

    /**
     * Gets a subset of all jokes for a given joke category, identified by its name.
     * @param name The name of the joke category.
     * @param pageable Specifies the start and end points of the subset.
     * @return List of all matching jokes.
     */
    List<Joke> findByCategoryName(String name, Pageable pageable)


    /**
     * Gets all jokes for a given joke category, identified by its name. Case-insensitive.
     * @param name The name of the joke category.
     * @return List of all matching jokes.
     */
    List<Joke> findByCategoryNameIgnoreCase(String name)

    /**
     * Gets a subset of all jokes for a given joke category, identified by its name. Case-insensitive.
     * @param name The name of the joke category.
     * @param pageable Specifies the start and end points of the subset.
     * @return List of all matching jokes.
     */
    List<Joke> findByCategoryNameIgnoreCase(String name, Pageable pageable)
}