/**
 * Graph 클래스
 */
public class Graph {
    int vertex;
    int[][] matrix;

    public Graph(int vertex) {
        this.vertex = vertex;
        this.matrix = new int[this.vertex][this.vertex];
    }

    @Override
    public String toString() {
        String str = "";
        str += "vertex :  " + vertex + "\n";
        str += "--- 인접행렬 ---\n";
        for (int i = 0; i < vertex; i++) {
            for (int j = 0; j < vertex; j++) {
                str += this.matrix[i][j] + " ";
            }
            str += "\n";
        }
        return str;
    }
}
