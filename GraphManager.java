import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * GraphManager 클래스 - 생성한 그래프 관리 및 탐색 실행 메소드 갖춤
 */
public class GraphManager {
    String fileName;    //input 파일 이름 저장
    boolean isWeight;  //input1.txt와 input2.txt 구별하기 위해 추가. 가중치그래프라면 isWeight = true;
    ArrayList<Graph> graphs;    //제작한 graph들을 담고, 관리

    public GraphManager(String fileName, boolean isWeight) {
        this.fileName = fileName;
        this.graphs = new ArrayList<>();
        this.isWeight = isWeight;
        //input1과 input2의 그래프 초기화 구별
        if (!isWeight)
            graphSet(fileName);
        else
            graphSet2(fileName);
    }

    /**
     * input1.txt 그래프 초기화
     *
     * @param fileName
     */
    private void graphSet(String fileName) {
        try (Scanner scan = new Scanner(new File(fileName));) {
            int graphInitialIndex = 0;  //graphs에 담긴 그래프 객체를 가리키기 위해 초기화 시에만 사용.
            /*파일 전체*/
            while (scan.hasNextLine()) {
                int vertex = scan.nextInt();    //정점 개수만큼 받아옴
                this.graphs.add(new Graph(vertex)); //vertex만큼의 개수를 가진 Graph 추가

                /*그래프마다 추가*/
                for (int i = 0; i < vertex; i++) {  //그래프 1개에서 라인을 받아오기 위한 반복의 역할
                    int startNode = scan.nextInt(); //인접 행렬에서 기준 vertex
                    String line = scan.nextLine(); //한 줄을 String으로 저장

                    /*공백처리를 위해 한 줄을 버퍼로 생각하고 입력받음*/
                    Scanner lineScan = new Scanner(line);
                    while (lineScan.hasNext()) {
                        int node = lineScan.nextInt();  //인접 노드
                        this.graphs.get(graphInitialIndex).matrix[startNode - 1][node - 1] = 1; //인덱스가 중요. 0번 인덱스부터 생각해야함
                    }
                }
                graphInitialIndex++;    //2,3..번째 그래프 받기 위해 증가
            }
        } catch (FileNotFoundException e) {
            System.out.println("파일이 존재하지 않습니다.");
        }
    }

    /**
     * input2.txt 그래프 초기화
     *
     * @param fileName
     */
    private void graphSet2(String fileName) {
        try (Scanner scan = new Scanner(new File(fileName));) {
            int graphInitialIndex = 0;  //초기화 시에만 사용
            while (scan.hasNextLine()) {
                int vertex = scan.nextInt();    //정점 개수만큼 받아옴
                this.graphs.add(new Graph(vertex));

                for (int i = 0; i < vertex; i++) {  //반복의 역할만 함
                    int startNode = scan.nextInt(); //처음 vertex 번호 나타내는 숫자 저장
                    String line = scan.nextLine(); //한 줄을 String으로 저장
                    Scanner lineScan = new Scanner(line);
                    while (lineScan.hasNext()) {
                        int node = lineScan.nextInt();  //노드
                        int weight = lineScan.nextInt();    //가중치
                        this.graphs.get(graphInitialIndex).matrix[startNode - 1][node - 1] = weight;    //가중치 그래프는, 인접행렬을 1 대신 가중치로 표현할 것
                    }

                    //엣지가 존재하지 않는 경우, Integer.MAX_VALUE로 초기화 (inf를 Integer.MAX_VALUE로 표현)
                    for (int j = 0; j < vertex; j++) {
                        if (this.graphs.get(graphInitialIndex).matrix[startNode - 1][j] != 0) continue;
                        else if ((startNode - 1) != j)  //인접 행렬의 entry가 0이고, 같은 노드가 아닐 경우
                            this.graphs.get(graphInitialIndex).matrix[startNode - 1][j] = Integer.MAX_VALUE;
                    }
                }
                graphInitialIndex++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("파일이 존재하지 않습니다.");
        }
    }

    /**
     * DFS, BFS, Dijkstra 알고리즘 실행 관리 메소드
     */
    public void Run() {
        if (!this.isWeight)
            RunDfsBfs();    //input1.txt
        else
            RunDijkstra();  //input2.txt
    }

    /**
     * dfs, bfs를 실행하는 메소드
     */
    public void RunDfsBfs() {
        ArrayList<Integer> traversal = new ArrayList<>();   //경로 추적에 이용
        int graphIndex = 0; //graphs에 담긴 그래프의 index
        for (Graph g : graphs) {
            /*시작*/
            System.out.println("그래프 [" + (graphIndex + 1) + "]");
            System.out.println("-".repeat(30));

            /*탐색부분*/
            System.out.println("깊이 우선 탐색");
            dfs(g, new boolean[g.vertex], traversal, 1);   //input 파일 상, 시작 노드는 항상 1부터 시작.
            //깊이 우선 탐색 출력
            printTraversal(traversal);
            traversal.clear();

            System.out.println("너비 우선 탐색");
            bfs(g, new int[g.vertex + 1], new boolean[g.vertex], traversal, 1); //new int[g.vertex+1] : 포인터가 배열의 끝 뒤에 한 공간을 더 가리키는 것을 고려하여 크기를 1 더 크게 잡아줌
            //너비 우선 탐색 출력
            printTraversal(traversal);
            traversal.clear();

            /*끝*/
            System.out.println("=".repeat(30));
            System.out.println();
            if (graphIndex < graphs.size()) //다음 그래프 준비
                graphIndex++;
        }
    }

    /**
     * dfs 수행
     *
     * @param graph       - graphs에 담긴 각 그래프
     * @param isVisited   - 방문 검사 배열
     * @param traversal   - 방문 경로
     * @param currentNode - 현재 노드
     */
    public void dfs(Graph graph, boolean[] isVisited, ArrayList<Integer> traversal, int currentNode) {
        isVisited[currentNode - 1] = true;    //현재 노드 방문했으므로 true
        traversal.add(currentNode);    //방문 경로에 현재 노드 추가

        for (int i = 0; i < graph.vertex; i++) {   //currentNode 인접확인을 위해 인접행렬의 행 개수(=노드 개수)만큼 반복
            if (!isVisited[i] && graph.matrix[currentNode - 1][i] == 1) //방문하지 않은 인접한 정점이라면 dfs()로 호출
                dfs(graph, isVisited, traversal, i + 1); //i는 인덱스이므로, 1 증가해서 currentNode를 인자로 줌
        }
    }

    /**
     * BFS 탐색
     *
     * @param graph     - graphs에 담긴 각 그래프
     * @param queue     - BFS 탐사시 vertex를 저장할 배열
     * @param isVisited - 방문 검사 배열
     * @param traversal - 방문 경로
     * @param startNode - 시작 노드
     */
    private void bfs(Graph graph, int[] queue, boolean[] isVisited, ArrayList<Integer> traversal, int startNode) {
        int front = 0;  //queue의 head를 가리키는 index
        int rear = 0;   //queue의 tail을 가리키는 index, queue에 대입하는 과정은 rear을 이용해 대입할 것.
        int currentNode;    //현재 노드를 startNode로 초기화.

        /*첫 노드를 queue에 초기화*/
        currentNode = startNode;
        queue[rear++] = currentNode; //rear 부분에 처음 노드 추가 후, rear++

        /*탐색 진행*/
        for (int i = 0; i < graph.vertex; i++) {    //front가 한번 탐색시마다 queue를 1의 index씩 이동. 총 vertex개수만큼 반복함.
            /*현재 노드를 경로에 추가*/
            currentNode = queue[front]; //현재 노드
            if (!isVisited[currentNode - 1])
                isVisited[currentNode - 1] = true;//front가 가리키는 노드 방문 처리
            traversal.add(currentNode); //경로에 queue의 front가 가리키는 노드 추가
            queue[front++] = 0; //기존 queue[front] 부분에 0 대입(Queue처럼 제거 처리). 이후 front++

            /*현재 노드와 인접한 노드를 queue에 추가*/
            for (int j = 0; j < graph.vertex; j++) {    //각 currentnode에 대해 인접한 노드 검사
                if (!isVisited[j] && graph.matrix[currentNode - 1][j] == 1) {
                    queue[rear++] = j + 1;  //queue에 방문 노드 추가
                    if (!isVisited[j])
                        isVisited[j] = true;    //방문체크. 이전에 방문한 적이 없다면 true로
                }
            }
        }
    }

    /**
     * DFS, BFS 경로 출력 메소드
     *
     * @param traversal
     */
    public void printTraversal(ArrayList<Integer> traversal) {
        for (int i = 0; i < traversal.size(); i++) {
            if (!(i == traversal.size() - 1))
                System.out.print(traversal.get(i) + " - ");
            else
                System.out.print(traversal.get(i));
        }
        System.out.println();
    }

    /**
     * dijkstra 실행하는 메소드
     */
    public void RunDijkstra() {
        int graphLabel = 1;
        for (Graph g : graphs) {
            System.out.println("그래프 [" + graphLabel + "]");
            System.out.println("-".repeat(30));
            System.out.println("시작점 : " + 1);
            for (int i = 1; i < g.vertex; i++) {
                System.out.print("정점 [" + (i + 1) + "]: ");
                dijkstra(g, 0, i);  //시작점과 종료 정점의 index를 인자로 줌
            }

            System.out.println("=".repeat(30));
            System.out.println();
            if (graphLabel < graphs.size())
                graphLabel++;
        }
    }

    /**
     * Dijkstra 알고리즘 메소드
     *
     * @param graph      - Dijkstra를 진행할 그래프
     * @param startIndex - Dijkstra 시작점(1)
     * @param endIndex   - 특정 정점까지의 최단경로를 구하는 과정에서의 `특정 정점`
     */
    private void dijkstra(Graph graph, int startIndex, int endIndex) {
        int[] D = new int[graph.vertex];    //거리 저장 배열
        boolean[] visited = new boolean[graph.vertex];  // 방문 확인 배열
        int[] previous = new int[graph.vertex]; // 경로 추적을 위한 배열

        /*인접 행렬의 1행으로 D(거리)를 초기화*/
        for (int i = 0; i < D.length; i++) {
            D[i] = graph.matrix[0][i];
        }

        Arrays.fill(previous, startIndex); // 모든 경로의 시작점은 1이므로, 이전 노드를 0(시작점의 index)로 초기화
        previous[startIndex] = -1;  //역추적시 시작점에 도착하면 종료하게 제작하기 위해, 시작점은 0으로 초기화
        visited[startIndex] = true; //시작점은 미리 방문 처리

        for (int i = 0; i < graph.vertex - 1; i++) {    //Dijkstra 알고리즘의 탐색 횟수는 vertex - 1회만 진행하면 됨. 탐색을 위한 반복 용도의 for문
            int currentNodeIndex = -1;   //현재 최단 거리를 가진 정점을 구하기 위한 변수

            // 방문하지 않은 노드 중 최단 거리를 가진 노드 선택
            for (int j = 0; j < graph.vertex; j++) {
                if (!visited[j] && (currentNodeIndex == -1 || D[j] < D[currentNodeIndex])) {  //current가 -1인 경우 : 처음 시작하는 경우이므로 current에 j 대입.
                    currentNodeIndex = j;
                }
            }

            if (D[currentNodeIndex] == Integer.MAX_VALUE) break; // 최단 거리의 정점의 값도 Inf라면, break - 더 이상 최단 거리가 존재하지 X
            visited[currentNodeIndex] = true;   //최단 거리 노드 방문 처리

            // 현재 노드에서 연결된 모든 노드의 거리 갱신
            for (int next = 0; next < graph.vertex; next++) {
                if (graph.matrix[currentNodeIndex][next] != Integer.MAX_VALUE && !visited[next]) {  //인접행렬의 값이 inf가 아니고, 방문하지 않았다면
                    int newDistance = D[currentNodeIndex] + graph.matrix[currentNodeIndex][next];   //기존 거리와 비교를 위한 변수 선언
                    if (newDistance < D[next]) {    //기존 거리와 비교시 더 작다면
                        D[next] = newDistance;  //새롭게 최단 거리로 선택
                        previous[next] = currentNodeIndex; // 이전 노드 저장
                    }
                }
            }
        }
        /*Dijkstra 최단 경로 찾기*/
        findDijkstraTraversal(previous, D, startIndex, endIndex);
    }

    /**
     * Dijkstra 알고리즘 정점 별 최단 경로 출력
     *
     * @param previous   - Dijkstra 알고리즘에서 이전 노드를 기록한 배열
     * @param D          - 거리
     * @param startIndex - 시작점(1)
     * @param endIndex   - 최단 경로를 구할 정점
     */
    private void findDijkstraTraversal(int[] previous, int[] D, int startIndex, int endIndex) {
        ArrayList<Integer> traversal = new ArrayList<>();
        int traversalIndex = endIndex;
        /*시작점을 만날 때까지, 현재 정점을 경로에 추가하고, traversalIndex를 previous[traversalIndex](=이전 노드)로 변경*/
        while (traversalIndex != -1) {
            traversal.add(traversalIndex + 1);
            traversalIndex = previous[traversalIndex];
        }

        /*역추적한 경로를 뒤집어서 본래 경로로 만들어줌*/
        Collections.reverse(traversal);

        //구한 본래 경로의 첫번째 요소가 시작점이 맞다면 경로 출력
        if (traversal.get(0) == startIndex + 1) {
            for (int i = 0; i < traversal.size(); i++) {
                if (i != traversal.size() - 1)
                    System.out.print(traversal.get(i) + " - ");
                else
                    System.out.print(traversal.get(i));
            }

        } else {
            System.out.println("경로를 찾을 수 없습니다.");
        }
        
        /*최단 길이 출력*/
        System.out.println(", 길이: " + D[endIndex]);  //endIndex까지에 해당하는 길이 구함
    }

    /**
     * 인접행렬 구현 확인용 메소드
     *
     * @return
     */
    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < graphs.size(); i++) {
            str += "=== " + (i + 1) + "번째 그래프 ===\n";
            str += graphs.get(i).toString() + "\n";
        }
        return str;
    }
}

