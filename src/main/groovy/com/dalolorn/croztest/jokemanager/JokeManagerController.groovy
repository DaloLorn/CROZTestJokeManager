package com.dalolorn.croztest.jokemanager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

import javax.validation.Valid

/* Location: Localhost:8080/new
 * Other requirements:
 * - Content field must contain text
 * - Category field must not be empty (use editable combobox?)
 * - Use button to submit form and create new joke in DB
 */
@Controller
class JokeManagerController {
    @Autowired
    CategoryRepository categories

    @Autowired
    JokeRepository jokes

    @GetMapping('/new')
    String newJoke(Model model) {
        model.addAttribute('joke', new JokeForm())
        model.addAttribute('categories', categories.findAll().collect {it.name})
        return 'newjoke'
    }

    @PostMapping('/new')
    String newJokeSubmit(@Valid @ModelAttribute('joke') JokeForm joke, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute('categories', categories.findAll().collect{it.name})
            return 'newjoke'
        }

        Category category
        Optional<Category> categoryOptional = categories.findByNameIgnoreCase(joke.category)
        if(categoryOptional.isPresent()) category = categoryOptional.get()
        else {
            category = new Category(joke.category)
            categories.save(category)
        }

        jokes.save(new Joke(category.id, 0, 0, joke.content))
        return 'redirect:/'
    }

    @GetMapping('/')
    String jokeList(@RequestParam(name= 'page', required=false, defaultValue= '1') String page, @RequestParam(name= 'length', required=false, defaultValue= '10') String length, Model model) {
        int pageNum, pageLength
        pageNum = Math.max(page as Integer ?: 1, 1)
        pageLength = Math.max(length as Integer ?: 10, 1)
        model.addAttribute(
                'jokes',
                jokes.findAllOrderByNetLikes(PageRequest.of(pageNum - 1, pageLength))
        )
        model.addAttribute('categories', categories.findAll().collectEntries{ [it.id, it.name]})
        model.addAttribute('startingIndex', 1 + pageLength * (pageNum-1))
        model.addAttribute('pageCount', Math.ceil(jokes.count() / pageLength))
        return 'jokelist'
    }

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
