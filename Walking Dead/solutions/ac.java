import java.io.*;
import java.util.*;
public class ac {
	static int m, n, k;
	static char[][] mat;
	static int[][] pre;
	static char[][] path;
	static int startX, startY, endX, endY;
	public static void main(String[] args) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String[] arr = in.readLine().trim().split(" ");
		m = Integer.parseInt(arr[0]);
		n = Integer.parseInt(arr[1]);
		k = Integer.parseInt(arr[2]);
		mat = new char[m][m];
		pre = new int[m][m];
		path = new char[m][m];
		for(int i = 0; i < m; i++){
			String s = in.readLine().trim();
			for(int j = 0; j < m; j++){
				char c = mat[i][j] = s.charAt(j);
				if(c == 's')
					populate(i, j);
				if(c == 'd'){
					endX = i;
					endY = j;
				}
				if(c == 'r'){
					startX = i;
					startY = j;
				}
			}
		}
		
		// find shortest time it takes for any zombie to reach destination
		int zombieTime = findZombieTime();
		
		// in pre[][], store 1 for every valid position and 0 for every invalid position
		int[] sum = new int[m];
		for(int i = 0; i < m; i++){
			// sum stores prefix array for current row
			for(int j = 0; j < m; j++)
				sum[j] += pre[i][j];
			// pre[i] stores prefix sum for row i
			int s = 0;
			for(int j = 0; j < m; j++){
				pre[i][j] = (s += sum[j]);
			}
			// find if each position is valid. pre[i][j] stores 1 for valid position, 0 for invalid
			for(int j = 0; j < m; j++){
				if(pre[i][j] >= n && mat[i][j] != 's')
					pre[i][j] = 1;
				else
					pre[i][j] = 0;
			}
		}

		// find shortest time takes for rick to reach destination, store path
		int rickTime = findRickTime();

		if(rickTime == -1 || rickTime > zombieTime)
			System.out.print("impossible");
		else{
			// backtrack path
			int i = startX, j = startY;
			StringBuilder str = new StringBuilder();
			while(!(i == endX && j == endY)){
				str.append(path[i][j]);
				switch(path[i][j]){
					case 't': i--; break;
					case 'b': i++; break;
					case 'l': j--; break;
					case 'r': j++; break;
				}
			}
			System.out.print(str);
		}
	}
	
	public static int findRickTime(){
		// bfs from destination to Rick, use only valid (not supply station) positions within range of n supply stations
		int[][] distance = new int[m][m];
		for(int i = 0; i < m; i++)
			for(int j = 0; j < m; j++)
				distance[i][j] = -1;
		distance[endX][endY] = 0;
		ArrayList<Point> queue = new ArrayList<Point>();
		queue.add(new Point(endX, endY, (char)0));
		int dis = 0;
		while(!queue.isEmpty()) {
			ArrayList<Point> newpoints = new ArrayList<>();
			dis++;
			for(int i = 0; i < queue.size(); i++){
				ArrayList<Point> points = queue.get(i).goodNeighbors();
				for(int j = 0; j < points.size(); j++){
					Point p = points.get(j);
					if(distance[p.x][p.y] == -1){
						distance[p.x][p.y] = dis;
						path[p.x][p.y] = p.dir;
						newpoints.add(p);
					}
					if(distance[p.x][p.y] == dis && path[p.x][p.y] > p.dir)
						path[p.x][p.y] = p.dir;
				}
			}
			queue = newpoints;
		}
		return distance[startX][startY];
	}
	public static int findZombieTime(){
		// bfs from destination to first zombie, use any position that is not a supply station
		boolean[][] explored = new boolean[m][m];
		explored[endX][endY] = true;
		ArrayList<Point> queue = new ArrayList<Point>();
		queue.add(new Point(endX, endY, (char)0));
		int dis = 0;
		while(!queue.isEmpty()){
			ArrayList<Point> newpoints = new ArrayList<>();
			dis++;
			for(int i = 0; i < queue.size(); i++){
				ArrayList<Point> points = queue.get(i).neighbors();
				for(int j = 0; j < points.size(); j++){
					Point p = points.get(j);
					if(!explored[p.x][p.y]){
						explored[p.x][p.y] = true;
						newpoints.add(p);
						if(mat[p.x][p.y] == 'z')
							return dis;
					}
				}
			}
			queue = newpoints;
		}
		return Integer.MAX_VALUE;
	}
	public static void populate(int r, int c){
		// add prefix to four corners of the prefix matrix
		pre[Math.max(r-k, 0)][Math.max(c-k, 0)]++;
		if(c + k + 1 <= m - 1)
			pre[Math.max(r-k, 0)][c+k+1]--;
		if(r + k + 1 <= m - 1)
			pre[r+k+1][Math.max(c-k, 0)]--;
		if(r + k + 1 <= m - 1 && c + k + 1 <= m - 1)
			pre[r+k+1][c+k+1]++;
	}
	static final class Point{
		int x;
		int y;
		char dir;
		Point(int xx, int yy, char d){
			x = xx;
			y = yy;
			dir = d;
		}
		// generate neighbors that are not supply stations (for zombie dfs)
		ArrayList<Point> neighbors(){
			ArrayList<Point> points = new ArrayList<>();
			if(x > 0 && mat[x - 1][y] != 's')
				points.add(new Point(x - 1, y, 'b'));
			if(x < m - 1 && mat[x + 1][y] != 's')
				points.add(new Point(x + 1, y, 't'));
			if(y > 0 && mat[x][y - 1] != 's')
				points.add(new Point(x, y - 1, 'r'));
			if(y < m - 1 && mat[x][y + 1] != 's')
				points.add(new Point(x, y + 1, 'l'));
			return points;
		}
		// generate neighbors that are not supply stations and within range of n supply stations
		// store the direction if stepping from neighbor to current point
		ArrayList<Point> goodNeighbors(){
			ArrayList<Point> points = new ArrayList<>();
			if(x > 0 && pre[x - 1][y] == 1)
				points.add(new Point(x - 1, y, 'b'));
			if(x < m - 1 && pre[x + 1][y] == 1)
				points.add(new Point(x + 1, y, 't'));
			if(y > 0 && pre[x][y - 1] == 1)
				points.add(new Point(x, y - 1, 'r'));
			if(y < m - 1 && pre[x][y + 1] == 1)
				points.add(new Point(x, y + 1, 'l'));
			return points;
		}
	}
	
}