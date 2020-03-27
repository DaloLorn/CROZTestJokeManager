package com.dalolorn.croztest.jokemanager

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest

import static org.junit.jupiter.api.Assertions.*

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JokeManagerApplicationTests {

	private static final String TAG_DB_INIT = 'DBInitAndFindAll'
	private static final String TAG_CATEGORY = 'Categories'
	private static final String TAG_JOKE = 'Jokes'

	@Autowired
	private CategoryRepository categories
	@Autowired
	private JokeRepository jokes

	private categoryList = [
			new Category('Chuck Norris'),
			new Category('Škola'),
			new Category('Mujo'),
			new Category('Overused Classics')
	]
	private jokeList = [
			// This is an awkward situation. Hardcoding the category IDs risks inaccuracies if I predict them incorrectly...
			// But finding the categories by name is one of the things I'm testing here!
			new Joke(1, 72, 10, 'Zašto je Chuck Norris najjači?\nZato što vježba dva dana dnevno.'),
			new Joke(2, 80, 40, 'Pita nastavnica hrvatskog jezika mladog osnovnoškolca:\nReci ti meni što su to prilozi?\nPrilozi su: ketchup, majoneza, luk, salata...'),
			new Joke(2, 25, 2, 'Pričaju dvije gimnazijalke:\nNema mi roditelja doma ovaj vikend!\nBože, pa koja si ti sretnica! Možeš učiti naglas!'),
			new Joke(3, 32, 9, 'Došao Mujo u pizzeriju i naručio pizzu. Konobar ga upita:\nŽelite da vam izrežem pizzu na 6 ili 12 komada?\nMa na 6 komada, nema šanse da pojedem 12.'),
			new Joke(4, 4, 17, 'Why did the chicken cross the road?\nTo get to the other side!')
	]

	@BeforeAll
	void initDatabase() {
		categoryList.each {
			categories.save it
		}

		jokeList.each {
			jokes.save it
		}
	}

	@Test
	@Tag(TAG_DB_INIT)
	void testCategoryFindAll() {
		categories.findAll().eachWithIndex { Category entry, int i ->
			assertEquals(entry.id, categoryList[i].id)
			assertEquals(entry.name, categoryList[i].name)
		}
	}

	@Test
	@Tag(TAG_DB_INIT)
	void testJokeFindAll() {
		jokes.findAll().eachWithIndex { Joke entry, int i ->
			assertEquals(entry.id, jokeList[i].id)
			assertEquals(entry.category, jokeList[i].category)
			assertEquals(entry.likes, jokeList[i].likes)
			assertEquals(entry.dislikes, jokeList[i].dislikes)
			assertEquals(entry.content, jokeList[i].content)
		}
	}

	@Test
	@Tag(TAG_CATEGORY)
	void testCategoryFindById() {
		int i = 0
		Optional<Category> resultOptional = categories.findById(i+1)
		assertTrue(resultOptional.isPresent() ^ categoryList.find{it.id == i+1} == null) // If the optional is present but the ID isn't, or vice versa, then something is wrong... so we want to assert that *one and only one* of those statements is true.
		if(resultOptional.isPresent()) {
			Category result = resultOptional.get()
			assertEquals(result.id, categoryList[i].id)
			assertEquals(result.name, categoryList[i].name)
		}
	}

	@Test
	@Tag(TAG_CATEGORY)
	void testCategoryFindByBadId() {
		int i = categoryList.size()
		Optional<Category> result = categories.findById(i+1)
		assertFalse(result.isPresent() || categoryList.find{it.id == i+1}) // We need to be sure that neither the list nor the DB can find the result.
	}

	@Test
	@Tag(TAG_CATEGORY)
	void testCategoryFindByName() {
		int i = 1
		Optional<Category> resultOptional = categories.findByName(categoryList[i]?.name)
		assertTrue(resultOptional.isPresent() ^ categoryList.size() <= i)
		if(resultOptional.isPresent()) {
			Category result = resultOptional.get()
			assertEquals(result.id, categoryList[i].id)
			assertEquals(result.name, categoryList[i].name)
		}
	}

	@Test
	@Tag(TAG_CATEGORY)
	void testCategoryFindByBadName() { // We can also consider this a negative test for findByNameIgnoreCase().
		String name = "Crash Test Dummy"
		while(categoryList.find{it.name == name}) name += " (Copy)"
		Optional<Category> result = categories.findByName(name)
		assertFalse(result.isPresent())
	}

	@Test
	@Tag(TAG_CATEGORY)
	void testCategoryFindByNameIgnoreCase() {
		int i = 2
		Category result = categories.findByNameIgnoreCase(categoryList[i].name.toLowerCase(Locale.ENGLISH)).get()
		assertNotNull(result)
		assertEquals(result.id, categoryList[i].id)
		assertEquals(result.name, categoryList[i].name)
	}

	void testJokes(List<Joke> result, int expectedSize) {
		assertEquals(result.size(), expectedSize)
		result.each { entry ->
			Joke joke = jokeList.find{it.id == entry.id}
			assertNotNull(joke)
			assertEquals(entry.category, joke.category)
			assertEquals(entry.likes, joke.likes)
			assertEquals(entry.dislikes, joke.dislikes)
			assertEquals(entry.content, joke.content)
		}
	}

	void testJokeFindByCategory(int i) {
		int count = jokeList.findAll{it.category == categoryList[i]?.id}.size()
		List<Joke> result = jokes.findByCategory(categoryList[i]?.id)
		testJokes(result, count)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategory1() {
		testJokeFindByCategory(1)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategory2() {
		testJokeFindByCategory(categoryList.size())
	}

	void testJokeFindByCategoryPaged(int i, int size, int page) {
		int possibleCount = jokeList.findAll{it.category == categoryList[i]?.id}.size()
		int pageSize = Math.max(Math.min(possibleCount - page * size, size), 0)
		List<Joke> result = jokes.findByCategory(categoryList[i]?.id, PageRequest.of(page, size))
		testJokes(result, pageSize)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryPaged1() {
		testJokeFindByCategoryPaged(1, 1, 0)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryPaged2() {
		testJokeFindByCategoryPaged(1, 5, 0)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryPaged3() {
		testJokeFindByCategoryPaged(1, 1, 4)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryPaged4() {
		testJokeFindByCategoryPaged(categoryList.size(), 1, 1)
	}

	void testJokeFindByCategoryName(int i) {
		int count = jokeList.findAll{it.category == categoryList[i]?.id}.size()
		List<Joke> result = jokes.findByCategoryName(categoryList[i]?.name)
		testJokes(result, count)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryName1() {
		testJokeFindByCategoryName(1)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryName2() {
		testJokeFindByCategoryName(categoryList.size())
	}

	void testJokeFindByCategoryNamePaged(int i, int size, int page) {
		int possibleCount = jokeList.findAll{it.category == categoryList[i]?.id}.size()
		int pageSize = Math.max(Math.min(possibleCount - page * size, size), 0)
		List<Joke> result = jokes.findByCategoryName(categoryList[i]?.name, PageRequest.of(page, size))
		testJokes(result, pageSize)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNamePaged1() {
		testJokeFindByCategoryNamePaged(1, 1, 0)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNamePaged2() {
		testJokeFindByCategoryNamePaged(1, 5, 0)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNamePaged3() {
		testJokeFindByCategoryNamePaged(1, 1, 4)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNamePaged4() {
		testJokeFindByCategoryNamePaged(categoryList.size(), 1, 1)
	}

	void testJokeFindByCategoryNameIgnoreCase(int i) {
		int count = jokeList.findAll{it.category == categoryList[i]?.id}.size()
		List<Joke> result = jokes.findByCategoryNameIgnoreCase(categoryList[i]?.name)
		testJokes(result, count)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNameIgnoreCase1() {
		testJokeFindByCategoryNameIgnoreCase(1)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNameIgnoreCase2() {
		testJokeFindByCategoryNameIgnoreCase(categoryList.size())
	}

	void testJokeFindByCategoryNameIgnoreCasePaged(int i, int size, int page) {
		int possibleCount = jokeList.findAll{it.category == categoryList[i]?.id}.size()
		int pageSize = Math.max(Math.min(possibleCount - page * size, size), 0)
		List<Joke> result = jokes.findByCategoryNameIgnoreCase(categoryList[i]?.name, PageRequest.of(page, size))
		testJokes(result, pageSize)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNameIgnoreCasePaged1() {
		testJokeFindByCategoryNameIgnoreCasePaged(1, 1, 0)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNameIgnoreCasePaged2() {
		testJokeFindByCategoryNameIgnoreCasePaged(1, 5, 0)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNameIgnoreCasePaged3() {
		testJokeFindByCategoryNameIgnoreCasePaged(1, 1, 4)
	}

	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNameIgnoreCasePaged4() {
		testJokeFindByCategoryNameIgnoreCasePaged(categoryList.size(), 1, 1)
	}
}
