import 'package:flutter/material.dart';
import 'database_helper.dart';
import 'word.dart';

class NewWordScreen extends StatefulWidget {
  const NewWordScreen({super.key});

  @override
  State<NewWordScreen> createState() => _NewWordScreenState();
}

class _NewWordScreenState extends State<NewWordScreen> {
  final _formKey = GlobalKey<FormState>();
  final _word1Controller = TextEditingController();
  final _word2Controller = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('New Word'),
      ),
      body: Form(
        key: _formKey,
        child: Column(
          children: <Widget>[
            TextFormField(
              controller: _word1Controller,
              decoration: const InputDecoration(labelText: 'Word 1'),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Please enter a value';
                }
                return null;
              },
            ),
            TextFormField(
              controller: _word2Controller,
              decoration: const InputDecoration(labelText: 'Word 2'),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Please enter a value';
                }
                return null;
              },
            ),
            ElevatedButton(
              onPressed: () {
                if (_formKey.currentState!.validate()) {
                  _saveWord();
                }
              },
              child: const Text('Save'),
            ),
          ],
        ),
      ),
    );
  }

  void _saveWord() async {
    final dbHelper = DatabaseHelper.instance;
    final word = Word(
      date: DateTime.now().millisecondsSinceEpoch.toString(),
      word1: _word1Controller.text,
      word2: _word2Controller.text,
    );
    await dbHelper.insert(word);
    if(mounted) {
      Navigator.pop(context);
    }
  }
}
