import 'package:flutter/material.dart';
import 'database_helper.dart';
import 'word.dart';

class EditWordScreen extends StatefulWidget {
  final Word word;

  const EditWordScreen({super.key, required this.word});

  @override
  State<EditWordScreen> createState() => _EditWordScreenState();
}

class _EditWordScreenState extends State<EditWordScreen> {
  final _formKey = GlobalKey<FormState>();
  late TextEditingController _word1Controller;
  late TextEditingController _word2Controller;

  @override
  void initState() {
    super.initState();
    _word1Controller = TextEditingController(text: widget.word.word1);
    _word2Controller = TextEditingController(text: widget.word.word2);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Edit Word'),
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
                  _updateWord();
                }
              },
              child: const Text('Save Changes'),
            ),
          ],
        ),
      ),
    );
  }

  void _updateWord() async {
    final dbHelper = DatabaseHelper.instance;
    final updatedWord = Word(
      date: widget.word.date,
      word1: _word1Controller.text,
      word2: _word2Controller.text,
    );
    await dbHelper.updateWord(updatedWord);
    if(mounted) {
      Navigator.pop(context);
    }
  }

  @override
  void dispose() {
    _word1Controller.dispose();
    _word2Controller.dispose();
    super.dispose();
  }
}
