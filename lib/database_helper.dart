import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';
import 'word.dart';

class DatabaseHelper {
  static const _databaseName = "Word.db";
  static const _databaseVersion = 1;

  static const tableWord = 'Word';
  static const tableStatus = 'Status';

  static const columnDate = 'date';
  static const columnWord1 = 'word1';
  static const columnWord2 = 'word2';

  static const columnWord = 'word';
  static const columnCurrentIndex = 'currentindex';
  static const columnReverse = 'reverse';

  DatabaseHelper._privateConstructor();
  static final DatabaseHelper instance = DatabaseHelper._privateConstructor();

  static Database? _database;
  Future<Database> get database async =>
      _database ??= await _initDatabase();

  Future<Database> _initDatabase() async {
    String path = join(await getDatabasesPath(), _databaseName);
    return await openDatabase(path,
        version: _databaseVersion,
        onCreate: _onCreate);
  }

  Future _onCreate(Database db, int version) async {
    await db.execute('''
          CREATE TABLE $tableWord (
            $columnDate TEXT PRIMARY KEY,
            $columnWord1 TEXT,
            $columnWord2 TEXT
          )
          ''');
    await db.execute('''
          CREATE TABLE $tableStatus (
            $columnWord TEXT PRIMARY KEY,
            $columnCurrentIndex TEXT,
            $columnReverse TEXT
          )
          ''');
  }

  Future<int> insert(Word word) async {
    Database db = await instance.database;
    return await db.insert(tableWord, {
      columnDate: word.date,
      columnWord1: word.word1,
      columnWord2: word.word2,
    });
  }

  Future<List<Word>> getAllWords() async {
    Database db = await instance.database;
    final List<Map<String, dynamic>> maps = await db.query(tableWord);

    return List.generate(maps.length, (i) {
      return Word(
        date: maps[i][columnDate],
        word1: maps[i][columnWord1],
        word2: maps[i][columnWord2],
      );
    });
  }

  Future<Map<String, dynamic>> getStatus() async {
    Database db = await instance.database;
    final List<Map<String, dynamic>> maps = await db.query(tableStatus);

    if (maps.isNotEmpty) {
      return maps.first;
    } else {
      return {};
    }
  }

  Future<int> deleteWord(String date) async {
    Database db = await instance.database;
    return await db.delete(
      tableWord,
      where: '$columnDate = ?',
      whereArgs: [date],
    );
  }

  Future<int> updateWord(Word word) async {
    Database db = await instance.database;
    return await db.update(
      tableWord,
      {
        columnWord1: word.word1,
        columnWord2: word.word2,
      },
      where: '$columnDate = ?',
      whereArgs: [word.date],
    );
  }

  Future<int> deleteAllWords() async {
    Database db = await instance.database;
    return await db.delete(tableWord);
  }

  Future<int> updateStatus(int currentIndex, bool isReverse) async {
    Database db = await instance.database;
    final statusKey = 'word'; // Fixed key as per original Android app
    final values = {
      columnWord: statusKey,
      columnCurrentIndex: currentIndex.toString(),
      columnReverse: isReverse.toString(),
    };
    int count = await db.update(tableStatus, values, where: '$columnWord = ?', whereArgs: [statusKey]);
    if (count == 0) {
      return await db.insert(tableStatus, values);
    }
    return count;
  }
}
