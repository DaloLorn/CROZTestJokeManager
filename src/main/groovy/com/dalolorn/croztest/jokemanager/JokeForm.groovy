package com.dalolorn.croztest.jokemanager

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class JokeForm {
    @Size(min=1, max=255, message='ERROR: Category names must be between 1 and 255 characters long!') // I somewhat suspect this is not the sort of validation that was expected here... but it seems pretty appropriate.
    String category

    @Size(min=1, max=65535, message='ERROR: Joke content must be between 1 and 65535 characters long!')
    String content

    @Override
    String toString() {
        "Joke(Category: \"${category}\", Content: \"${content}\""
    }
}
