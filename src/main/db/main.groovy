databaseChangeLog() {
    changeSet(id: 1, author: 'Neven Prašnikar') {
        createTable(tableName: 'category') {
            column(name: 'id', type: 'int', autoIncrement: true) {
                constraints(primaryKey: true, nullable: false)
            }
            column(name: 'name', type: 'varchar(255)') {
                constraints(nullable: false)
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
}