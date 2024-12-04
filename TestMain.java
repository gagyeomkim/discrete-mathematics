/**
 * TestMain 클래스
 */
public class TestMain {
    public static void main(String[] args) {
        GraphManager gm1 = new GraphManager("src/input1.txt", false);
//        System.out.println(gm1);   //체크 완료
        System.out.println("1. 그래프 탐방 수행 결과");
        System.out.println();
        gm1.Run();

        GraphManager gm2 = new GraphManager("src/input22.txt", true);
//        System.out.println(gm2);  //체크완료
        System.out.println("2. 최단 경로 구하기 수행 결과");
        System.out.println();
        gm2.Run();
    }
}
