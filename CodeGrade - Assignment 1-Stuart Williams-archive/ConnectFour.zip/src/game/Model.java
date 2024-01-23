package game;

import interfaces.IModel;
import util.GameSettings;
import java.io.File;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * This class represents the state of the game.
 * It has been partially implemented, but needs to be completed by you.
 *
 * @author <YOUR UUN>
 */
public class Model implements IModel
{
	// A reference to the game settings from which you can retrieve the number
	// of rows and columns the board has and how long the win streak is.
	private GameSettings settings;
	public byte[][] board; public boolean player;public boolean looser;
	// The default constructor.
	public Model()
	{
		// You probably won't need this.
	}
	
	// A constructor that takes another instance of the same type as its parameter.
	// This is called a copy constructor.
	public Model(IModel model)
	{
		this.settings = model.getGameSettings();
		this.board = new byte[this.settings.nrCols][this.settings.nrRows];
		this.player = (model.getActivePlayer() ==2);
		this.looser = false;
		for(int i =0;i<this.settings.nrCols;i++){
			for(int j =0;j<this.settings.nrRows;j++){
				this.board[i][j] = model.getPieceIn(j,i);
			}
		}
	}
	
	// Called when a new game is started on an empty board.
	public void initNewGame(GameSettings settings)
	{
		this.settings = settings;
		this.board = new byte[settings.nrCols][settings.nrRows];
		this.player = false;
		this.looser = false;
		// This method still needs to be extended.
	}
	
	// Called when a game state should be loaded from the given file.
	public void initSavedGame(String fileName)
	{
		File file = new File("saves/"+fileName);
		try {
			Scanner fileRd = new Scanner(file);
			int nrRows = fileRd.nextInt();
			int nrCols = fileRd.nextInt();
			int streak = fileRd.nextInt();
			int sPlayer = fileRd.nextInt();
			this.settings = new GameSettings(nrRows,nrCols,streak);
			this.board = new byte[nrCols][nrRows];
			this.player = false;
			this.looser = false;
			if(sPlayer ==2){this.player = true;}
			System.out.println(fileRd.nextLine());
			for(int i =0; i<nrRows;i++){
				String ln = fileRd.nextLine();
				for(int j =0; j<nrCols;j++){
					char test = ln.charAt(j);
					int tt = (int) test;
					this.board[j][i]= (byte) ((int)ln.charAt(j)-48);
				}
			}
		} catch (Exception e){
			System.out.println("File does not exist");
		}
	}
	
	// Returns whether or not the passed in move is valid at this time.
	public boolean isMoveValid(int move)
	{
		if(move==-1){return true;}
		// Assuming all moves are valid.
		if(move<0 | move >= settings.nrCols) {
			return false;
		}
		if(board[move][0]!=0){
			return false;
		}
		return true;
	}
	
	// Actions the given move if it is valid. Otherwise, does nothing.
	public void makeMove(int move)
	{
		if(move ==-1){
			this.looser = true;
			return;
		}
		int x=0;
		int y=0;
		for(int i = (this.board[0].length-1);i>=0;i--){
			if(this.board[move][i] ==0){
				this.board[move][i]= getActivePlayer();
				x= move;
				y = i;
				break;
			}
		}
		this.player = !(this.player);
	}
	// Returns one of the following codes to indicate the game's current status.
	// IModel.java in the "interfaces" package defines constants you can use for this.
	// 0 = Game in progress
	// 1 = Player 1 has won
	// 2 = Player 2 has won
	// 3 = Tie (board is full and there is no winner)
	private boolean checkStreak(){
		if(checkForHorizontalWin()){
			return true;
		}
		if(checkForVirticalWin()){
			return true;
		}
		if(checkForUpDirWin()){
			return true;
		}
		if(checkForDownDirWin()){
			return true;
		}
		return false;
	}
	private boolean checkForVirticalWin(){
		for(int i =0;i<this.settings.nrCols;i++){
			for(int j = this.settings.nrRows-1;j>=this.settings.minStreakLength-1;j--){
				if(this.board[i][j]==0){break;}
				boolean win = true;
				for(int k =0;k<this.settings.minStreakLength-1;k++){
					if(this.board[i][j-k]!=this.board[i][j-(k+1)]){
						win = false;
						break;
					}

				}
				if(win){return true;}
			}
		}
		return false;
	}
	private boolean checkForUpDirWin(){
		for(int i =0;i<=this.settings.nrCols-this.settings.minStreakLength;i++){
			for(int j = this.settings.nrRows-1;j>=this.settings.minStreakLength-1;j--){
				if(this.board[i][j]==0){break;}
				boolean win = true;
				for(int k =0;k<this.settings.minStreakLength-1;k++){
					if(this.board[i+k][j-k]!=this.board[i+k+1][j-(k+1)]){
						win = false;
						break;
					}

				}
				if(win){return true;}
			}
		}
		return false;
	}
	private boolean checkForDownDirWin(){
		for(int i =this.settings.minStreakLength-1;i<this.settings.nrCols;i++){
			for(int j = this.settings.nrRows-1;j>=this.settings.minStreakLength-1;j--){
				if(this.board[i][j]==0){break;}
				boolean win = true;
				for(int k =0;k<this.settings.minStreakLength-1;k++){
					if(this.board[i-k][j-k]!=this.board[i-(k+1)][j-(k+1)]){
						win = false;
						break;
					}

				}
				if(win){
					return true;
				}
			}
		}
		return false;
	}
	private boolean checkForHorizontalWin(){
		for(int i =0;i<=this.settings.nrCols-this.settings.minStreakLength;i++){
			for(int j = this.settings.nrRows-1;j>=0;j--){
				if(this.board[i][j]==0){break;}
				boolean win = true;
				for(int k =0;k<this.settings.minStreakLength-1;k++){
					if(this.board[i+k][j]!=this.board[i+k+1][j]){
						win = false;
						break;
					}

				}
				if(win){return true;}
			}
		}
		return false;
	}
	public byte getGameStatus()
	{
		// Assuming the game is never ending.
		if(!looser){looser = checkStreak();}
		boolean isOnGoing = false;
		for(int i=0; i<this.settings.nrCols;i++){
			if(this.board[i][0]==0){
				isOnGoing = true;
				break;
			}
		}
		if(looser){
			if(player){
				return IModel.GAME_STATUS_WIN_1;
			}
			return IModel.GAME_STATUS_WIN_2;
		}
		if(isOnGoing){
			return IModel.GAME_STATUS_ONGOING;
		}
		return IModel.GAME_STATUS_TIE;
	}
	
	// Returns the number of the player whose turn it is.
	public byte getActivePlayer()
	{
		// Assuming it is always the turn of player 1.

		if(player){
			return 2;
		}
		return 1;
	}
	
	// Returns the owner of the piece in the given row and column on the board.
	// Return 1 or 2 for players 1 and 2 respectively or 0 for empty cells.
	public byte getPieceIn(int row, int column)
	{
		// AHHHH. WHY DID YOU PUT 'ROW' FIRST AHHHH. X AXIS FIRST THEN Y. I'VE SWAPPED THEM NOW TO GET MARKS. BUT WHY DO THIS TO ME
		return (this.board[column][row]);
	}
	
	// Returns a reference to the game settings, from which you can retrieve the
	// number of rows and columns the board has and how long the win streak is.
	public GameSettings getGameSettings()
	{
		return settings;
	}
	
	// =========================================================================
	// ================================ HELPERS ================================
	// =========================================================================
	
	// You may find it useful to define some helper methods here.
	
}
