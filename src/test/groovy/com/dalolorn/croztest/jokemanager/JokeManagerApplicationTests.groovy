package com.dalolorn.croztest.jokemanager

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest

import static org.junit.jupiter.api.Assertions.*

/**
 * Test package for the joke manager.
 */
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JokeManagerApplicationTests {

	/** If this did what I thought it would, it would sort the DB initialization tests into their own category for tidier viewing. */
	private static final String TAG_DB_INIT = 'DBInitAndFindAll'
	/** This would sort the category tests into their own category. */
	private static final String TAG_CATEGORY = 'Categories'
	/** This would sort the joke tests into their own category. */
	private static final String TAG_JOKE = 'Jokes'

	/** Controls database access to the joke categories. */
	@Autowired
	private CategoryRepository categories
	/** Controls database access to the jokes. */
	@Autowired
	private JokeRepository jokes

	/**
	 * Defines the categories to test on. (Why, I think I'm falling in love with this language... I dodged so much redundant verbosity by deciding to try it out...)
	 */
	private categoryList = [
			new Category('Chuck Norris'),
			new Category('Škola'),
			new Category('Mujo'),
			new Category('Overused Classics')
	]

	/**
	 * Defines the jokes to test on.
	 */
	private jokeList = [
			// This is an awkward situation. Hardcoding the category IDs risks inaccuracies if I predict them incorrectly, or they change...
			// But finding the categories by name is one of the things I'm testing here!
			new Joke(1, 72, 10, 'Zašto je Chuck Norris najjači?\nZato što vježba dva dana dnevno.'),
			new Joke(2, 80, 40, 'Pita nastavnica hrvatskog jezika mladog osnovnoškolca:\nReci ti meni što su to prilozi?\nPrilozi su: ketchup, majoneza, luk, salata...'),
			new Joke(2, 25, 2, 'Pričaju dvije gimnazijalke:\nNema mi roditelja doma ovaj vikend!\nBože, pa koja si ti sretnica! Možeš učiti naglas!'),
			new Joke(3, 32, 9, 'Došao Mujo u pizzeriju i naručio pizzu. Konobar ga upita:\nŽelite da vam izrežem pizzu na 6 ili 12 komada?\nMa na 6 komada, nema šanse da pojedem 12.'),
			new Joke(4, 4, 17, 'Why did the chicken cross the road?\nTo get to the other side!')
	]

	/**
	 * Prepares the database.
	 */
	@BeforeAll
	void initDatabase() {
		categoryList.each {
			categories.save it
		}

		jokeList.each {
			jokes.save it
		}
	}

	/**
	 * Tests that all categories were successfully saved to and loaded from the database.
	 */
	@Test
	@Tag(TAG_DB_INIT)
	void testCategoryFindAll() {
		categories.findAll().eachWithIndex { Category entry, int i ->
			assertEquals(entry.id, categoryList[i].id)
			assertEquals(entry.name, categoryList[i].name)
		}
	}

	/**
	 * Tests that all jokes were successfully saved to and loaded from the database.
	 */
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

	/**
	 * Tests if a category can be found by its ID.
	 */
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

	/**
	 * Tests if a faulty ID will return no category.
	 */
	@Test
	@Tag(TAG_CATEGORY)
	void testCategoryFindByBadId() {
		int i = categoryList.size()
		Optional<Category> result = categories.findById(i+1)
		assertFalse(result.isPresent() || categoryList.find{it.id == i+1}) // We need to be sure that neither the list nor the DB can find the result.
	}

	/**
	 * Tests if a category can be found by its name.
	 */
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

	/**
	 * Tests if a faulty name will return no category.
	 */
	@Test
	@Tag(TAG_CATEGORY)
	void testCategoryFindByBadName() { // We can also consider this a negative test for findByNameIgnoreCase().
		String name = "Crash Test Dummy"
		while(categoryList.find{it.name == name}) name += " (Copy)"
		Optional<Category> result = categories.findByName(name)
		assertFalse(result.isPresent())
	}

	/**
	 * Tests if a category can be case-insensitively found by its name. (This test would be quite useless if someone set categoryList[2].name to a lowercase string...)
	 */
	@Test
	@Tag(TAG_CATEGORY)
	void testCategoryFindByNameIgnoreCase() {
		int i = 2
		Category result = categories.findByNameIgnoreCase(categoryList[i].name.toLowerCase(Locale.ENGLISH)).get()
		assertNotNull(result)
		assertEquals(result.id, categoryList[i].id)
		assertEquals(result.name, categoryList[i].name)
	}

	/**
	 * Shared code for all validations on a joke list.
	 * @param result The joke list being validated. All jokes within must be present within jokeList, and the list must be of a certain size.
	 * @param expectedSize The expected size of the joke list.
	 */
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

	/**
	 * Shared code for all testJokeFindByCategory variants.
	 * @param i The category index whose ID should be filtered for.
	 */
	void testJokeFindByCategory(int i) {
		int count = jokeList.findAll{it.category == categoryList[i]?.id}.size() // How many matching items do we expect the DB to return?
		List<Joke> result = jokes.findByCategory(categoryList[i]?.id)
		testJokes(result, count)
	}

	/**
	 * Tests if any jokes can be found in category 1.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategory1() {
		testJokeFindByCategory(1)
	}

	/**
	 * Tests if no jokes can be found in a non-existent category.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategory2() {
		testJokeFindByCategory(categoryList.size())
	}

	/**
	 * Shared code for all testJokeFindByCategoryPaged variants.
	 * @param i The category index whose ID should be filtered for.
	 * @param size Page size to request.
	 * @param page The page to request.
	 */
	void testJokeFindByCategoryPaged(int i, int size, int page) {
		int possibleCount = jokeList.findAll{it.category == categoryList[i]?.id}.size() // What's the biggest possible page size for this query on this DB?
		int pageSize = Math.max(Math.min(possibleCount - page * size, size), 0) // How many items do we *actually* expect to get?
		List<Joke> result = jokes.findByCategory(categoryList[i]?.id, PageRequest.of(page, size))
		testJokes(result, pageSize)
	}

	/**
	 * Tests if the first joke can be found in category 1.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryPaged1() {
		testJokeFindByCategoryPaged(1, 1, 0)
	}

	/**
	 * Tests if up to a non-existent amount of jokes can be found in category 1.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryPaged2() {
		testJokeFindByCategoryPaged(1, jokeList.size()+1, 0)
	}

	/**
	 * Tests if a non-existent page of jokes can be found in category 1.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryPaged3() {
		testJokeFindByCategoryPaged(1, 1, jokeList.size())
	}

	/**
	 * Tests if the first joke cannot be found in a non-existent category.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryPaged4() {
		testJokeFindByCategoryPaged(categoryList.size(), 1, 1)
	}

	/**
	 * Shared code for all testJokeFindByCategoryName variants.
	 * @param i The category index whose name should be filtered for.
	 */
	void testJokeFindByCategoryName(int i) {
		int count = jokeList.findAll{it.category == categoryList[i]?.id}.size() // How many matching items do we expect the DB to return?
		List<Joke> result = jokes.findByCategoryName(categoryList[i]?.name)
		testJokes(result, count)
	}

	/**
	 * Tests if any jokes can be found in category 1 when searching by its name.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryName1() {
		testJokeFindByCategoryName(1)
	}

	/**
	 * Tests if no jokes can be found in a non-existent category when searching by its name.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryName2() {
		testJokeFindByCategoryName(categoryList.size())
	}

	/**
	 * Shared code for all testJokeFindByCategoryNamePaged variants.
	 * @param i The category index whose ID should be filtered for.
	 * @param size Page size to request.
	 * @param page The page to request.
	 */
	void testJokeFindByCategoryNamePaged(int i, int size, int page) {
		int possibleCount = jokeList.findAll{it.category == categoryList[i]?.id}.size() // What's the biggest possible page size for this query on this DB?
		int pageSize = Math.max(Math.min(possibleCount - page * size, size), 0) // How many items do we *actually* expect to get?
		List<Joke> result = jokes.findByCategoryName(categoryList[i]?.name, PageRequest.of(page, size))
		testJokes(result, pageSize)
	}

	/**
	 * Tests if the first joke can be found in category 1 when searching by its name.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNamePaged1() {
		testJokeFindByCategoryNamePaged(1, 1, 0)
	}

	/**
	 * Tests if up to a non-existent amount of jokes can be found in category 1 when searching by its name.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNamePaged2() {
		testJokeFindByCategoryNamePaged(1, jokeList.size()+1, 0)
	}

	/**
	 * Tests if a non-existent page of jokes can be found in category 1 when searching by its name.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNamePaged3() {
		testJokeFindByCategoryNamePaged(1, 1, jokeList.size())
	}

	/**
	 * Tests if the first joke cannot be found in a non-existent category when searching by its name.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNamePaged4() {
		testJokeFindByCategoryNamePaged(categoryList.size(), 1, 1)
	}

	void testJokeFindByCategoryNameIgnoreCase(int i) {
		int count = jokeList.findAll{it.category == categoryList[i]?.id}.size() // How many matching items do we expect the DB to return?
		List<Joke> result = jokes.findByCategoryNameIgnoreCase(categoryList[i]?.name)
		testJokes(result, count)
	}

	/**
	 * Tests if any jokes can be found in category 1 when case-insensitively searching by its name.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNameIgnoreCase1() {
		testJokeFindByCategoryNameIgnoreCase(1)
	}

	/**
	 * Tests if no jokes can be found in a non-existent category case-insensitively searching by its name.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNameIgnoreCase2() {
		testJokeFindByCategoryNameIgnoreCase(categoryList.size())
	}

	void testJokeFindByCategoryNameIgnoreCasePaged(int i, int size, int page) {
		int possibleCount = jokeList.findAll{it.category == categoryList[i]?.id}.size() // What's the biggest possible page size for this query on this DB?
		int pageSize = Math.max(Math.min(possibleCount - page * size, size), 0) // How many items do we *actually* expect to get?
		List<Joke> result = jokes.findByCategoryNameIgnoreCase(categoryList[i]?.name, PageRequest.of(page, size))
		testJokes(result, pageSize)
	}

	/**
	 * Tests if the first joke can be found in category 1 when case-insensitively searching by its name.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNameIgnoreCasePaged1() {
		testJokeFindByCategoryNameIgnoreCasePaged(1, 1, 0)
	}

	/**
	 * Tests if up to a non-existent amount of jokes can be found in category 1 when case-insensitively searching by its name.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNameIgnoreCasePaged2() {
		testJokeFindByCategoryNameIgnoreCasePaged(1, jokeList.size()+1, 0)
	}

	/**
	 * Tests if a non-existent page of jokes can be found in category 1 when case-insensitively searching by its name.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNameIgnoreCasePaged3() {
		testJokeFindByCategoryNameIgnoreCasePaged(1, 1, jokeList.size())
	}

	/**
	 * Tests if the first joke cannot be found in a non-existent category when case-insensitively searching by its name.
	 */
	@Test
	@Tag(TAG_JOKE)
	void testJokeFindByCategoryNameIgnoreCasePaged4() {
		testJokeFindByCategoryNameIgnoreCasePaged(categoryList.size(), 1, 1)
	}
}
