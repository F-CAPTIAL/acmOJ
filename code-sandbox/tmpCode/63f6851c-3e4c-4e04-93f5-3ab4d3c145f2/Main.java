import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner cin = new Scanner(System.in);
        int i = 0;
        int a;
        int[] list = null;
        a = cin.nextInt();
        list = new int[a];
        while (i < a) {
            list[i++] = cin.nextInt();
        }

        for (int j = 0; j < list.length; j++) {
            System.out.println(list[j]);
        }
    }
}
