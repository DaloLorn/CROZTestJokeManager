package com.dalolorn.croztest.jokemanager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

import javax.validation.Valid

/**
 * Spring MVC controller class for the joke manager.
 */
@Controller
class JokeManagerController {
    /** Controls database access to the joke categories. */
    @Autowired
    CategoryRepository categories

    /** Controls database access to the jokes. */
    @Autowired
    JokeRepository jokes

    /**
     * Spring MVC handler for GET requests to '/new'. (Also known as the 'Create New Joke' screen.)
     * @param model
     * @return The joke editor.
     */
    @GetMapping('/new')
    String newJoke(Model model) {
        model.addAttribute('joke', new JokeForm())
        model.addAttribute('categories', categories.findAll().collect {it.name})
        return 'newjoke'
    }

    /**
     * Spring MVC handler for POST requests to '/new'. (Handles joke submission from 'Create New Joke'.)
     * @param joke Form data for the joke being submitted.
     * @param bindingResult Binding result for the joke data.
     * @param model
     * @return If the joke is invalid, refreshes the joke editor, writing errors to the page. Otherwise, redirects back to the joke list.
     */
    @PostMapping('/new')
    String newJokeSubmit(@Valid @ModelAttribute('joke') JokeForm joke, BindingResult bindingResult, Model model) {
        // Check for errors.
        if(bindingResult.hasErrors()) {
            // 'newjoke' needs to know what to put into the category datalist, so let's generate a fresh list.
            model.addAttribute('categories', categories.findAll().collect{it.name})
            return 'newjoke'
        }

        // Get the joke category, creating a new one if necessary.
        // This is needed to populate the category ID on the newly-created joke.
        // (I opted for case-insensitive category names, just because.)
        Category category
        Optional<Category> categoryOptional = categories.findByNameIgnoreCase(joke.category)
        if(categoryOptional.isPresent()) category = categoryOptional.get()
        else {
            category = new Category(joke.category)
            categories.save(category)
        }

        // Save the joke and send us back to the joke list.
        jokes.save(new Joke(category.id, 0, 0, joke.content))
        return 'redirect:/'
    }

    /**
     * Spring MVC handler for GET requests to '/'. (Also known as the 'Joke List' screen.)
     * @param page The page to navigate to.
     * @param length How many jokes to show per page.
     * @param model
     * @return The joke list at the page uniquely identified by 'page' and 'length'.
     */
    @GetMapping('/')
    String jokeList(@RequestParam(name='page', required=false, defaultValue= '1') String page, @RequestParam(name='length', required=false, defaultValue= '10') String length, Model model) {
        // Calculate data for the page request.
        int pageNum, pageLength
        pageNum = Math.max(page as Integer ?: 1, 1)
        pageLength = Math.max(length as Integer ?: 10, 1)

        // Fetch page.
        model.addAttribute(
                'jokes',
                jokes.findAllOrderByNetLikes(PageRequest.of(pageNum - 1, pageLength))
        )

        // We'll need to be able to get a category name based on the ID stored in a joke.
        model.addAttribute('categories', categories.findAll().collectEntries{ [it.id, it.name]})

        // Let's make the indexing persist across pages, shall we?
        // ... This will backfire somewhat if I was actually supposed to show joke IDs in that field...
        model.addAttribute('startingIndex', 1 + pageLength * (pageNum-1))

        // We'll need a page count in order to generate navigation to all the pages.
        model.addAttribute('pageCount', Math.ceil(jokes.count() / pageLength))
        return 'jokelist'
    }

    /**
     * Spring MVC handler for GET requests to 'like'. (Handles the like button.)
     * @param id The joke ID to like.
     * @param page The page we're currently on. (There may have been another way of persisting that bit of information.)
     * @param length The page length we're currently showing. (See above.)
     * @return Redirects back to the current page of the joke list.
     */
    @GetMapping('/like')
    String likeJoke(@RequestParam(name='id', required=true) String id, @RequestParam(name= 'page', required=false, defaultValue= '1') String page, @RequestParam(name= 'length', required=false, defaultValue= '10') String length) {
        int jokeId = id as Integer
        if(jokeId) {
            Optional<Joke> jokeOptional = jokes.findById(jokeId)
            if(jokeOptional.isPresent()) {
                Joke joke = jokeOptional.get()
                joke.likes++
                jokes.save(joke)
            }
        }
        return "redirect:/?page=${page}&length=${length}"
    }


    /**
     * Spring MVC handler for GET requests to 'dislike'. (Handles the dislike button.)
     * @param id The joke ID to dislike.
     * @param page The page we're currently on. (There may have been another way of persisting that bit of information.)
     * @param length The page length we're currently showing. (See above.)
     * @return Redirects back to the current page of the joke list.
     */
    @GetMapping('/dislike')
    String dislikeJoke(@RequestParam(name='id', required=true) String id, @RequestParam(name= 'page', required=false, defaultValue= '1') String page, @RequestParam(name= 'length', required=false, defaultValue= '10') String length) {
        int jokeId = id as Integer
        if(jokeId) {
            Optional<Joke> jokeOptional = jokes.findById(jokeId)
            if(jokeOptional.isPresent()) {
                Joke joke = jokeOptional.get()
                joke.likes--
                jokes.save(joke)
            }
        }
        return "redirect:/?page=${page}&length=${length}"
    }
}
