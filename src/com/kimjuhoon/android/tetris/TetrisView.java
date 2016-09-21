package com.kimjuhoon.android.tetris;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

public class TetrisView extends SurfaceView implements SurfaceHolder.Callback {
	/** Constants */
	private static final int BRICK_SIZE = 28;
	private static int SCREEN_WIDTH = 240;
	private static int SCREEN_HEIGHT = 320;
	private static int BOARD_OFFSET_WIDTH = 0;
	private static int BOARD_OFFSET_HEIGHT = 0;
	
	/** State */
	private static final int STATE_MAIN = 0;
	private static final int STATE_GAME = 1;
	private static final int STATE_PAUSE = 2;
	private static final int STATE_LOSE = 3;
	private static final int STATE_RESULT = 4;
	
	/** Sound Effect */
	private static final int SOUND_DOWN = 0;
	private static final int SOUND_EXPLODE = 1;
	private static final int SOUND_HURRYUP = 2;
	private static final int SOUND_LINE = 3;
	private static final int SOUND_MAGIC = 4;
	private static final int SOUND_MOVE = 5;
	private static final int SOUND_OVER = 6;
	private static final int SOUND_SET1 = 7;
	private static final int SOUND_SET2 = 8;
	private static final int SOUND_SET3 = 9;
	private static final int SOUND_SPRING = 10;
	private static final int SOUND_START = 11;
	private static final int SOUND_TURN = 12;
	private static final int SOUND_WRONGMOVE = 13;

	private TetrisExt mTetris = new TetrisExt();

	private int mState = STATE_MAIN;
	
    /** Handle to the application context, used to e.g. fetch Drawables. */
    private Context mContext;
    
    /** Paints */
    private Paint paintBoard;
    
    /** Scratch rect object. */
    private RectF mRect;    
    
	/** Drawables */
	private Drawable[] mImage;

	/** Drawables to use as backgrounds of the canvas */
    private Bitmap mBackgroundImage;
    
    /** The ID of a current background image */
	private int mNumBackground;
    
	/** Sound Player */
	private MediaPlayer[] mSound;
	
	class TetrisExt extends JHGameTetris {
		@Override
		public void OnGameStartAct() {
			playAudio(SOUND_START);
		}

		@Override
		public void OnBlockCopyN2CAct() {

		}

		@Override
		public void OnBlockDownAct() {

		}

		@Override
		public void OnBlockMoveAct() {
			playAudio(SOUND_MOVE);
		}

		@Override
		public void OnBlockTurnAct() {
			playAudio(SOUND_TURN);
		}

		@Override
		public void OnGameOverAct() {
			playAudio(SOUND_OVER);
		}

		@Override
		public void OnImpossibleAct() {
			playAudio(SOUND_WRONGMOVE);
		}

		@Override
		public void OnLineCompleteAct(int line) {
			playAudio(SOUND_LINE);
		}

		@Override
		public void OnSettleAct() {
			playAudio(SOUND_SET1);
		}		
	}
	
	class TetrisThread extends Thread {
        /** Indicate whether the surface has been created & is ready to draw */
        private boolean mRun = false;
		
		/** Message handler used by thread to interact with TextView */
        private Handler mHandler;
		
        /** Handle to the surface manager object we interact with */
		private SurfaceHolder mSurfaceHolder;
       
		public TetrisThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
			mSurfaceHolder = surfaceHolder;
			mHandler = handler;
			
			mNumBackground = 0;
			changeBackground(mNumBackground);
		}

		public void startGame() {
            mTetris.OnStart();
            mTetris.m_nMovePoint = 25;
            setGameState(STATE_MAIN);
		}
		
		public void nextBackground() {
			if (mNumBackground == 10) {
				mNumBackground = 1;
			} else {
				mNumBackground++;				
			}
			
			changeBackground(mNumBackground);
		}
		
		public void prevBackground() {
			if (mNumBackground == 1) {
				mNumBackground = 10;
			} else {
				mNumBackground--;				
			}

			changeBackground(mNumBackground);			
		}
		
		public void changeBackground(int numImage) {
			Resources res = mContext.getResources();
			
			if (numImage == 0) {
				mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.bg00);
			} else if (numImage == 1) {
				mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.bg01);				
			} else if (numImage == 2) {
				mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.bg02);				
			} else if (numImage == 3) {
				mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.bg03);				
			} else if (numImage == 4) {
				mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.bg04);				
			} else if (numImage == 5) {
				mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.bg05);				
			} else if (numImage == 6) {
				mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.bg06);				
			} else if (numImage == 7) {
				mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.bg07);				
			} else if (numImage == 8) {
				mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.bg08);				
			} else if (numImage == 9) {
				mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.bg09);				
			} else if (numImage == 10) {
				mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.bg10);				
			}
		}
		
		@Override
		public void run() {
			while (mRun) {
				Canvas c = mSurfaceHolder.lockCanvas(null);
	
				try {
                	synchronized (mSurfaceHolder) {
        				doUpdate();                		
                		doDraw(c);
                	}
	            } finally {
	                if (c != null) {
	                    mSurfaceHolder.unlockCanvasAndPost(c);
	                }
	            }
			}
		}
	
		public int getGameState() {
			return mState;
		}

		public void setGameState(int state) {
			switch (state) {
			case STATE_MAIN:
				setStatusMessage("Press the center button");
				break;
			case STATE_GAME:
				setStatusMessage("");
				break;
			case STATE_PAUSE:
				setStatusMessage("Paused");
				break;
			case STATE_LOSE:
//				setStatusMessage("Game over");
				break;
			case STATE_RESULT:
//				setStatusMessage("Score:");
				break;
			}
			
			mState = state;
		}

		public void togglePause() {
			if (mState != STATE_PAUSE) {
				doPause();
			} else {
				cancelPause();				
			}
		}
		
		public void doPause() {
			setGameState(STATE_PAUSE);
		}
		
		public void cancelPause() {
			setGameState(STATE_GAME);
		}
		
		private void doDraw(Canvas canvas) {
            // Draw the background image. Operations on the Canvas accumulate
            // so this is like clearing the screen.
            canvas.drawBitmap(mBackgroundImage, 0, 0, null);
			
            canvas.save();
						
            /*
			Paint paint = new Paint();
			paint.setColor(Color.GRAY);
			canvas.drawRect(0, 0, 50, 50, paint);
			paint.setColor(Color.GREEN);
			canvas.drawRect(40, 40, 100, 100, paint);
			
			Resources res = mContext.getResources();			
			mImage = res.getDrawable(R.drawable.icon);			
            mImage.setBounds(10, 50, 58, 108);
            mImage.draw(canvas);
            */
            
            drawBoundary(canvas);
            drawBlock(canvas);
            drawBoard(canvas);            
            
            canvas.restore();
		}
			
		private void doUpdate() {			
			switch (mState) {
			case STATE_MAIN:
				doStateMain();
				break;
			case STATE_GAME:
				doStateGame();
				break;
			case STATE_PAUSE:
				doStatePause();
				break;
			case STATE_LOSE:
				doStateLose();
				break;
			case STATE_RESULT:
				doStateResult();
				break;
			}			
		}
		
		private void doStateMain() {
			// TODO Auto-generated method stub
		}
		
		private void doStateGame() {
			// TODO Auto-generated method stub
			mTetris.OnUpdate();
			
			if (mTetris.m_bGameBegin == false) {
				setGameState(STATE_LOSE);
			}
		}
		
		private void doStatePause() {
			// TODO Auto-generated method stub
		}		

		private void doStateLose() {
			// TODO Auto-generated method stub
		}
		
        private void doStateResult() {
			// TODO Auto-generated method stub
		}
        
		public boolean isRun() {
			return mRun;
		}

		public void setRun(boolean run) {
			mRun = run;
		}		
		
        boolean doKeyDown(int keyCode, KeyEvent msg) {
        	switch (mState) {
        	case STATE_MAIN:
	            synchronized (mSurfaceHolder) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    	setGameState(STATE_GAME);
                        return true;
                    }
	            }
        		return true;
        		
        	case STATE_GAME:
	            synchronized (mSurfaceHolder) {
                    if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) || (keyCode == KeyEvent.KEYCODE_DPAD_UP)) {
                    	mTetris.OnRotate();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    	mTetris.OnLeft();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    	mTetris.OnRight();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    	mTetris.OnDown();
                        return true;
                    }       	
	            }
        		return true;
        		
        	case STATE_PAUSE:
        		return true;
        		
        	case STATE_LOSE:
        		return true;
        		
        	case STATE_RESULT:
        		return true;
        	}	            
	            
        	return false;
		}
        
        public boolean doTouchEvent(MotionEvent event) {
        	switch (mState) {
        	case STATE_MAIN:
        		return true;
        		
        	case STATE_GAME:
	            synchronized (mSurfaceHolder) {
	            	togglePause();       	
	            }
        		return true;
        		
        	case STATE_PAUSE:
	            synchronized (mSurfaceHolder) {
	            	togglePause();       	
	            }        		
        		return true;
        		
        	case STATE_LOSE:
        		return true;
        		
        	case STATE_RESULT:
        		return true;
        	}
        	
        	return false;
        }
		
		public void drawBlock(Canvas canvas) {
			int left, top, right, bottom;
			
			for (int i = 0 ; i < mTetris.BLOCK_WIDTH ; i++)	{
				for (int j = 0 ; j < mTetris.BLOCK_HEIGHT ; j++) {	
					if (mTetris.m_BrkCurr[i][j].nFill == mTetris.BRICK_FILLED) {
						if (mTetris.OnCheckValidArea(mTetris.m_nPosX + i, mTetris.m_nPosY + j))	{
							left = BOARD_OFFSET_WIDTH + (mTetris.m_nPosX + i) * BRICK_SIZE;
							top = BOARD_OFFSET_HEIGHT + (mTetris.m_nPosY + j) * BRICK_SIZE;
							right = left + BRICK_SIZE;
							bottom = top + BRICK_SIZE;
							
				            mImage[mTetris.m_BrkCurr[i][j].nColor].setBounds(left, top, right, bottom);
				            mImage[mTetris.m_BrkCurr[i][j].nColor].draw(canvas);
						}
					}
				}
			}
		}
		
		void drawBoard(Canvas canvas) {
			int left, top, right, bottom;			
			
			for (int i = 0 ; i < mTetris.TOTAL_BRICK_NUM_X ; i++) {
				for (int j = 0 ; j < mTetris.TOTAL_BRICK_NUM_Y ; j++) {
					if (mTetris.m_BrkBoard[i][j].nFill == mTetris.BRICK_FILLED)	{
						left = BOARD_OFFSET_WIDTH + i * BRICK_SIZE;
						top = BOARD_OFFSET_HEIGHT + j * BRICK_SIZE;
						right = left + BRICK_SIZE;
						bottom = top + BRICK_SIZE;
						
			            mImage[mTetris.m_BrkBoard[i][j].nColor].setBounds(left, top, right, bottom);
			            mImage[mTetris.m_BrkBoard[i][j].nColor].draw(canvas);			
					}
				}
			}
		}
		
		void drawBoundary(Canvas canvas) {
			mRect.set(BOARD_OFFSET_WIDTH, BOARD_OFFSET_HEIGHT, BOARD_OFFSET_WIDTH + (mTetris.TOTAL_BRICK_NUM_X * BRICK_SIZE),  BOARD_OFFSET_HEIGHT + (mTetris.TOTAL_BRICK_NUM_Y * BRICK_SIZE));
			canvas.drawRect(mRect, paintBoard);
		}
	} // End of the class TetrisThread
	
    private void playAudio(int sound) {
   		mSound[sound].start();
    }
	
	public void loadImageResources(Resources res) {
        mImage = new Drawable[7];
        
        mImage[0] = res.getDrawable(R.drawable.brick1);
        mImage[1] = res.getDrawable(R.drawable.brick2);
        mImage[2] = res.getDrawable(R.drawable.brick3);
        mImage[3] = res.getDrawable(R.drawable.brick4);
        mImage[4] = res.getDrawable(R.drawable.brick5);
        mImage[5] = res.getDrawable(R.drawable.brick6);
        mImage[6] = res.getDrawable(R.drawable.brick7);			
	}
	
	public void loadSoundResources() {
		mSound = new MediaPlayer[14];
		
		mSound[SOUND_DOWN] = MediaPlayer.create(mContext, R.raw.down);
		mSound[SOUND_EXPLODE] = MediaPlayer.create(mContext, R.raw.explode);
		mSound[SOUND_HURRYUP] = MediaPlayer.create(mContext, R.raw.hurryup);
		mSound[SOUND_LINE] = MediaPlayer.create(mContext, R.raw.line);
		mSound[SOUND_MAGIC] = MediaPlayer.create(mContext, R.raw.magic);
		mSound[SOUND_MOVE] = MediaPlayer.create(mContext, R.raw.move);
		mSound[SOUND_OVER] = MediaPlayer.create(mContext, R.raw.over);
		mSound[SOUND_SET1] = MediaPlayer.create(mContext, R.raw.set1);
		mSound[SOUND_SET2] = MediaPlayer.create(mContext, R.raw.set2);
		mSound[SOUND_SET3] = MediaPlayer.create(mContext, R.raw.set3);
		mSound[SOUND_SPRING] = MediaPlayer.create(mContext, R.raw.spring);
		mSound[SOUND_START] = MediaPlayer.create(mContext, R.raw.start);
		mSound[SOUND_TURN] = MediaPlayer.create(mContext, R.raw.turn);
		mSound[SOUND_WRONGMOVE] = MediaPlayer.create(mContext, R.raw.wrongmove);
	}	
	
	private TetrisThread thread;
	
	public TetrisThread getThread() {
		return thread;
	}

	public void setThread(TetrisThread thread) {
		this.thread = thread;
	}
	
	private TextView mStatusText;
	
	
	/*private RefreshHandler mRedrawHandler = new RefreshHandler();

	class RefreshHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			TetrisView.this.doUpdate();
			TetrisView.this.invalidate();
		}
		
		public void sleep(long delayMilsec) {
			this.removeMessages(0);
			this.sendMessageDelayed(this.obtainMessage(0), delayMilsec);
		}
	}*/	
		
	public TetrisView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub		
		this.initTetrisView(context);
	}
	
	public TetrisView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.initTetrisView(context);		
	}
	
	private void initTetrisView(Context context) {
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		mContext = context;
		
		Resources res = mContext.getResources();
		loadImageResources(res);
		loadSoundResources();
		
		paintBoard = new Paint();
		paintBoard.setAntiAlias(true);
		paintBoard.setARGB(70, 239, 239, 216);
		
		mRect = new RectF(0,0,0,0);
		
		thread = new TetrisThread(holder, context, new Handler() {
			@Override
            public void handleMessage(Message m) {
                mStatusText.setVisibility(m.getData().getInt("viz"));
                mStatusText.setText(m.getData().getString("text"));
            }
        });
		
		setFocusable(true);
	}
	
	public void showState() {
		if (getStatusText().getVisibility() == INVISIBLE) {
			getStatusText().setVisibility(View.VISIBLE);
		}
	}
	
	public void hideState() {
		if (getStatusText().getVisibility() == VISIBLE) {
			getStatusText().setVisibility(View.INVISIBLE);
		}
	}
	
	public TextView getStatusText() {
		return mStatusText;
	}

	public void setStatusText(TextView statusText) {
			mStatusText = statusText;
	}
	
	public void setStatusMessage(CharSequence msg) {
		mStatusText.setText(msg);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		SCREEN_WIDTH = width;
		SCREEN_HEIGHT = height;
		
		BOARD_OFFSET_WIDTH = (int) Math.floor((double) (SCREEN_WIDTH - (BRICK_SIZE * mTetris.TOTAL_BRICK_NUM_X)) / 2);
		BOARD_OFFSET_HEIGHT = SCREEN_HEIGHT - (BRICK_SIZE * mTetris.TOTAL_BRICK_NUM_Y);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRun(true);
		thread.start();		
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.setRun(false);
		
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }	
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		thread.doKeyDown(keyCode, event);
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		thread.doTouchEvent(event);		
		return super.onTouchEvent(event);
	}

	
}
