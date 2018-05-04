package droid.word;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * DroidWordActivity
 * @author yasupong
 */
public class WordActivity extends Activity implements OnSeekBarChangeListener, OnClickListener {
	
	/** メニューアイテムID0 */
	private final int MENU_ITEM0 = 0;
	/** メニューアイテムID1 */
	private final int MENU_ITEM1 = 1;
	/** メニューアイテムID2 */
	private final int MENU_ITEM2 = 2;
	/** メニューアイテムID3 */
	private final int MENU_ITEM3 = 3;
	/** メニューアイテムID4 */
	private final int MENU_ITEM4 = 4;
	
	/** ファイル名  */
	private final String FILE_NAME = "wordlist.csv";
	
	/** メンバー変数：データリスト */
	private List<WordEntity> dataList = null;
	/** メンバー変数：表示中のインデックス */
	private int currentIndex = 0;
	/** メンバー変数：セーブ可能状態か */
	private boolean isSaveEnabled = false;
	/** メンバー変数：新規登録可能状態か */
	private boolean isRegist = false;
	/** メンバー変数：逆転状態か */
	private boolean isReverse = false;
	/** メンバー変数：全件削除状態か */
	private boolean isAllDelete = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        // リスナー登録
        Button btnDisp = (Button)findViewById(R.id.buttonDisp);
        btnDisp.setOnClickListener(this);
        Button btnSave = (Button)findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(this);
        Button btnReg = (Button)findViewById(R.id.buttonRegist);
        btnReg.setOnClickListener(this);
        Button btnDelete = (Button)findViewById(R.id.buttonDelete);
        btnDelete.setOnClickListener(this);
        Button btnFwd = (Button)findViewById(R.id.buttonFwd);
        btnFwd.setOnClickListener(this);
        Button btnRev = (Button)findViewById(R.id.buttonRev);
        btnRev.setOnClickListener(this);
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBarData);
        seekBar.setOnSeekBarChangeListener(this);
        
        dataList = getAllData();
        
        if (dataList.size() == 0) {
        	isRegist = true;
        }
        
        // 状態取得
        getStatus();
        // 状態更新
        updateStatus();       
        
        //AdView初期化
        AdView adView = (AdView)this.findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());
    }

	@Override
	public void onClick(View arg0) {
		
		// 画面部品取得
        EditText editValue = (EditText)findViewById(R.id.editTextValue);
        EditText editKey = (EditText)findViewById(R.id.editTextKeyword);
        Button btnDisp = (Button)findViewById(R.id.buttonDisp);
        Button btnSave = (Button)findViewById(R.id.buttonSave);
        Button btnDelete = (Button)findViewById(R.id.buttonDelete);
        Button btnReg = (Button)findViewById(R.id.buttonRegist);
        Button btnFwd = (Button)findViewById(R.id.buttonFwd);
        Button btnRev = (Button)findViewById(R.id.buttonRev);
        
        // 表示
        if (arg0 == btnDisp && dataList.size() > 0) {
            editValue.setText(((WordEntity)dataList.get(currentIndex)).getWord2());
            isSaveEnabled = true;
        }
        
        // 保存
        if (arg0 == btnSave) {
        	saveData();
        	editValue.setText("");
            isSaveEnabled = false;
        }
        
        // 登録
        if (arg0 == btnReg) {
        	if (!isRegist) {
	            editKey.setText("");
	            editValue.setText("");
	            isSaveEnabled = true;
	            isRegist = true;
	            btnSave.setClickable(true);
	        	btnSave.setVisibility(View.VISIBLE);
	            btnReg.setText(getString(R.string.act_btn_cancel));
	            return;
        	} else {
	            isSaveEnabled = false;
	            isRegist = false;
	            btnSave.setClickable(false);
	        	btnSave.setVisibility(View.GONE);
        	}
        }
        
        // 削除
        if (arg0 == btnDelete) {
        	if (!isRegist) {
        		delete();
        	}
        }

        // 進む
        if (arg0 == btnFwd) {
        	currentIndex++;
        	isSaveEnabled = false;
            isRegist = false;
        	editValue.setText("");
        }
        
        // 戻る
        if (arg0 == btnRev) {
        	currentIndex--;
        	isSaveEnabled = false;
            isRegist = false;
        	editValue.setText("");
        }
        
        // 状態更新
        updateStatus();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// キーワード逆転
		MenuItem actionItem0 = menu.add(0, MENU_ITEM0, 0, getString(R.string.act_menu_reverse_key));
		actionItem0.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		//　最初に戻る
		MenuItem actionItem1 = menu.add(0, MENU_ITEM1, 0, getString(R.string.act_menu_btf));
		actionItem1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		//　データエクスポート
		MenuItem actionItem2 = menu.add(0, MENU_ITEM2, 0, getString(R.string.act_menu_export));
		actionItem2.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		//　データインポート
//		menu.add(0, MENU_ITEM3, 0, getString(R.string.act_menu_import));
		
		//　リセット
		MenuItem actionItem3 = menu.add(0, MENU_ITEM4, 0, getString(R.string.act_reset));
		actionItem3.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
        EditText editKey = (EditText)findViewById(R.id.editTextKeyword);
        EditText editValue = (EditText)findViewById(R.id.editTextValue);
        
		switch (item.getItemId()) {
			// キーワード逆転
			case MENU_ITEM0:
				if (!isReverse) {
					isReverse = true;
				}
				else {
					isReverse = false;
				}
				// データの取り直し
		        dataList = getAllData();
		        updateStatus();
	        	editValue.setText("");
	        	// 状態保存
	        	saveStatus();
				return true;
			//　最初に戻る
			case MENU_ITEM1:
				currentIndex = 0;
				editKey.setText("");
	        	editValue.setText("");
		        updateStatus();
				return true;
			//　データエクスポート
			case MENU_ITEM2:
				exportData();
				return true;
			//　データインポート
			case MENU_ITEM3:
				importData();
		        updateStatus();
				return true;
			//　リセット
			case MENU_ITEM4:
				reset();
				return true;
		}
		return true;
	}
	
	/**
	 * データ取得
	 * @return
	 */
	private List<WordEntity> getAllData() {
		WordDBHelper dbHelper = new WordDBHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cWord = db.query(WordDBHelper.DB_TABLE_WORD, new String[]{ "date","word1","word2" }, null, null, null, null, null);

		List<WordEntity> resuleList = new ArrayList<WordEntity>();
		
		while (cWord.moveToNext()) {
			WordEntity data = new WordEntity();
			data.setKey(cWord.getString(0));
			if (!isReverse) {
				data.setWord1(cWord.getString(1));
				data.setWord2(cWord.getString(2));
			} else {
				data.setWord1(cWord.getString(2));
				data.setWord2(cWord.getString(1));
			}
			resuleList.add(data);
		}
		
		cWord.close();
        db.close();
        dbHelper.close();
		
        return resuleList;
	}
	
	/**
	 * データ状態更新
	 */
	private void updateStatus() {
		
		// 画面部品取得
		Button btnFwd = (Button)findViewById(R.id.buttonFwd);
        Button btnRev = (Button)findViewById(R.id.buttonRev);
        Button btnSave = (Button)findViewById(R.id.buttonSave);
        Button btnReg = (Button)findViewById(R.id.buttonRegist);
        Button btnDisp = (Button)findViewById(R.id.buttonDisp);
        Button btnDelete = (Button)findViewById(R.id.buttonDelete);
        
        TextView txtStatus =  (TextView)findViewById(R.id.textViewStatus);
        
        // リバース状態の場合はメッセージを表示
        String status = "";
        if (isReverse) {
        	status = " " + getString(R.string.act_status_rev);
        }
        
        txtStatus.setText((currentIndex + 1) + "/" + dataList.size() + status);
        
        if (dataList.size() > 0) {
            EditText editKey =  (EditText)findViewById(R.id.editTextKeyword);
            editKey.setText(((WordEntity)dataList.get(currentIndex)).getWord1());
        	btnDisp.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnReg.setVisibility(View.VISIBLE);
        } else {
        	isSaveEnabled = true;
        	btnDisp.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            btnReg.setVisibility(View.GONE);
        }
 
        if ((currentIndex + 1) >= dataList.size()) {
            btnFwd.setClickable(false);
            btnFwd.setVisibility(View.GONE);
        } else {
            btnFwd.setClickable(true);	
            btnFwd.setVisibility(View.VISIBLE);
        }

        if (currentIndex == 0) {
        	btnRev.setClickable(false);
        	btnRev.setVisibility(View.GONE);
        } else {
        	btnRev.setClickable(true);	
        	btnRev.setVisibility(View.VISIBLE);
        }
        
        if (isSaveEnabled) {
        	btnSave.setClickable(true);
        	btnSave.setVisibility(View.VISIBLE);
        } else {
        	btnSave.setClickable(false);
        	btnSave.setVisibility(View.GONE);
        }
        
    	if (!isRegist) {
            btnReg.setText(getString(R.string.act_btn_new));
    	}
    	
    	// 状態保存
    	saveStatus();
    	
        // シークバーのデフォルト設定
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBarData);
        seekBar.setProgress(currentIndex);
        seekBar.setMax(dataList.size());
	}
	
	/**
	 * データ保存
	 */
	private void saveData() {
		
        EditText editKey = (EditText)findViewById(R.id.editTextKeyword);
        EditText editValue = (EditText)findViewById(R.id.editTextValue);
        
		WordDBHelper dbHelper = new WordDBHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		String id = null;
		if (isRegist) {
			id = String.valueOf(System.currentTimeMillis());
		} else {
			id = ((WordEntity)dataList.get(currentIndex)).getKey();
		}
		
		try {
			ContentValues values = new ContentValues();
			values.put("date", id);
			
			if (!isReverse) {
				values.put("word1", editKey.getText().toString());
				values.put("word2", editValue.getText().toString());
			} else {
				values.put("word2", editKey.getText().toString());
				values.put("word1", editValue.getText().toString());	
			}
		
			if (isRegist) {
				db.insert(WordDBHelper.DB_TABLE_WORD, null, values);
			} else {
				db.update(WordDBHelper.DB_TABLE_WORD, values, "date=" + id, null);	
			}
		}
		finally {
			db.close();
			dbHelper.close();
		}
		
		isRegist = false;
        Button btnReg = (Button)findViewById(R.id.buttonRegist);
        btnReg.setText(getString(R.string.act_btn_new));
        
        // ダイアログの表示
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setMessage(getString(R.string.act_msg_data_saved));
		dlg.setPositiveButton("OK", null);
		dlg.show();
        
		// データ再取得
        dataList = getAllData();
	}
	
	/**
	 * 状態保存
	 */
	private void saveStatus() {
		WordDBHelper dbHelper = new WordDBHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		try {
			ContentValues values = new ContentValues();
			values.put("word","word");
			values.put("currentindex",currentIndex);
			values.put("reverse",isReverse);
			int row = db.update(WordDBHelper.DB_TABLE_STATUS, values, "word=word", null);
			if (row == 0) db.insert(WordDBHelper.DB_TABLE_STATUS, null, values);
		}
		finally {
			db.close();
			dbHelper.close();
		}
	}
	
	/**
	 * 状態取得
	 */
	private void getStatus() {
		WordDBHelper dbHelper = new WordDBHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cStatus = db.query(WordDBHelper.DB_TABLE_STATUS, new String[]{ "currentindex","reverse" }, null, null, null, null, null);
		
		if (!cStatus.moveToNext()) return;
		
		currentIndex = Integer.parseInt(cStatus.getString(0));
		isReverse = Boolean.valueOf(cStatus.getString(1));
		
		cStatus.close();
        db.close();
        dbHelper.close();
	}
	
	/**
	 * データ削除
	 */
	private void delete() {
		// リスナー登録
		DialogInterface.OnClickListener listnerDel = new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				if (DialogInterface.BUTTON_POSITIVE == which) {
					deleteData();
				}
			}
		};
		// 確認ダイアログ表示
		AlertDialog.Builder dlgDel = new AlertDialog.Builder(this);
		dlgDel.setMessage(getString(R.string.act_msg_data_delete_conf));
		dlgDel.setPositiveButton("Yes", listnerDel);
		dlgDel.setNegativeButton("No", listnerDel);
		dlgDel.show();
	}

	/**
	 * リセット
	 */
	private void reset() {
		// リスナー登録
		DialogInterface.OnClickListener listnerDel = new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				if (DialogInterface.BUTTON_POSITIVE == which) {
					deleteData();
				}
			}
		};
		// 確認ダイアログ表示
		AlertDialog.Builder dlgDel = new AlertDialog.Builder(this);
		dlgDel.setMessage(getString(R.string.act_msg_data_alldelete_conf));
		dlgDel.setPositiveButton("Yes", listnerDel);
		dlgDel.setNegativeButton("No", listnerDel);
		dlgDel.show();
	}
	
	/**
	 * リセット処理
	 */
	public void deleteData() {
			
		WordDBHelper dbHelper = new WordDBHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		try {

			if (isAllDelete) {
				// 全件削除
				db.delete(WordDBHelper.DB_TABLE_WORD, null, null);
				currentIndex = 0;
				isAllDelete = false;
				isRegist = true;
			} else {
				// 単発削除
				String id = ((WordEntity)dataList.get(currentIndex)).getKey();
				db.delete(WordDBHelper.DB_TABLE_WORD, "date=" + id, null);
				
				if (currentIndex > 0) {
					currentIndex--;
				}
			}
		}
		finally {
			db.close();
			dbHelper.close();
		}
		
        // ダイアログの表示
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setMessage(getString(R.string.act_msg_data_delete));
		dlg.setPositiveButton("OK", null);
		dlg.show();
		
		// 画面クリア
        EditText editValue = (EditText)findViewById(R.id.editTextValue);
        EditText editKey = (EditText)findViewById(R.id.editTextKeyword);
        editKey.setText("");
        editValue.setText("");
        
		// データの取り直し
        dataList = getAllData();
        // 状態更新
        updateStatus();
		
	}
	
	/**
	 * データエクスポート
	 */
	private void exportData() {
		
		if (dataList == null || dataList.size() == 0) {
			return;
		}
		
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
        
		String text = "";
		
        // データリスト作成
		for (Iterator<WordEntity> iterator = dataList.iterator(); iterator.hasNext();) {
			WordEntity data= (WordEntity) iterator.next();
			text = text + data.getWord1()+ "," + data.getWord2() + "\n";
		}
		
		intent.putExtra(Intent.EXTRA_TEXT, text);  
		startActivity(Intent.createChooser(intent, getString(R.string.act_menu_export)));
	}

	/**
	 * データインポート
	 */
	private void importData() {
	    FileInputStream input = null;
	    BufferedReader reader = null;
	    
		WordDBHelper dbHelper = new WordDBHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		try {
		    // ストリームを開く  
		    input = this.openFileInput(FILE_NAME);
		    // 読み込み  
		    reader = new BufferedReader(new InputStreamReader(input));  
		    String line = null;
		    
		    while ((line = reader.readLine()) != null) {
				ContentValues values = new ContentValues();
				values.put("date", String.valueOf(System.currentTimeMillis()));
				
			       // 1行をデータの要素に分割
		        StringTokenizer st = new StringTokenizer(line, ",");
		        
		        if (st.hasMoreTokens()) {
		        	 values.put("word1", st.nextToken());
		        }
		        if (st.hasMoreTokens()) {
		        	 values.put("word2", st.nextToken());
		        }
		        
				db.insert(WordDBHelper.DB_TABLE_WORD, null, values);
		    }
		}
		catch (Throwable th) {
	        // ダイアログの表示
			AlertDialog.Builder dlg = new AlertDialog.Builder(this);
			dlg.setMessage(getString(R.string.act_msg_file_error) +  " " + th.getLocalizedMessage());
			dlg.setPositiveButton("OK", null);
			dlg.show();
			return;
		}
		finally{
			try {
				if (input != null) {
					input.close();
				}
				if (reader != null) {
					reader.close();
				}
				
				db.close();
			}
			catch (Throwable th) {
		        // ダイアログの表示
				AlertDialog.Builder dlg = new AlertDialog.Builder(this);
				dlg.setMessage(getString(R.string.act_msg_file_error) + " " + th.getLocalizedMessage());
				dlg.setPositiveButton("OK", null);
				dlg.show();
				return;
			}
		}
		
        // ダイアログの表示
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setMessage(getString(R.string.act_msg_import_ok));
		dlg.setPositiveButton("OK", null);
		dlg.show();
		return;
	}
	
	/* (non-Javadoc)
	 * @see android.widget.SeekBar.OnSeekBarChangeListener#onProgressChanged(android.widget.SeekBar, int, boolean)
	 */
	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        currentIndex = arg1;
        
    	isSaveEnabled = false;
    	if (dataList != null && dataList.isEmpty()) {
    		isRegist = true;
    	}
        
        EditText editValue = (EditText)findViewById(R.id.editTextValue);
    	editValue.setText("");
        // 状態更新
        updateStatus();
	}

	/* (non-Javadoc)
	 * @see android.widget.SeekBar.OnSeekBarChangeListener#onStartTrackingTouch(android.widget.SeekBar)
	 */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	/* (non-Javadoc)
	 * @see android.widget.SeekBar.OnSeekBarChangeListener#onStopTrackingTouch(android.widget.SeekBar)
	 */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}