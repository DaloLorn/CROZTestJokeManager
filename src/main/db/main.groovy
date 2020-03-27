databaseChangeLog() {
    changeSet(id: 1, author: 'Neven Prašnikar') { // Define tables.
        createTable(tableName: 'category') {
            column(name: 'id', type: 'int', autoIncrement: true) {
                constraints(primaryKey: true, nullable: false)
            }
            column(name: 'name', type: 'varchar(255)') {
                constraints(nullable: false, unique: true) // Presumably we don't want two categories sharing the same name.
            }
        }
        createTable(tableName: 'joke') {
            column(name: 'id', type: 'int', autoIncrement: true) {
                constraints(primaryKey: true, nullable: false)
            }
            column(name: 'content', type: 'varchar(65535)') {
                constraints(nullable: false)
            }
            column(name: 'likes', type: 'int', defaultValueNumeric: 0)
            column(name: 'dislikes', type: 'int', defaultValueNumeric: 0)
            column(name: 'category', type: 'int') {
                constraints(nullable: false, foreignKeyName: 'fk_category_id', referencedTableName: 'category', referencedColumnNames:'id')
            }
        }
    }
    changeSet(id: 2, author: 'Neven Prašnikar') { // Define joke categories.
        insert(tableName: 'category') {
            column(name: 'name', value: 'Chuck Norris')
        }
        insert(tableName: 'category') {
            column(name: 'name', value: 'Škola')
        }
        insert(tableName: 'category') {
            column(name: 'name', value: 'Mujo')
        }
    }
    changeSet(id: 3, author: 'Neven Prašnikar') { // Define first joke. (I could technically roll the subsequent joke definitions into this one, but I feel like that would not accurately represent how I approached this.)
        insert(tableName: 'joke') {
            column(name: 'category', valueComputed: '(SELECT id FROM category WHERE name = \'Chuck Norris\')') // We have no guarantee that Chuck Norris jokes will have an externally predictable category ID. They probably will, but they don't have to.
            column(name: 'content', value: 'Zašto je Chuck Norris najjači?\nZato što vježba dva dana dnevno.')
            column(name: 'likes', valueNumeric: 72)
            column(name: 'dislikes', valueNumeric: 10)
        }
    }
    changeSet(id: 4, author: 'Neven Prašnikar') { // Add the other default jokes.
        insert(tableName: 'joke') {
            column(name: 'category', valueComputed: '(SELECT id FROM category WHERE name = \'Škola\')')
            column(name: 'content', value: 'Pita nastavnica hrvatskog jezika mladog osnovnoškolca:\nReci ti meni što su to prilozi?\nPrilozi su: ketchup, majoneza, luk, salata...')
            column(name: 'likes', valueNumeric: 80)
            column(name: 'dislikes', valueNumeric: 40)
        }
        insert(tableName: 'joke') {
            column(name: 'category', valueComputed: '(SELECT id FROM category WHERE name = \'Škola\')')
            column(name: 'content', value: 'Pričaju dvije gimnazijalke:\nNema mi roditelja doma ovaj vikend!\nBože, pa koja si ti sretnica! Možeš učiti naglas!')
            column(name: 'likes', valueNumeric: 25)
            column(name: 'dislikes', valueNumeric: 2)
        }
        insert(tableName: 'joke') {
            column(name: 'category', valueComputed: '(SELECT id FROM category WHERE name = \'Mujo\')')
            column(name: 'content', value: 'Došao Mujo u pizzeriju i naručio pizzu. Konobar ga upita:\nŽelite da vam izrežem pizzu na 6 ili 12 komada?\nMa na 6 komada, nema šanse da pojedem 12.')
            column(name: 'likes', valueNumeric: 32)
            column(name: 'dislikes', valueNumeric: 9)
        }
    }
    changeSet(id: 5, author: 'Neven Prašnikar') { // Add another joke, since the assignment specified 5 jokes and I was only able to, ehm, 'borrow' 4 of them.
        insert(tableName: 'category') {
            column(name: 'name', value: 'Overused Classics') // Because of course those would be the only kind I know...
        }
        insert(tableName: 'joke') {
            column(name: 'category', valueComputed: '(SELECT id FROM category WHERE name = \'Overused Classics\')')
            column(name: 'content', value: 'Why did the chicken cross the road?\nTo get to the other side!')
            column(name: 'likes', valueNumeric: 4)
            column(name: 'dislikes', valueNumeric: 17)
        }
    }
}