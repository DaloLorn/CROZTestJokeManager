package com.dalolorn.croztest.jokemanager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.Pageable

/**
 * Implementation for the custom segment of the JPA joke repository. (In hindsight, I could have just used a bunch more @Query annotations and skipped this... but hey, showing off! That's nice too.)
 */
class JokeRepositoryCustomImpl implements JokeRepositoryCustom {
    /** Reference to the joke repository. */
    @Autowired
    @Lazy
    private JokeRepository jokes
    /** We need the category repository to look up the category by name. */
    @Autowired
    private CategoryRepository categories

    @Override
    List<Joke> findByCategoryName(String name) {
        Optional<Category> category = categories.findByName(name)
        category.isPresent() ? jokes.findByCategory(category.get().id) : [] // If we found nothing, the default behavior seems to be to return an empty list. We should follow suit.
    }

    @Override
    List<Joke> findByCategoryName(String name, Pageable pageable) {
        Optional<Category> category = categories.findByName(name)
        category.isPresent() ? jokes.findByCategory(category.get().id, pageable) : [] // If we found nothing, the default behavior seems to be to return an empty list. We should follow suit.
    }

    @Override
    List<Joke> findByCategoryNameIgnoreCase(String name) {
        Optional<Category> category = categories.findByNameIgnoreCase(name)
        category.isPresent() ? jokes.findByCategory(category.get().id) : [] // If we found nothing, the default behavior seems to be to return an empty list. We should follow suit.
    }

    @Override
    List<Joke> findByCategoryNameIgnoreCase(String name, Pageable pageable) {
        Optional<Category> category = categories.findByNameIgnoreCase(name)
        category.isPresent() ? jokes.findByCategory(category.get().id, pageable) : [] // If we found nothing, the default behavior seems to be to return an empty list. We should follow suit.
    }
}
