//
// Created by alber on 12/9/2018.
//

#include <iostream>
#include <cstdio>
#include <cmath>
#include <cstring>
#include <algorithm>
#include <string>
#include <vector>
#include <stack>
#include <queue>
#include <set>
#include <map>
#include <sstream>
#include <complex>
#include <ctime>
#include <cassert>
#include <functional>

using namespace std;

#define MAXM 1000
#define INF INT32_MAX

struct Point {

    int x, y, dist;
    char dir;

    Point(int xx, int yy, char d) : x(xx), y(yy), dir(d) {

    }

};

int m, n, k, rx, ry, dx, dy, prefixSum[MAXM][MAXM];
char mat[MAXM][MAXM], path[MAXM][MAXM];

void getNeighbors(Point& p, vector<Point>& neighbors) {
    int x = p.x;
    int y = p.y;
    if (x > 0 && mat[x - 1][y] != 's')
        neighbors.push_back(Point(x - 1, y, 'b'));
    if (x < m - 1 && mat[x + 1][y] != 's')
        neighbors.push_back(Point(x + 1, y, 't'));
    if (y > 0 && mat[x][y - 1] != 's')
        neighbors.push_back(Point(x, y - 1, 'r'));
    if (y < m - 1 && mat[x][y + 1] != 's')
        neighbors.push_back(Point(x, y + 1, 'l'));
}

void goodNeighbors(Point& p, vector<Point>& neighbors) {
    int x = p.x;
    int y = p.y;
    if (x > 0 && prefixSum[x - 1][y] == 1)
        neighbors.push_back(Point(x - 1, y, 'b'));
    if (x < m - 1 && prefixSum[x + 1][y] == 1)
        neighbors.push_back(Point(x + 1, y, 't'));
    if (y > 0 && prefixSum[x][y - 1] == 1)
        neighbors.push_back(Point(x, y - 1, 'r'));
    if (y < m - 1 && prefixSum[x][y + 1] == 1)
        neighbors.push_back(Point(x, y + 1, 'l'));
}

int zombieTime() {
    bool visited[m][m];
    for(int i = 0; i < m; i++)
        for(int j = 0; j < m; j++)
            visited[i][j] = false;
    visited[dx][dy] = true;
    vector<Point> q;
    q.push_back(Point(dx, dy, 0));
    int dis = 0;
    while (!q.empty()) {
        vector<Point> newpoints;
        dis++;
        for (int i = 0; i < q.size(); i++) {
            vector<Point> neighbors;
            getNeighbors(q[i], neighbors);
            for (int j = 0; j < neighbors.size(); j++) {
                Point p = neighbors[j];
                if (!visited[p.x][p.y]) {
                    visited[p.x][p.y] = true;
                    newpoints.push_back(p);
                    if (mat[p.x][p.y] == 'z')
                        return dis;
                }
            }
        }
        q = newpoints;
    }
    return INF;
}

int rickTime() {
    int distance[m][m];
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < m; j++) {
            distance[i][j] = -1;
        }
    }
    distance[dx][dy] = 0;
    vector<Point> q;
    q.push_back(Point(dx, dy, 0));
    int dis = 0;
    while (!q.empty()) {
        vector<Point> newpoints;
        dis++;
        for (int i = 0; i < q.size(); i++) {
            vector<Point> neighbors;
            goodNeighbors(q[i], neighbors);
            for (int j = 0; j < neighbors.size(); j++) {
                Point p = neighbors[j];
                if (distance[p.x][p.y] == -1) {
                    distance[p.x][p.y] = dis;
                    path[p.x][p.y] = p.dir;
                    newpoints.push_back(p);
                }
                if (distance[p.x][p.y] == dis && path[p.x][p.y] > p.dir)
                    path[p.x][p.y] = p.dir;
            }
        }
        q = newpoints;
    }
    return distance[rx][ry];
}

void populate(int r, int c) {
    prefixSum[max(r - k, 0)][max(c - k, 0)]++;
    if (c + k + 1 <= m - 1) {
        prefixSum[max(r - k, 0)][c + k + 1]--;
    }
    if (r + k + 1 <= m - 1) {
        prefixSum[r + k + 1][max(c - k, 0)]--;
    }
    if (r + k + 1 <= m - 1 && c + k + 1 <= m - 1)
        prefixSum[r + k + 1][c + k + 1]++;
}

int main() {
    scanf("%d%d%d", &m, &n, &k);
    for (int i = 0; i < m; i++) {
        scanf("%s", mat[i]);
        for (int j = 0; j < m; j++) {
            char c = mat[i][j];
            if (c == 's')
                populate(i, j);
            else if (c == 'd') {
                dx = i;
                dy = j;
            } else if (c == 'r') {
                rx = i;
                ry = j;
            }
        }
    }
    int zTime = zombieTime();
    int sum[m];
    for (int i = 0; i < m; i++) {
        sum[i] = 0;
    }
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < m; j++)
            sum[j] += prefixSum[i][j];
        int s = 0;
        for (int j = 0; j < m; j++) {
            s += sum[j];
            prefixSum[i][j] = s;
        }
        for (int j = 0; j < m; j++) {
            if (prefixSum[i][j] >= n && mat[i][j] != 's')
                prefixSum[i][j] = 1;
            else
                prefixSum[i][j] = 0;
        }
    }

    int rTime = rickTime();
    if (rTime == -1 || rTime > zTime) {
        cout << "impossible" << endl;
    } else {
        int i = rx, j = ry;
        while (!(i == dx && j == dy)) {
            printf("%c", path[i][j]);
            if (path[i][j] == 't')
                i--;
            else if (path[i][j] == 'b')
                i++;
            else if (path[i][j] == 'l')
                j--;
            else if (path[i][j] == 'r')
                j++;
        }
    }
    return 0;
}