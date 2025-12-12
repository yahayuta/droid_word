import 'package:flutter/material.dart';
import 'database_helper.dart';
import 'word.dart';
import 'new_word_screen.dart';
import 'edit_word_screen.dart';
import 'dart:io';
import 'package:path_provider/path_provider.dart';
import 'package:share_plus/share_plus.dart';
import 'package:file_picker/file_picker.dart';

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Droid Word',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Droid Word'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  List<Word> _dataList = [];
  int _currentIndex = 0;
  bool _isReverse = false;
  bool _showWord2 = false;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  void _loadData() async {
    final dbHelper = DatabaseHelper.instance;
    final allWords = await dbHelper.getAllWords();
    final status = await dbHelper.getStatus();

    setState(() {
      _dataList = allWords;
      if (status.isNotEmpty) {
        _currentIndex = int.parse(status[DatabaseHelper.columnCurrentIndex]);
        _isReverse = status[DatabaseHelper.columnReverse] == 'true';
      }
    });
  }

  Future<void> _saveStatus() async {
    final dbHelper = DatabaseHelper.instance;
    await dbHelper.updateStatus(_currentIndex, _isReverse);
  }

  Future<void> _exportData() async {
    if (_dataList.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('No words to export.')),
      );
      return;
    }

    final StringBuffer csv = StringBuffer();
    for (var word in _dataList) {
      csv.writeln('${word.word1},${word.word2}');
    }

    final directory = await getApplicationDocumentsDirectory();
    final file = File('${directory.path}/wordlist.csv');
    await file.writeAsString(csv.toString());

    await Share.shareXFiles([XFile(file.path)], text: 'Exported Word List');

    if(mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Words exported to ${file.path}')),
      );
    }
  }

  Future<void> _importData() async {
    FilePickerResult? result = await FilePicker.platform.pickFiles(
      type: FileType.custom,
      allowedExtensions: ['csv'],
    );

    if (result != null && result.files.single.path != null) {
      File file = File(result.files.single.path!);
      try {
        List<String> lines = await file.readAsLines();
        final dbHelper = DatabaseHelper.instance;
        for (String line in lines) {
          List<String> parts = line.split(',');
          if (parts.length == 2) {
            final word = Word(
              date: DateTime.now().millisecondsSinceEpoch.toString(),
              word1: parts[0].trim(),
              word2: parts[1].trim(),
            );
            await dbHelper.insert(word);
          }
        }
        if(mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Words imported successfully!')),
          );
        }
        _loadData(); // Reload data after import
      } catch (e) {
        if(mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Error importing words: $e')),
          );
        }
      }
    } else {
      if(mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('No file selected for import.')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
        actions: [
          IconButton(
            icon: const Icon(Icons.swap_horiz),
            onPressed: () {
              setState(() {
                _isReverse = !_isReverse;
                _saveStatus(); // Save status after change
              });
              _loadData(); // Reload data to reflect reversed words
            },
          ),
          IconButton(
            icon: const Icon(Icons.first_page),
            onPressed: () {
              setState(() {
                _currentIndex = 0;
                _showWord2 = false;
                _saveStatus(); // Save status after change
              });
            },
          ),
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () async {
              final confirm = await showDialog<bool>(
                context: context,
                builder: (BuildContext context) {
                  return AlertDialog(
                    title: const Text('Confirm Reset'),
                    content: const Text(
                        'Are you sure you want to delete all words? This action cannot be undone.'),
                    actions: <Widget>[
                      TextButton(
                        onPressed: () => Navigator.of(context).pop(false),
                        child: const Text('Cancel'),
                      ),
                      TextButton(
                        onPressed: () => Navigator.of(context).pop(true),
                        child: const Text('Reset All'),
                      ),
                    ],
                  );
                },
              );
              if (confirm == true) {
                final dbHelper = DatabaseHelper.instance;
                await dbHelper.deleteAllWords();
                setState(() {
                  _currentIndex = 0;
                  _isReverse = false;
                  _showWord2 = false;
                  _saveStatus(); // Save status after reset
                });
                _loadData(); // Reload data after reset
              }
            },
          ),
          IconButton(
            icon: const Icon(Icons.file_upload),
            onPressed: _exportData,
          ),
          IconButton(
            icon: const Icon(Icons.file_download),
            onPressed: _importData,
          ),
        ],
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              '${_currentIndex + 1}/${_dataList.length}${_isReverse ? " (Reverse)" : ""}',
            ),
            Card(
              child: Column(
                children: [
                  Text(_dataList.isNotEmpty
                      ? (_isReverse ? _dataList[_currentIndex].word2 : _dataList[_currentIndex].word1)
                      : ''),
                  if (_showWord2)
                    Text(_dataList.isNotEmpty
                        ? (_isReverse ? _dataList[_currentIndex].word1 : _dataList[_currentIndex].word2)
                        : ''),
                ],
              ),
            ),
            ElevatedButton(
              onPressed: () {
                setState(() {
                  _showWord2 = true;
                });
              },
              child: const Text('Display'),
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                ElevatedButton(
                  onPressed: _currentIndex > 0
                      ? () {
                          setState(() {
                            _currentIndex--;
                            _showWord2 = false;
                            _saveStatus(); // Save status after change
                          });
                        }
                      : null,
                  child: const Text('Previous'),
                ),
                ElevatedButton(
                  onPressed: _currentIndex < _dataList.length - 1
                      ? () {
                          setState(() {
                            _currentIndex++;
                            _showWord2 = false;
                            _saveStatus(); // Save status after change
                          });
                        }
                      : null,
                  child: const Text('Next'),
                ),
              ],
            ),
            Slider(
              value: _currentIndex.toDouble(),
              min: 0,
              max: _dataList.isNotEmpty ? _dataList.length.toDouble() - 1 : 0,
              divisions: _dataList.isNotEmpty ? _dataList.length - 1 : 1,
              label: '${_currentIndex + 1}',
              onChanged: (double value) {
                setState(() {
                  _currentIndex = value.toInt();
                  _showWord2 = false;
                  _saveStatus(); // Save status after change
                });
              },
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                ElevatedButton(
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (context) => const NewWordScreen()),
                    ).then((_) {
                      _loadData(); // Reload data after new word is added
                      _saveStatus(); // Save status after load data
                    });
                  },
                  child: const Text('New'),
                ),
                ElevatedButton(
                  onPressed: _dataList.isNotEmpty
                      ? () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (context) =>
                                    EditWordScreen(word: _dataList[_currentIndex])),
                          ).then((_) {
                            _loadData(); // Reload data after word is edited
                            _saveStatus(); // Save status after load data
                          });
                        }
                      : null,
                  child: const Text('Save'),
                ),
                ElevatedButton(
                  onPressed: _dataList.isNotEmpty
                      ? () async {
                          final confirm = await showDialog<bool>(
                            context: context,
                            builder: (BuildContext context) {
                              return AlertDialog(
                                title: const Text('Confirm Deletion'),
                                content: const Text(
                                    'Are you sure you want to delete this word?'),
                                actions: <Widget>[
                                  TextButton(
                                    onPressed: () => Navigator.of(context).pop(false),
                                    child: const Text('Cancel'),
                                  ),
                                  TextButton(
                                    onPressed: () => Navigator.of(context).pop(true),
                                    child: const Text('Delete'),
                                  ),
                                ],
                              );
                            },
                          );
                          if (confirm == true) {
                            final dbHelper = DatabaseHelper.instance;
                            await dbHelper.deleteWord(_dataList[_currentIndex].date);
                            setState(() {
                              if (_currentIndex >= _dataList.length - 1 && _currentIndex > 0) {
                                _currentIndex--;
                              } else if (_dataList.length == 1) { // If it was the last word
                                _currentIndex = 0;
                              }
                              _showWord2 = false;
                              _saveStatus(); // Save status after deletion
                            });
                            _loadData(); // Reload data after deletion
                          }
                        }
                      : null,
                  child: const Text('Delete'),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
