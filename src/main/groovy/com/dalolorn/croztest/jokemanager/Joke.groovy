package com.dalolorn.croztest.jokemanager

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * JPA entity class for jokes.
 */
@Entity
class Joke {
    @SuppressWarnings("GrFinalVariableAccess")
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    final int id
    int category
    int likes
    int dislikes
    String content

    protected Joke() {}

    Joke(int category, int likes, int dislikes, String content) {
        this.category = category
        this.likes = likes
        this.dislikes = dislikes
        this.content = content
    }

    @Override
    String toString() {
        "Joke[id=${id}, category=${category}, likes=${likes}, dislikes=${dislikes}, content='${content}']"
    }
}
