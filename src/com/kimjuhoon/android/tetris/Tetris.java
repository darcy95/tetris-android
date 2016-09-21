package com.kimjuhoon.android.tetris;

import com.kimjuhoon.android.tetris.TetrisView.TetrisThread; 

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class Tetris extends Activity {
	private static final int MENU_PREV_BG = 0;
	private static final int MENU_NEXT_BG = 1;
	private TetrisThread mTetrisThread;		
	
	private TetrisView mTetrisView;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // No Title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
                        
        //	Set layout 
        setContentView(R.layout.tetris_layout);
        mTetrisView = (TetrisView) findViewById(R.id.tetris);
        mTetrisView.setStatusText((TextView) findViewById(R.id.text));
        mTetrisThread = mTetrisView.getThread();
        mTetrisThread.startGame();
        mTetrisView.showState();        
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, MENU_PREV_BG, 0, R.string.menu_prev_bg);
		menu.add(0, MENU_NEXT_BG, 0, R.string.menu_next_bg);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
		case MENU_PREV_BG:
			mTetrisThread.prevBackground();
			return true;			
		case MENU_NEXT_BG:
			mTetrisThread.nextBackground();
			return true;
		}
		
		return false;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mTetrisThread.doPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
    
}
