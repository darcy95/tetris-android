package com.kimjuhoon.android.tetris;

public class JHGameTetris {
	public static final int TOTAL_BRICK_NUM_X = 10;	//	
	public static final int TOTAL_BRICK_NUM_Y = 14;	//	
	public static final int INIT_SPEED = 20;		//	
	public static final int BLOCK_KIND = 7;			//	
	public static final int BLOCK_STATE = 4;		//	
	public static final int BLOCK_WIDTH = 4;		//
	public static final int BLOCK_HEIGHT = 4;		//
	public static final int MOVE_TURN = 0;			//	
	public static final int MOVE_DOWN = 1;			//	
	public static final int MOVE_LEFT = 2;			//	
	public static final int MOVE_RIGHT = 3;			//	
	public static final int BRICK_EMPTY = 0;		//	
	public static final int BRICK_FILLED = 1;		//	
	
	private static int g_nBlockKinds[][][] = 	
	{
		{	
			{0,1,0,0},
			{0,1,0,0},
			{0,1,0,0},
			{0,1,0,0}
		},	
		{
			{0,0,0,0},
			{0,1,1,0},
			{0,1,1,0},
			{0,0,0,0}
		},
		{
			{0,1,0,0},
			{0,1,1,0},
			{0,0,1,0},
			{0,0,0,0}
		},
		{
			{0,0,1,0},
			{0,1,1,0},
			{0,1,0,0},
			{0,0,0,0}
		},
		{
			{0,1,0,0},
			{0,1,0,0},
			{0,1,1,0},
			{0,0,0,0}
		},
		{
			{0,0,1,0},
			{0,0,1,0},
			{0,1,1,0},
			{0,0,0,0}
		},
		{
			{0,1,0,0},
			{0,1,1,0},
			{0,1,0,0},
			{0,0,0,0}
		}
	};	
	
	class Brick {
		public int nColor;
		public int nFill;

		public Brick() {
			nColor = 0;
			nFill = BRICK_EMPTY;
		}
		
		public Brick(int color, int fill) {
			nColor = color;
			nFill = fill;
		}
	}	
	
	public Brick m_BrkBoard[][] = new Brick[TOTAL_BRICK_NUM_X][TOTAL_BRICK_NUM_Y];		
	public Brick m_BrkCurr[][] = new Brick[BLOCK_WIDTH][BLOCK_HEIGHT];					
	public Brick m_BrkNext[][] = new Brick[BLOCK_WIDTH][BLOCK_HEIGHT];					
	public Brick m_BrkTemp[][] = new Brick[BLOCK_WIDTH][BLOCK_HEIGHT];					
	public int m_nPosX;				
	public int m_nPosY;				
	public int m_nTmpPosX;			
	public int m_nTmpPosY;			
	public int m_nCurrBlockKind;	
	public int m_nNextBlockKind;	
	public int m_nTickCount;		
	public int m_nMovePoint;		
	public int m_nScore;			
	public int m_nLine;				
	public boolean m_bGameBegin;	

	public void OnInit() {
		int i = 0;
		int j = 0;
		
		for (i = 0 ; i < TOTAL_BRICK_NUM_X ; i++) {
			for (j = 0 ; j < TOTAL_BRICK_NUM_Y ; j++) {
				m_BrkBoard[i][j] = new Brick();
			}
		}			

		for (i = 0 ; i < BLOCK_WIDTH ; i++) {
			for (j = 0 ; j < BLOCK_HEIGHT ; j++) {
				m_BrkCurr[i][j] = new Brick();
				m_BrkNext[i][j] = new Brick();
				m_BrkTemp[i][j] = new Brick();				
			}
		}
	}
	
	public void OnReset() {
		int i = 0;
		int j = 0;
	
		for (i = 0 ; i < TOTAL_BRICK_NUM_X ; i++)
		{
			for (j = 0 ; j < TOTAL_BRICK_NUM_Y ; j++)
			{
				m_BrkBoard[i][j].nFill = BRICK_EMPTY;
				m_BrkBoard[i][j].nColor = 0;
			}
		}

		for (i = 0 ; i < BLOCK_WIDTH ; i++)
		{
			for (j = 0 ; j < BLOCK_HEIGHT ; j++)
			{
				m_BrkCurr[i][j].nFill = BRICK_EMPTY;
				m_BrkCurr[i][j].nColor = 0;

				m_BrkNext[i][j].nFill = BRICK_EMPTY;
				m_BrkNext[i][j].nColor = 0;

				m_BrkTemp[i][j].nFill = BRICK_EMPTY;
				m_BrkTemp[i][j].nColor = 0;			
			}
		}

		m_nPosX	= 0;
		m_nPosY	= 0;
		m_nTmpPosX = 0;
		m_nTmpPosY = 0;
		m_nCurrBlockKind = 0;
		m_nNextBlockKind = 0;
		m_nTickCount = 0;
		m_nMovePoint = INIT_SPEED;
		m_nScore = 0;
		m_nLine	= 0;
		m_bGameBegin = false;	
	}
	
	public void OnStart() {
		OnInit();
		OnReset();
		m_bGameBegin = true;
		m_nNextBlockKind = OnSetNext();
		OnBlockCopyN2C();
		m_nNextBlockKind = OnSetNext();
		OnGameStartAct();
	}
	
	public void OnExit() {
		m_bGameBegin = false;	
	}
	
	public void OnUpdate() {
		if (m_bGameBegin)
		{
			if (m_nTickCount < m_nMovePoint)
			{
				m_nTickCount++;
			}
			else
			{
				m_nTickCount = 0;
				OnDown();
			}
		}
	}
	
	public void OnBlockCopyC2T() {		
		for (int i = 0 ; i < BLOCK_WIDTH ; i++)
		{
			for (int j = 0 ; j < BLOCK_HEIGHT ; j++)
			{
				m_BrkTemp[i][j].nFill = m_BrkCurr[i][j].nFill;
				m_BrkTemp[i][j].nColor = m_BrkCurr[i][j].nColor;
			}
		}

		m_nTmpPosX = m_nPosX;
		m_nTmpPosY = m_nPosY;		
	}
	
	public void OnBlockCopyT2C() {
		for (int i = 0 ; i < BLOCK_WIDTH ; i++)
		{
			for (int j = 0 ; j < BLOCK_HEIGHT ; j++)
			{
				m_BrkCurr[i][j].nFill = m_BrkTemp[i][j].nFill;
				m_BrkCurr[i][j].nColor = m_BrkTemp[i][j].nColor;
			}
		}

		m_nPosX = m_nTmpPosX;
		m_nPosY = m_nTmpPosY;
	}
	
	public void OnBlockCopyN2C() {
		for (int i = 0 ; i < BLOCK_WIDTH ; i++)
		{
			for (int j = 0 ; j < BLOCK_HEIGHT ; j++)
			{
				m_BrkCurr[i][j].nFill = m_BrkNext[i][j].nFill;
				m_BrkCurr[i][j].nColor = m_BrkNext[i][j].nColor;
			}
		}

		m_nPosX = (TOTAL_BRICK_NUM_X / 2) - (BLOCK_WIDTH / 2);
		m_nPosY = 1 - BLOCK_HEIGHT;

		m_nCurrBlockKind = m_nNextBlockKind;

		OnBlockCopyN2CAct();
	}
	
	public void OnSettle() {
		for (int i = 0 ; i < BLOCK_WIDTH ; i++)
		{
			for (int j = 0 ; j < BLOCK_HEIGHT ; j++)
			{
				if (OnCheckValidArea(m_nPosX + i, m_nPosY + j))
				{
					if (m_BrkCurr[i][j].nFill == BRICK_FILLED)
					{
						m_BrkBoard[m_nPosX + i][m_nPosY + j].nFill = m_BrkCurr[i][j].nFill;
						m_BrkBoard[m_nPosX + i][m_nPosY + j].nColor = m_BrkCurr[i][j].nColor;
						m_BrkCurr[i][j].nFill  = BRICK_EMPTY;
						m_BrkCurr[i][j].nColor = 0;
					}
				}
			}
		}

		OnSettleAct();
	}
	
	public int OnSetNext() {
		// Random하게 1종류의 블록을 선택한다.
		int nNextBlock = (int) ((Math.random() * 1000) % 7);

		// 다음 블록의 배열에 결정된 블록을 채워 준다.
		return OnSetNext(nNextBlock);
	}
	
	public int	OnSetNext(int nKind) {
		// 다음 블록의 배열에 결정된 블록을 채워 준다.
		for (int i = 0 ; i < BLOCK_WIDTH ; i++)
		{
			for (int j = 0 ; j < BLOCK_HEIGHT ; j++)
			{
				m_BrkNext[i][j].nFill  = g_nBlockKinds[nKind][i][j];
				m_BrkNext[i][j].nColor = nKind;
			}
		}

		return nKind;
	}
	
	public boolean OnDown() {
		boolean	bReturn = true;

		if (m_bGameBegin)
		{
			OnBlockCopyC2T();

			m_nPosY++;

			if (OnCheckCollision())
			{
				bReturn = false;
				OnBlockCopyT2C();
				OnSettle();
				OnCheckLine();

				if (OnCheckOver())
				{
					OnGameOverAct();
					OnExit();
				}
				else
				{
					OnBlockCopyN2C();
					m_nNextBlockKind = OnSetNext();
				}
			}
			else
			{
				OnBlockDownAct();
			}
		}

		return	bReturn;
	}
	
	public boolean	OnLeft() {
		boolean	bReturn = true;

		if (m_bGameBegin)
		{
			OnBlockCopyC2T();
			m_nPosX--;

			if (OnCheckCollision())
			{
				OnBlockCopyT2C();
				bReturn = false;
				OnImpossibleAct();
			}
			else
			{
				OnBlockMoveAct();
			}
		}

		return	bReturn;
	}
	
	public boolean OnRight() {
		boolean	bReturn = true;

		if (m_bGameBegin)
		{
			OnBlockCopyC2T();
			m_nPosX++;

			if (OnCheckCollision())
			{
				OnBlockCopyT2C();
				bReturn = false;
				OnImpossibleAct();
			}
			else
			{
				OnBlockMoveAct();
			}
		}

		return	bReturn;
	}
	
	
	public boolean OnRotate() {
		boolean	bReturn = true;

		if (m_bGameBegin)
		{
			OnBlockCopyC2T();

			for (int i = 0 ; i < BLOCK_WIDTH ; i++)
			{
				for (int j = 0 ; j < BLOCK_HEIGHT ; j++)
				{
					m_BrkCurr[i][j].nFill	= m_BrkTemp[j][BLOCK_HEIGHT - 1 - i].nFill;
					m_BrkCurr[i][j].nColor	= m_BrkTemp[j][BLOCK_HEIGHT - 1 - i].nColor;
				}
			}

			if (OnCheckCollision())
			{
				OnBlockCopyT2C();
				bReturn = false;
				OnImpossibleAct();
			}
			else
			{
				OnBlockTurnAct();
			}
		}

		return	bReturn;
	}
	
	public int	OnCheckLine() {
		int	nReturn = 0;

		for (int i = 0 ; i < TOTAL_BRICK_NUM_Y ; i++)
		{
			if (OnCheckComplete(i))
			{
				OnArrangeLine(i);
				nReturn++;
			}
		}

		if (nReturn > 0)
		{
			m_nScore = m_nScore + nReturn + (nReturn - 1);
		}

	 	m_nLine = m_nLine + nReturn;

		return	nReturn;
	}
	
	public int	OnCheckBrick(int nLine) {
		int nReturn = 0;

		for (int i = 0 ; i < TOTAL_BRICK_NUM_X ; i++)
		{
			if (m_BrkBoard[i][nLine].nFill == BRICK_FILLED)
			{
				nReturn++;
			}
		}

		return	nReturn;
	}
	
	public int OnCheckFirst() {
		int nReturn = -1;

		for (int i = 0 ; i < TOTAL_BRICK_NUM_Y ; i++)
		{
			if (!OnCheckLineEmpty(i))
			{
				nReturn = i;
				break;
			}
		}

		return nReturn;
	}
	
	public boolean OnCheckLineEmpty(int nLine) {
		boolean bReturn = true;

		for (int i = 0 ; i < TOTAL_BRICK_NUM_X ; i++)
		{
			if (m_BrkBoard[i][nLine].nFill == BRICK_FILLED)
			{
				bReturn = false;
				break;
			}
		}

		return bReturn;
	}
	
	public boolean OnCheckOver() {
		boolean	bReturn = false;

		if (m_nPosY < 0)
		{
			bReturn = true;
		}

		return	bReturn;
	}
	
	public boolean OnCheckCollision() {
		int		i = 0;
		int		j = 0;
		boolean	bReturn = false;

		for (i = 0 ; i < BLOCK_WIDTH ; i++)
		{
			for (j = 0 ; j < BLOCK_HEIGHT ; j++)
			{
				if (OnCheckValidArea(m_nPosX + i, m_nPosY + j))
				{
					if ((m_BrkBoard[m_nPosX + i][m_nPosY + j].nFill == 1) && (m_BrkCurr[i][j].nFill == 1))
					{
						bReturn = true;
					}
				}
			}
		}

		for (i = 0 ; i < BLOCK_WIDTH ; i++)
		{
			for (j = 0 ; j < BLOCK_HEIGHT ; j++)
			{
				if (m_BrkCurr[i][j].nFill == BRICK_FILLED)
				{
					if ((m_nPosX + i) < 0)
					{
						bReturn = true;
					}

					if ((m_nPosX + i) >= TOTAL_BRICK_NUM_X)
					{
						bReturn = true;
					}

					if ((m_nPosY + j) >= TOTAL_BRICK_NUM_Y)
					{
						bReturn = true;
					}
				}
			}
		}

		return	bReturn;
	}
	
	public boolean OnCheckComplete(int nLine) {
		boolean	bReturn = true;

		for (int i = 0 ; i < TOTAL_BRICK_NUM_X ; i++)
		{
			if (m_BrkBoard[i][nLine].nFill == BRICK_EMPTY)
			{
				bReturn = false;
				break;
			}
		}

		if (bReturn)
		{
			OnLineCompleteAct(nLine);
		}

		return bReturn;		
	}
	
	public boolean OnCheckValidArea(int nX, int nY) {
		boolean bReturn = true;

		if (nX < 0)
		{
			bReturn = false;
		}
		else if (nX >= TOTAL_BRICK_NUM_X)
		{
			bReturn = false;
		}
		else if (nY < 0)
		{
			bReturn = false;
		}
		else if (nY >= TOTAL_BRICK_NUM_Y)
		{
			bReturn = false;
		}

		return bReturn;
	}
	
	public void OnArrangeLine(int nLine) {
		// 해당 라인을 우선 다 지워 버린다.
		for (int i = 0 ; i < TOTAL_BRICK_NUM_X ; i++)
		{
			m_BrkBoard[i][nLine].nFill	= BRICK_EMPTY;
			m_BrkBoard[i][nLine].nColor	= 0;
		}

		// 해당 라인의 위쪽으로만 아래로 한 칸씩 쉬프트 시킨다. 
		for (int j = nLine ; j > 0 ; j--)
		{
			for (int k = 0 ; k < TOTAL_BRICK_NUM_X ; k++)
			{
				m_BrkBoard[k][j].nFill	= m_BrkBoard[k][j - 1].nFill;
				m_BrkBoard[k][j].nColor	= m_BrkBoard[k][j - 1].nColor;
			}
		}	
	}
	
	public void OnGameStartAct() {
		
	}
	
	public void OnBlockCopyN2CAct() {
		
	}
	
	public void OnSettleAct() {
		
	}
	
	public void OnGameOverAct() {
		
	}
	
	public void OnBlockDownAct() {
		
	}
	
	public void OnImpossibleAct() {
		
	}
	
	public void OnBlockMoveAct() {
				
	}
	
	public void OnBlockTurnAct() {
		
	}
	
	public void OnLineCompleteAct(int nLine) {
		
	}
}
