import java.io.*;
import java.util.*;
public class WalkingDeadACBackTracing {
	static int m, n, k;
	static char[][] mat;
	static int[][] pre;
	static int[][] distance;
	static int startX, startY, endX, endY;
	public static void main(String[] args) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String[] arr = in.readLine().trim().split(" ");
		m = Integer.parseInt(arr[0]);
		n = Integer.parseInt(arr[1]);
		k = Integer.parseInt(arr[2]);
		mat = new char[m][m];
		pre = new int[m][m];
		distance = new int[m][m];
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
		
		int zombieTime = findZombieTime();
		int[] sum = new int[m];
		for(int i = 0; i < m; i++){
			for(int j = 0; j < m; j++)
				sum[j] += pre[i][j];
			int s = 0;
			for(int j = 0; j < m; j++){
				pre[i][j] = (s += sum[j]);
			}
			for(int j = 0; j < m; j++){
				if(pre[i][j] >= n && mat[i][j] != 's')
					pre[i][j] = 1;
				else
					pre[i][j] = 0;
			}
		}
		int rickTime = findRickTime();
		if(rickTime == -1 || rickTime > zombieTime)
			System.out.print("impossible");
		else{
			int i = startX, j = startY;
			int dis = distance[i][j];
			StringBuilder str = new StringBuilder();
			while(!(i == endX && j == endY)){
				dis--;
				if(i < m - 1 && distance[i + 1][j] == dis){
					i++;
					str.append('b');
					continue;
				}
				if(j > 0 && distance[i][j - 1] == dis){
					j--;
					str.append('l');
					continue;
				}
				if(j < m - 1 && distance[i][j + 1] == dis){
					j++;
					str.append('r');
					continue;
				}
				if(i > 0 && distance[i-1][j] == dis){
					i--;
					str.append('t');
					continue;
				}
			}
			System.out.print(str);
		}
	}
	
	public static int findRickTime(){
		distance = new int[m][m];
		for(int i = 0; i < m; i++)
			for(int j = 0; j < m; j++)
				distance[i][j] = -1;
		distance[endX][endY] = 0;
		ArrayList<Point> queue = new ArrayList<Point>();
		queue.add(new Point(endX, endY));
		int dis = 0;
		while(!queue.isEmpty()){
			ArrayList<Point> newpoints = new ArrayList<>();
			dis++;
			for(int i = 0; i < queue.size(); i++){
				ArrayList<Point> points = queue.get(i).goodNeighbors();
				for(int j = 0; j < points.size(); j++){
					Point p = points.get(j);
					if(distance[p.x][p.y] == -1){
						distance[p.x][p.y] = dis;
						newpoints.add(p);
					}
				}
			}
			queue = newpoints;
		}
		return distance[startX][startY];
	}
	public static int findZombieTime(){
		boolean[][] explored = new boolean[m][m];
		explored[endX][endY] = true;
		ArrayList<Point> queue = new ArrayList<Point>();
		queue.add(new Point(endX, endY));
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
		Point(int xx, int yy){
			x = xx;
			y = yy;
		}
		ArrayList<Point> neighbors(){
			ArrayList<Point> points = new ArrayList<>();
			if(x > 0 && mat[x - 1][y] != 's')
				points.add(new Point(x - 1, y));
			if(x < m - 1 && mat[x + 1][y] != 's')
				points.add(new Point(x + 1, y));
			if(y > 0 && mat[x][y - 1] != 's')
				points.add(new Point(x, y - 1));
			if(y < m - 1 && mat[x][y + 1] != 's')
				points.add(new Point(x, y + 1));
			return points;
		}
		ArrayList<Point> goodNeighbors(){
			ArrayList<Point> points = new ArrayList<>();
			if(x > 0 && pre[x - 1][y] == 1)
				points.add(new Point(x - 1, y));
			if(x < m - 1 && pre[x + 1][y] == 1)
				points.add(new Point(x + 1, y));
			if(y > 0 && pre[x][y - 1] == 1)
				points.add(new Point(x, y - 1));
			if(y < m - 1 && pre[x][y + 1] == 1)
				points.add(new Point(x, y + 1));
			return points;
		}
	}
	
}