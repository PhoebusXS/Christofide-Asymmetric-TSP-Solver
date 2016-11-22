import java.util.*;

public class TestTree {
    public static void main (String[] args) {
        Tree t = new Tree(5);
        t.addEdge(0,3);
        System.out.println(Arrays.deepToString(t.table));
    }
}
