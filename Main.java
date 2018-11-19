import java.util.Stack;

class Main
{

	public static void main(String[] args) throws Exception
	{
			long startTime = System.currentTimeMillis();
			byte[] positions = {1,3,1,5,2,5,3,7,4,7,6,7,4,5,6,4,7,6,5,3,5,1};
			byte[] objective = {4,-2}; //cool objectives: {2,-2}, {6,0}, {6,2}. {4,4}, {4,-2}
			PuzzleSolver solver = new PuzzleSolver(positions, objective);
			GameState path = solver.bfs();
			long endTime = System.currentTimeMillis();
			if(path == null)
			{
				System.out.println("No path found. Took: " + (endTime - startTime) + " milliseconds");
				return;
			}
			System.out.println("Finding the path took " + (endTime - startTime) + " milliseconds");
	
			Stack<GameState> s = new Stack<GameState>();
			while(path.getPrevious() != null)
			{
				s.push(path);
				path = path.getPrevious();
			}
			new Viz(s);
	}

}