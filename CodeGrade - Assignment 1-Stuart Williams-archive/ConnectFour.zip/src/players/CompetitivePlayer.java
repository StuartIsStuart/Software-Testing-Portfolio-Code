package players;

import game.Model;
import interfaces.IModel;
import interfaces.IPlayer;
import util.GameSettings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Implementing this player is an advanced task.
 * See assignment instructions for what to do.
 * If not attempting it, just upload the file as it is.
 *
 * @author <YOUR UUN>
 */
public class CompetitivePlayer implements IPlayer
{
	// A reference to the model, which you can use to get information about
	// the state of the game. Do not use this model to make any moves!
	private IModel model;
	
	// The constructor is called when the player is selected from the game menu.
	public CompetitivePlayer()
	{
		// You may (or may not) need to perform some initialisation here.
	}
	
	// This method is called when a new game is started or loaded.
	// You can use it to perform any setup that may be required before
	// the player is asked to make a move. The second argument tells
	// you if you are playing as player 1 or player 2.
	public void prepareForGameStart(IModel model, byte playerId)
	{
		this.model = model;
	}
	
	// This method is called to ask the player to take their turn.
	// The move they choose should be returned from this method.
	//Its a minimax algorithm. Its not well coded but it did almost kill me. So itll do.
	public int chooseMove()
	{
		int numOfMov = this.model.getGameSettings().nrCols;//Number of possible moves
		int fstCheck = anyWinning(this.model,numOfMov);//It first checks if there are any insta wins
		if(fstCheck!=-1){
			return fstCheck;//If there are it does it.
		}
		int[] moves = new int[numOfMov];//This array will hold the minimax value for that branch
		for(int i =0;i<numOfMov;i++){//goes through each move
			if(model.isMoveValid(i)){//If the move is valid
				IModel copy = new Model(model);//it copies the board
				copy.makeMove(i);//Makes the move
				moves[i] = drStrange(copy,numOfMov,6);//and finds the minimax value of that moves branch
			}
			else {moves[i]=999999999;}//If the move isnt valid then i just use a really big value
		}
		List <Integer> ord = new ArrayList<Integer>();//This all just
		for(int i =0;i<numOfMov;i++){ord.add(i);}//  finds the location
		ord = sorter(ord,moves);//               of the smallest minimax values
		if(moves[ord.get(0)]==999999999) {//If the smallest value is the largest posible then all moves are certain loses
			return -1;//                    and will concede
		}
		return (ord.get(0));//This returns the move that the minimax has found the best
	}
	private int drStrange(IModel model,int pos,int depth){
		if(anyWinning(model,pos)!=-1){//If there is a winning move then it returns max value
			return 999999999;
		}
		if(depth ==0){//If the depth has been reached then we avaluate this state and return that
			return rate(model);
		}
		List <Integer> nodes = new ArrayList<Integer>();//This list will store the minimax values of all posible moves from this position
		for(int i= 0;i<pos;i++){//We go through each posible move
			if(model.isMoveValid(i)) {//If the move is valid
				IModel copy = new Model(model);//We clone the model
				copy.makeMove(i);//Make the move
				nodes.add(-1*drStrange(copy,pos,depth-1));//And find the minimax value of this branch.
			}// The depth has been increased. So it wont continue forever
		}// We times it by -1 as this means for each alternating moves we will be finding the mini and the max, hence the name. As the bot wants the biggest value and the opposing player wants the smallest
		if(nodes.size()==0){return 0;}//If no moves are valid then the nodes list will be empty. This returns 0 as it is a draw
		return (Collections.max(nodes));//This returns the max value of nodes.
	}
	private List<Integer> sorter(List<Integer> ind, int[] val){//this is just a quicksort
		if(ind.size()<=1){
			return ind;
		}
		List<Integer> bigger = new ArrayList<Integer>();
		List<Integer> smaller = new ArrayList<Integer>();
		for(int i =1;i<ind.size();i++){
			if(val[ind.get(i)]>val[ind.get(0)]){
				bigger.add(ind.get(i));
			}else{smaller.add(ind.get(i));}
		}
		smaller = (this.sorter(smaller,val));
		smaller.add(ind.get(0));
		bigger = this.sorter(bigger,val);
		smaller.addAll(bigger);
		return (smaller);
	}
	private int anyWinning(IModel model,int nmbOfPosMove){
		for(int i =0; i<nmbOfPosMove;i++){
			if(model.isMoveValid(i)){
				if(wouldWin(i, model)){return i;}
			}
		}
		return -1;
	}
	private boolean wouldWin(int i, IModel model){
		IModel modelCopy = new Model(model);
		modelCopy.makeMove(i);
		return ((modelCopy.getGameStatus() == IModel.GAME_STATUS_WIN_1)|(modelCopy.getGameStatus() == IModel.GAME_STATUS_WIN_2));
	}
	private int rate(IModel model){
		GameSettings settings = model.getGameSettings();
		byte[][] tempB = copyBoard(model);
		int ratings = 0;
		for(int i =0; i<tempB.length-settings.minStreakLength;i++){
			for(int j = tempB[0].length-1;j>=0;j--){
				if(tempB[i][j]==0){
					ratings+=ratePos(tempB,model.getActivePlayer(),settings.minStreakLength,i,j);
					break;
				}
			}
		}
		return ratings;
	}
	private int ratePos(byte[][] tempB,byte player,int streak,int x,int y){
		int rating =0;
		for(int i =1; i<streak;i++){
			if(x+i<tempB.length){
				if(tempB[i+x][y]==player){
					rating+=4;
				}else if(tempB[i+x][y]!=0){break;}else{rating++;}
			}else {break;}
		}
		for(int i =1; i<streak;i++){
			if(x-i>=0){
				if(tempB[x-i][y]==player){
					rating+=4;
				}else if(tempB[x-i][y]!=0){break;}else{rating++;}
			}else {break;}
		}
		for(int i =1; i<streak;i++){
			if(y+i<tempB[0].length){
				if(tempB[x][y+i]==player){
					rating+=4;
				}else{break;}
			}else {break;}
		}
		for(int i =1; i<streak;i++){
			if(x+i<tempB.length & y+i<tempB[0].length){
				if(tempB[x+i][y+i]==player){
					rating+=4;
				}else if(tempB[x+i][y+i]!=0){break;}else{rating++;}
			}else {break;}
		}
		for(int i =1; i<streak;i++){
			if(x-i>=0 & y+i<tempB[0].length){
				if(tempB[x-i][y+i]==player){
					rating+=4;
				}else if(tempB[x-i][y+i]!=0){break;}else{rating++;}
			}else {break;}
		}
		for(int i =1; i<streak;i++){
			if(x+i<tempB.length & y-i>=0){
				if(tempB[x+i][y-i]==player){
					rating+=4;
				}else if(tempB[x+i][y-i]!=0){break;}else{rating++;}
			}else {break;}
		}
		for(int i =1; i<streak;i++){
			if(x-i>=0 & y-i>=0){
				if(tempB[x-i][y-i]==player){
					rating+=4;
				}else if(tempB[x-i][y-i]!=0){break;}else{rating++;}
			}else {break;}
		}
		return rating;
	}
	private byte[][] copyBoard(IModel model){
		int hei = model.getGameSettings().nrRows;
		int numbMov = model.getGameSettings().nrCols;
		byte[][] tempB = new byte[numbMov][hei];
		for(int i =0;i<numbMov;i++){
			for(int j =0;j<hei;j++){
				tempB[i][j]= model.getPieceIn(j,i);
			}
		}
		return tempB;
	}
}