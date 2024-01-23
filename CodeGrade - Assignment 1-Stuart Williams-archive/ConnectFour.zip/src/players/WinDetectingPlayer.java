package players;

import game.Model;
import interfaces.IModel;
import interfaces.IPlayer;
import util.GameSettings;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementing this player is an advanced task.
 * See assignment instructions for what to do.
 * If not attempting it, just upload the file as it is.
 *
 * @author <YOUR UUN>
 */
public class WinDetectingPlayer implements IPlayer
{
	// A reference to the model, which you can use to get information about
	// the state of the game. Do not use this model to make any moves!
	private IModel model;
	
	// The constructor is called when the player is selected from the game menu.
	public WinDetectingPlayer()
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
	public int chooseMove()
	{
		int nmbOfPosMove = model.getGameSettings().nrCols;//This is getting how many potential moves there are
		int temp = anyWinning(this.model,nmbOfPosMove);//checks if any move the bot can make will insta win
		if(temp!=-1){//Minus one means that non of the moves can insta win
			return temp;//If one of the moves can it returns that move
		}
		int[] order  = getRandomOrder(nmbOfPosMove);//this gets an array of all potential moves in a random order
		for(int i: order){//this cycles through the random potential moves
			if(this.model.isMoveValid(i)){//checks if the move is possible
				Model copy = new Model(this.model);//if it is then it creates a copy of the board
				copy.makeMove(i);//And makes the move
				if(anyWinning(copy,nmbOfPosMove)==-1){//It then checks if this new board has any insta wins.
					return i;//If the player wont be able to insta win next move if the bot plays the randomly selected move then the bot plays it.
				}
			}
		}
		return IModel.CONCEDE_MOVE;//If the code gets to here then the bot has no way of winning and will concede
	}
	private int anyWinning(IModel model,int nmbOfPosMove){
		for(int i =0; i<nmbOfPosMove;i++){
			if(model.isMoveValid(i)){
				if(wouldWin(i, model)){return i;}
			}
		}
		return -1;
	}
	private int[] getRandomOrder(int num){
		List<Integer> temp = new LinkedList<Integer>();
		for(int i =0; i<num;i++){temp.add(i);}
		int[] order = new int[num];
		for(int i =0; i<num;i++){
			int temp2 = (int)Math.round(Math.random()*(temp.size()-1));
			order[i] = temp.get(temp2);
			temp.remove(temp2);
		}
		return order;
	}
	private boolean wouldWin(int i, IModel model){
		IModel modelCopy = new Model(model);
		modelCopy.makeMove(i);
		return ((modelCopy.getGameStatus() == IModel.GAME_STATUS_WIN_1)|(modelCopy.getGameStatus() == IModel.GAME_STATUS_WIN_2));
	}
}
