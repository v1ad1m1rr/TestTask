import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    public static boolean isLastMove = false;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Введите количество пробирок (N): ");
        int N = Integer.parseInt(reader.readLine());
        System.out.print("Введите объем пробирки (V): ");
        int V = Integer.parseInt(reader.readLine());
        System.out.print("Введите количество цветов (M): ");
        int M = Integer.parseInt(reader.readLine());
        reader.close();

//        int[][] matrix = initializeMatrix(N, V, M);
        int[][] matrix = {{4, 2, 1, 4}, {4, 3, 3, 3}, {1, 1, 1, 4}, {2, 2, 3, 2}, {0, 0, 0, 0}, {0, 0, 0, 0}};

        System.out.println();
        for (int i = 0; i < N; i++) {
            System.out.println("Пробирка " + i + ": " + Arrays.toString(matrix[i]));
        }

        for (int i = 0; i < 30; i++) {
            if (!isLastMove) {
                makeMove(matrix, M);
            }
        }
//        int[][] afterFirPour = firstPour(matrix, M);
//        System.out.println(Arrays.deepToString(afterFirPour));
//
//        int[][] afterPour = pour(matrix, M);
//        System.out.println(Arrays.deepToString(afterPour));
    }

    public static int[][] initializeMatrix(int N, int V, int M) {
        int[][] matrix = new int[N][V];
        List<Integer> colors = new ArrayList<>();

        for (int i = 1; i <= M; i++) {
            for (int j = 0; j < V; j++) {
                colors.add(i);
            }
        }

        Collections.shuffle(colors);

        int index = 0;
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < V; j++) {
                matrix[i][j] = colors.get(index++);
            }
        }
        return matrix;
    }

//    public static int[][] firstPour(int[][] matrix, int M) {
//        int countMatchFirLn = 0;
//        for (int i = 0; i < matrix[0].length - 1; i++) {
//            for (int j = i; j < matrix[i].length - 1; j++) {
//                if (matrix[i][0] == matrix[j + 1][0]) {
//                    countMatchFirLn++;
//                    if (countMatchFirLn == 1) {
//                        matrix[M][matrix[0].length - 1] = matrix[i][0];
//                        matrix[M][matrix[0].length - 2] = matrix[i][0];
//                        matrix[i][0] = 0;
//                        matrix[j + 1][0] = 0;
//                    }
//                }
//            }
//        }
//        System.out.println(countMatchFirLn);
//        if (countMatchFirLn == 0) {
//            matrix[M][matrix[0].length - 1] = matrix[0][0];
//            matrix[0][0] = 0;
//        }
//        return matrix;
//    }

    public static int[][] makeMove(int[][] matrix, int M) {
        int maxCountRow = 0;
        int numVialRow = 0;
        int neededTopRow = 1;
        int countRow;
        int topRow;
        boolean isEmpty = false;
        boolean isFull = false;
        for (int i = 0; i < matrix.length - (matrix.length - M); i++) {
            countRow = 1;
            topRow = findTop(matrix[i]);
            if (topRow == 0) {
                continue;
            }
            int pos = findPositionTop(matrix[i]);
            for (int j = pos; j < matrix[i].length - 1; j++) {
                if (topRow == matrix[i][j + 1]) {
                    countRow++;
                    if ((j + 1) == (matrix[i].length - 1)) {
                        countRow = 0;
                    }
                } else {
                    break;
                }
            }
            if (countRow > maxCountRow) {
                maxCountRow = countRow;
                numVialRow = i;
                neededTopRow = topRow;
            }
        }

        if (maxCountRow == 0) {
            isLastMove = true;
            return matrix;
        }

        System.out.println(numVialRow);
        System.out.println(neededTopRow);
        System.out.println(maxCountRow);
        System.out.println();

        int maxCountCol = 0;
        int numVialCol = 0;
        int neededTopCol = 1;
        int countCol = 1;
        int topCol = 0;

        if (isEmptyExists(matrix, M)) {
            for (int i = 0; i < matrix.length - (matrix.length - M) - 1; i++) {
                countCol = 1;
                topCol = findTop(matrix[i]);
                for (int j = i; j < matrix[i].length - 1; j++) {
                    int pos = findPositionTop(matrix[j + 1]);
                    if (topCol == matrix[j + 1][pos]) {
                        if ((j + 1) != matrix[i].length && pos != 3) {
                            countCol++;
                        }
                    }
                }
                if (countCol >= maxCountCol && countCol != 1) {
                    maxCountCol = countCol;
                    numVialCol = i;
                    neededTopCol = topCol;
                }
            }
        }

        System.out.println(numVialCol);
        System.out.println(neededTopCol);
        System.out.println(maxCountCol);
        System.out.println();


        if (maxCountRow > maxCountCol) {
            int countPour = maxCountRow;
            for (int i = 0; i < countPour; i++) {
                if (matrix[numVialRow][i] == 0) {
                    countPour++;
                    continue;
                }
                if (matrix[numVialRow][i] == neededTopRow) {
                    matrix[numVialRow][i] = 0;
                }
            }
            System.out.println(Arrays.deepToString(matrix));

            int countNull = 0;
            for (int i = 0; i < matrix.length; i++) {
                countNull = 0;
                if ((i == matrix.length - 1 || i == matrix.length - 2) && isEmpty(matrix[i])) {
                    countNull = matrix[i].length;
                    for (int j = 0; j < maxCountRow; j++) {
                        matrix[i][matrix[i].length - j - 1] = neededTopRow;
                    }
                    break;
                }
                int top = findTop(matrix[i]);
                if (top == neededTopRow) {
                    for (int j = 0; j < matrix[i].length; j++) {
                        if (matrix[i][j] == 0) {
                            countNull++;
                        }
                    }
                    if (countNull >= maxCountRow) {
                        int countPour2 = maxCountRow;
                        for (int j = 0; j < countPour2; j++) {
                            if (matrix[i][matrix[i].length - j - 1] == 0) {
                                matrix[i][matrix[i].length - j - 1] = neededTopRow;
                            } else {
                                countPour2++;
                            }
                        }
                        break;
                    }
                }
            }
        } else {

            for (int i = 0; i < matrix.length - (matrix.length - M); i++) {
                int top = findTop(matrix[i]);
                int pos = findPositionTop(matrix[i]);
                if (top == neededTopCol && pos != matrix[i].length - 1) {
                    System.out.println("top: " + top + "   pos: " + pos);
                    matrix[i][pos] = 0;
                }
            }
            System.out.println(Arrays.deepToString(matrix));

            int countNull = 0;
            for (int i = 0; i < matrix.length; i++) {
                countNull = 0;
                if (isEmpty(matrix[i])) {
                    countNull = matrix[i].length;
                    for (int j = 0; j < maxCountCol; j++) {
                        matrix[i][matrix[i].length - j - 1] = neededTopCol;
                    }
                    break;
                }
                int top = findTop(matrix[i]);
                if (top == neededTopCol) {
                    for (int j = 0; j < matrix[i].length; j++) {
                        if (matrix[i][j] == 0) {
                            countNull++;
                        }
                    }
                    if (countNull >= maxCountCol) {
                        int countPour = maxCountCol;
                        for (int j = 0; j < countPour; j++) {
                            if (matrix[i][matrix[i].length - j - 1] == 0) {
                                matrix[i][matrix[i].length - j - 1] = neededTopCol;
                            } else {
                                countPour++;
                            }
                        }
                        break;
                    }
                }
            }
        }

        System.out.println(Arrays.deepToString(matrix));

        return matrix;
    }

    public static int findTop (int[] vial) {
        int top = 0;
        for (int j : vial) {
            if (j == 0) {
                continue;
            }
            top = j;
            break;
        }
        return top;
    }

    public static int findPositionTop (int[] vial) {
        int pos = 0;
        for (int i = 0; i < vial.length; i++) {
            if (vial[i] == 0) {
                continue;
            }
            pos = i;
            break;        }
        return pos;
    }

//    public static int[][] pour(int[][] matrix, int M) {
//        int secNum = 0;
//        int positionJ = 0;
//        int positionK = 0;
//        for (int i = 0; i < 2; i++) {
//
//            for (int j = positionJ; j < M; j++) {
//                if (matrix[j][0] == 0) {
//                    secNum = matrix[j][1];
//                    positionJ = j;
//                    positionK = i;
//                    break;
//                }
//            }
//            System.out.println("secNum: " + secNum);
//            System.out.println("positionJ: " + positionJ);
//
//            for (int j = 0; j < M; j++) {
//                if (j != positionJ && matrix[j][i] == secNum && !isCorrectPosition(matrix[j])) {
//                    matrix[positionJ][positionK] = matrix[j][i];
//                    matrix[j][i] = 0;
//                    break;
//                }
//                System.out.println("Нет подходящих перестановок");
//            }
//            positionJ++;
//        }
//        return matrix;
//    }

    public static boolean isEmpty(int[] vial) {
        return vial[vial.length - 1] == 0;
    }

    public static boolean isFull(int[] vial) {
        return vial[0] != 0;
    }

    public static boolean isEmptyExists(int[][] matrix, int M) {
        for (int i = M; i < matrix.length; i++) {
            if (isEmpty(matrix[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCorrectPosition(int[] vial) {
        for (int i = 0; i < vial.length - 1; i++) {
            if (vial[i] == 0 || i == vial.length - 1) {
                continue;
            }
            if (vial[i] == vial[i + 1]) {
                System.out.println(Arrays.toString(vial));
                System.out.println("Цифра уже в правильной позиции");
                return true;
            }
        }

        return false;
    }
}