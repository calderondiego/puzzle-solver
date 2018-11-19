import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

class GameState 
{
	private GameState previous;
	private byte[] state;
	
	GameState(GameState _prev)
	{
		previous = _prev;
		state = new byte[22];
	}
	
	public void setState(byte[] _state)
	{
		for(byte i = 0; i < 22; i++)
			this.state[i] = _state[i];
	}
	
	public void setPrev(GameState _prev)
	{
		this.previous = _prev;
	}
	
	public GameState getPrevious()
	{
		return previous;
	}
	
	public byte[] getState()
	{
		return state;
	}
	
	
}


class StateComparator implements Comparator<byte[]>
{
	@Override
	public int compare(byte[] a, byte[] b)
	{
		for(byte i = 0; i < 22; i++)
		{
			if(Byte.compare(a[i], b[i]) < 0)
				return -1;
			else if(Byte.compare(a[i], b[i]) > 0)
				return 1;
		}
		return 0;

		
	}	
}

/*
 * The discrete space of the puzzle is as follows:
		              0  1  2  3  4  5  6  7  8  9
			 	  ==================================
				  0|  1, 1, 1, 1, 1, 1, 1, 1, 1, 1,    
				  1|  1, 1, 1, 0, 0, 0, 0, 1, 1, 1,    
				  2|  1, 1, 0, 0, 0, 0, 0, 0, 1, 1,    
				  3|  1, 0, 0, 0, 1, 0, 0, 0, 0, 1,    
				  4|  1, 0, 0, 1, 1, 0, 0, 0, 0, 1,    
				  5|  1, 0, 0, 0, 0, 0, 0, 0, 0, 1,    
				  6|  1, 0, 0, 0, 0, 0, 0, 0, 0, 1,    
				  7|  1, 1, 0, 0, 0, 0, 0, 0, 1, 1,    
				  8|  1, 1, 1, 0, 0, 0, 0, 1, 1, 1,    
				  9|  1, 1, 1, 1, 1, 1, 1, 1, 1, 1      
 */
class PuzzleSolver 
{
	
	//shapes: 1 = 3. 2 = 4. 5 =10. 8 = 9.
	
	//byte[] state;
	byte[] positions;
	byte[] objective;
	byte[] currentAux; // will use this to check boundaries
 	
 	//Initialize state to all 0's (starting position)
 	PuzzleSolver (byte[] pos, byte[] obj)
	{
		positions = pos;
		objective = obj;
		//printState(state);
		currentAux = new byte[22];
	}
	
	//search for the shortest path (breadth first search)
	public GameState bfs ()
	{
		//to check if state exists in the set
		StateComparator comp = new StateComparator();

		//set
		TreeSet<byte[]> set = new TreeSet<byte[]>(comp);
		//queue interface
		Queue<GameState> q = new LinkedList<GameState>();
		
		//start state. parent = NIL
		GameState root = new GameState(null);
		q.add(root);
		set.add(root.getState());

		//while Q is not empty
		while (!q.isEmpty())
		{
			//get the first state from the queue
			GameState current = q.remove();
			
			//check if piece 0 has reached its destination
			if(reachedObjective(current))
			{	
				return current;
			}	
			//add all possible movements with current state of the puzzle
			
			for(byte i = 0; i < 22; i++)
			{
				//make a positive change in the current state at ith position (right or down)
				byte[] potentialState = new byte[22];
				stateChange(current.getState(),potentialState, i, (byte)1);
				//check if it is a valid movement
				if(validState(potentialState))
				{
					//check if the state exists in the set
					if(set.add(potentialState) == true)
					{
						//at this point we know it is a valid state and that it does not exist in the set
						//create state and add it to the queue (it already was added in the set)
						GameState nextState = new GameState(current);
						//next.setPrev(current);
						nextState.setState(potentialState);
						q.add(nextState);
					}
				}
				//make a negative change in in the current state at the ith position (move left or up)
				potentialState = new byte[22];
				stateChange(current.getState(), potentialState, i, (byte)-1);
				//check if it is a valid move
				if(validState(potentialState))
				{
					//check if state was successfully added to the set
					if(set.add(potentialState) == true)
					{	
						//at this point we know it is a valid state and that it does not exist in the set
						//create state and add it to the queue (it already was added in the set)
						GameState nextState = new GameState(current);
						//nextState.setPrev(current);
						nextState.setState(potentialState);
						q.add(nextState);
					}
				}
			}
		}
		
		//return null if there is no possible path
		return null;
	}
	
	//make a change in the state
	public void stateChange(byte[] current, byte[] next, byte pos, byte dir)
	{
		//loop unrolled below
		/*for(byte i = 0; i < 22; i++)
		{
			next[i] = current[i];
		}*/
		//this implementation reduces the time of execution of the bfs 
		next[0] =  current[0]; next[1] =  current[1]; next[2] =  current[2]; next[3] =  current[3];
		next[4] =  current[4]; next[5] =  current[5]; next[6] =  current[6]; next[7] =  current[7];
		next[8] =  current[8]; next[9] =  current[9]; next[10] =  current[10]; next[11] =  current[11];
		next[12] =  current[12]; next[13] =  current[13]; next[14] =  current[14]; next[15] =  current[15];
		next[16] =  current[16]; next[17] =  current[17]; next[18] =  current[18]; next[19] =  current[19];
		next[20] =  current[20]; next[21] =  current[21]; 
		
		//move the piece
		next[pos] += dir;
		
	}
	
	public boolean reachedObjective(GameState current)
	{
		return (current.getState()[0] == objective[0] && current.getState()[1] == objective[1]);
	}
	
	//print the position of the pieces relative to the puzzle (not their original position) -debugging purposes
	public void printCurrentState(byte[] current)
	{
		for(byte i =0; i < 22; i++)
		{
			System.out.print((byte)(current[i] + positions[i]) + ", ");
		}
		System.out.println("");
	}
	
	//check if the current state is allowed (can be improved) 
	public boolean validState (byte[] currentState)
	{
		//calculate the positions relative to the puzzle (not their original position) and check if they are out of bounds. 
		// unrolled this loop below. 
		/*for(byte p = 0; p < 22; p++)
		{
			currentAux[p] = (byte)( currentState[p] + positions[p]);
			//check if any position is out of bounds
			if(currentAux[p] <= 0 || currentAux[p] >= 9)
				return false;
		}*/
		
		currentAux[0] = (byte)( currentState[0] + positions[0]);
		if(currentAux[0] <= 0 || currentAux[0] >= 9) return false;
		currentAux[1] = (byte)( currentState[1] + positions[1]);
		if(currentAux[1] <= 0 || currentAux[1] >= 9) return false;
		currentAux[2] = (byte)( currentState[2] + positions[2]);
		if(currentAux[2] <= 0 || currentAux[2] >= 9) return false;
		currentAux[3] = (byte)( currentState[3] + positions[3]);
		if(currentAux[3] <= 0 || currentAux[3] >= 9) return false;
		currentAux[4] = (byte)( currentState[4] + positions[4]);
		if(currentAux[4] <= 0 || currentAux[4] >= 9) return false;
		currentAux[5] = (byte)( currentState[5] + positions[5]);
		if(currentAux[5] <= 0 || currentAux[5] >= 9) return false;
		currentAux[6] = (byte)( currentState[6] + positions[6]);
		if(currentAux[6] <= 0 || currentAux[6] >= 9) return false;
		currentAux[7] = (byte)( currentState[7] + positions[7]);
		if(currentAux[7] <= 0 || currentAux[7] >= 9) return false;
		currentAux[8] = (byte)( currentState[8] + positions[8]);
		if(currentAux[8] <= 0 || currentAux[8] >= 9) return false;
		currentAux[9] = (byte)( currentState[9] + positions[9]);
		if(currentAux[9] <= 0 || currentAux[9] >= 9) return false;
		currentAux[10] = (byte)( currentState[10] + positions[10]);
		if(currentAux[10] <= 0 || currentAux[10] >= 9) return false;
		currentAux[11] = (byte)( currentState[11] + positions[11]);
		if(currentAux[11] <= 0 || currentAux[11] >= 9) return false;
		currentAux[12] = (byte)( currentState[12] + positions[12]);
		if(currentAux[12] <= 0 || currentAux[12] >= 9) return false;
		currentAux[13] = (byte)( currentState[13] + positions[13]);
		if(currentAux[13] <= 0 || currentAux[13] >= 9) return false;
		currentAux[14] = (byte)( currentState[14] + positions[14]);
		if(currentAux[14] <= 0 || currentAux[14] >= 9) return false;
		currentAux[15] = (byte)( currentState[15] + positions[15]);
		if(currentAux[15] <= 0 || currentAux[15] >= 9) return false;
		currentAux[16] = (byte)( currentState[16] + positions[16]);
		if(currentAux[16] <= 0 || currentAux[16] >= 9) return false;
		currentAux[17] = (byte)( currentState[17] + positions[17]);
		if(currentAux[17] <= 0 || currentAux[17] >= 9) return false;
		currentAux[18] = (byte)( currentState[18] + positions[18]);
		if(currentAux[18] <= 0 || currentAux[18] >= 9) return false;
		currentAux[19] = (byte)( currentState[19] + positions[19]);
		if(currentAux[19] <= 0 || currentAux[19] >= 9) return false;
		currentAux[20] = (byte)( currentState[20] + positions[20]);
		if(currentAux[20] <= 0 || currentAux[20] >= 9) return false;
		currentAux[21] = (byte)( currentState[21] + positions[21]);
		if(currentAux[21] <= 0 || currentAux[21] >= 9) return false;

		byte x,y;
		//get the original puzzle (only boundaries)
		//original = generatePuzzle(original);
		byte[][] layout = {  
//	 		              0  1  2  3  4  5  6  7  8  9
//			 	  ===================================================
				  /*0*/ { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  },  // 0
				  /*1*/ { 1, 1, 1, 0, 0, 0, 0, 1, 1, 1,  },  // 1
				  /*2*/ { 1, 1, 0, 0, 0, 0, 0, 0, 1, 1,  },  // 2
				  /*3*/ { 1, 0, 0, 0, 1, 0, 0, 0, 0, 1,  },  // 3
				  /*4*/ { 1, 0, 0, 1, 1, 0, 0, 0, 0, 1,  },  // 4
				  /*5*/ { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1,  },  // 5
				  /*6*/ { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1,  },  // 6
				  /*7*/ { 1, 1, 0, 0, 0, 0, 0, 0, 1, 1,  },  // 7
				  /*8*/ { 1, 1, 1, 0, 0, 0, 0, 1, 1, 1,  },  // 8
				  /*9*/	{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1   }   // 9
			 			};;
		
		//place all the pieces. Try to unroll loop.
		for(byte j = 0; j < 11; j++)
		{
			x = currentAux[(byte)(j*2)]; y = currentAux[(byte)(j*2+1)]; 
			if(j == 0)
			{
				if(layout[y][x] == 1 || layout[y][x+1] == 1 || layout[y+1][x] == 1 || layout[y+1][x+1] == 1) 
					return false;
				else
				{
					layout[y][x] = 1;
					layout[y][x+1] = 1;
					layout[y+1][x] = 1;
					layout[y+1][x+1] = 1;
				}
			}
			else if( j == 1 || j == 3)
			{
				if(layout[y][x] == 1 || layout[y+1][x] ==1 || layout[y+1][x+1] ==1) 
					return false;
				else
				{
					layout[y][x] = 1;
					layout[y+1][x] = 1;
					layout[y+1][x+1] = 1;
				}
			}
			else if (j == 2 || j == 4)
			{
				if(layout[y][x] == 1 || layout[y][x+1] == 1 || layout[y+1][x+1] == 1) 
					return false;
				else 
				{
					layout[y][x] = 1;
					layout[y][x+1] = 1;
					layout[y+1][x+1] = 1;
				}
			}
			else if(j == 5 || j== 10)
			{
				if(layout[y][x] == 1 || layout[y][x+1] == 1 || layout[y+1][x] == 1) 
					return false;
				else
				{
					layout[y][x] = 1;
					layout[y][x+1] = 1;
					layout[y+1][x] = 1;
				}
				
			}
			else if( j == 8 || j == 9)
			{
				if(layout[y][x] == 1 || layout[y][x+1] == 1 || layout[y-1][x+1] == 1 )
					return false;
				else
				{
					layout[y][x] = 1; layout[y][x+1] = 1; layout[y-1][x+1] = 1;
				}
					
			}
			else if (j == 6)
			{
				if(layout[y][x] == 1 || layout[y][x+1] == 1 || layout[y-1][x+1] == 1 || layout[y+1][x+1] == 1)
					return false;
				else
				{
					layout[y][x] = 1; layout[y][x+1] = 1; layout[y-1][x+1] = 1; layout[y+1][x+1] = 1; 
				}
			}
			else if (j == 7)
			{
				if(layout [y][x] == 1 || layout[y+1][x] == 1 || layout[y+2][x] == 1 || layout[y+1][x+1] == 1)
					return false;
				else
				{
					layout [y][x] = 1; layout[y+1][x] = 1; layout[y+2][x] = 1; layout[y+1][x+1] = 1;
				}
			}
			
		}
		return true;
	}
	
	//print the state in the required format
	public void printState(byte[] state)
	{
		for( int i = 0; i < 11; i++)
		{
			System.out.print("(" + state[2*i] + "," +
					state[2*i + 1] + ") ");
		}
		System.out.println();
	}
	
	//print the puzzle for debugging purposes
	public void printPuzzle(byte[][] puzzle1)
	{
		for( int i = 0; i < 10; i++ )
		{
			System.out.print(i+": ");
			for( int j = 0; j < 10; j++)
			{
				System.out.print(" " + puzzle1[i][j] + " ");
			}
			System.out.println("");
		}
	}
	

	
	/*public static void main(String[] args) throws Exception 
	{
		long startTime = System.currentTimeMillis();
		Main main = new Main();
		GameState path = main.bfs();
		long endTime = System.currentTimeMillis();

		System.out.println("Finding the path took " + (endTime - startTime) + " milliseconds");
		//count how many game states for debugging purposes
		//int i = 0;
		Stack<GameState> s = new Stack<GameState>();
		while(path.prev != null)
		{
			s.push(path);
			//i++;
			path = path.prev;
		}
		main.p = s;
		main.printState(new byte[22]);
		while (!s.isEmpty())
		{
			main.printState(s.pop().state);
		}
		
		//testing state comparator comparator 
		/*StateComparatorB comp = new StateComparatorB();
		TreeSet<byte[]> set = new TreeSet<byte[]>(comp);
		byte[] a = new byte[22];
		byte[] b = new byte[22];
		byte[] c = new byte[22];
		System.out.println(set.add(a));
		main.printCurrentState(a);
		a = main.stateChange(a,a, (byte)20, (byte)-1);
		main.printCurrentState(a);
		b =main.stateChange(b,b,(byte)8,(byte)1);
		c = main.stateChange(c,c, (byte)1,(byte)1);
		System.out.println(set.add(c));	
		main.printCurrentState(b);
		System.out.println(set.add(b));
		System.out.println(set.size());*/
		
		
		/*
		//testing drawing function
		byte[] aa = {0,-1, 1,0, 1,0, 0,0, 0,0, 0,0, 1,0, 1,-1, 0,0, 0,0, -2,0 };
		byte[] bb = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-3,0};
		
		byte[][] p = new byte[10][10];
		System.out.println(main.drawed(aa, p));
		System.out.println(main.drawed(bb, p));

		
	}*/
}