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

        int[][] matrix = initializeMatrix(N, V, M);
//        int[][] matrix = {{1, 2, 4, 1}, {3, 4, 1, 3}, {2, 1, 2, 2}, {3, 4, 3, 4}, {0, 0, 0, 0}, {0, 0, 0, 0}};
//        int[][] matrix = {{4, 2, 1, 4}, {4, 3, 3, 3}, {1, 1, 1, 4}, {2, 2, 3, 2}, {0, 0, 0, 0}, {0, 0, 0, 0}};
//        int[][] matrix = {{1, 4, 2, 2}, {4, 1, 3, 4}, {3, 3, 2, 2}, {4, 1, 3, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}};
//        int[][] matrix = {{2, 4, 3, 4}, {4, 3, 1, 1}, {4, 3, 1, 3}, {2, 2, 2, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}};
//        int[][] matrix = {{4, 5, 1, 3}, {2, 1, 1, 2}, {5, 3, 4, 4}, {2, 5, 1, 5}, {3, 4, 3, 2}, {0, 0, 0, 0}, {0, 0, 0, 0}};
//        int[][] matrix = {{2, 10, 4, 4}, {1, 8, 12, 8}, {10, 7, 5, 9}, {5, 3, 2, 5}, {6, 11, 8, 7}, {12, 12, 1, 2}, {4, 7, 8, 11}, {10, 11, 3, 1}, {10, 7, 9, 9}, {6, 2, 6, 11}, {4, 6, 9, 3}, {5, 3, 12, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}};

        System.out.println();
        for (int i = 0; i < N; i++) {
            System.out.println("Пробирка " + i + ": " + Arrays.toString(matrix[i]));
        }

        for (int i = 0; i < 100; i++) {
            if (!isLastMove) {
                makeMove(matrix, M, V);
            }
        }

        System.out.println();
        System.out.println("Ходов больше сделать нельзя. Финальное состояние матрицы: ");
        for (int i = 0; i < N; i++) {
            System.out.println("Пробирка " + i + ": " + Arrays.toString(matrix[i]));
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

    public static int[][] makeMove(int[][] matrix, int M, int V) {
        int maxCountRow = 0;
        int numVialRow = 0;
        int neededTopRow = 1;
        int countRow;
        int topRow;
        boolean isEmpty = false;
        boolean isFull = false;
        boolean isCanPour;
        for (int i = 0; i < matrix.length - (matrix.length - M); i++) {
            isCanPour = true;
            countRow = 1;
            topRow = findTop(matrix[i]);
            if (topRow == 0) {
                continue;
            }
            int pos = findPositionTop(matrix[i]);
            for (int j = pos; j < matrix[i].length - 1; j++) {
                if (topRow == matrix[i][j + 1]) {
                    countRow++;
                    if ((j + 1) == (matrix[i].length - 1) && (countRow == matrix[i].length || countRow == matrix[i].length - 1)) {
                        countRow = 0;
                        isCanPour = false;
                    }
                } else {
                    break;
                }
            }

            if (isCanPour) {
                isCanPour = isCanPour(matrix, M, topRow, i, pos, countRow);
            }
//            System.out.println();
//            System.out.print("Можем перелить цифру " + topRow + " из колбы номер " + i + " позиция: " + pos + " - ");
//            System.out.println(isCanPour);
            if (countRow > maxCountRow && isCanPour) {

                maxCountRow = countRow;
                numVialRow = i;
                neededTopRow = topRow;

            }
        }

        if (maxCountRow == 0) {
            isLastMove = true;
            return matrix;
        }

//        System.out.println(numVialRow);
//        System.out.println(neededTopRow);
//        System.out.println(maxCountRow);
//        System.out.println();

        int maxCountCol = 0;
        int numVialCol = 0;
        int neededTopCol = 1;
        int countCol = 1;
        int topCol = 0;

        if (isEmptyExists(matrix, M)) {
            // - 1
            for (int i = 0; i < matrix.length - (matrix.length - M) - 1; i++) {
                countCol = 1;
                topCol = findTop(matrix[i]);
                // matrix[i].length - 1
                for (int j = i; j < M - 1; j++) {
                    int pos = findPositionTop(matrix[j + 1]);
                    if (topCol == matrix[j + 1][pos]) {
                        // j + 1 != matrix[i].length
                        if (pos != matrix[i].length - 1) {
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

//        System.out.println(numVialCol);
//        System.out.println(neededTopCol);
//        System.out.println(maxCountCol);
//        System.out.println();


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
//            System.out.println(Arrays.deepToString(matrix));

            int countNull = 0;
            for (int i = 0; i < matrix.length; i++) {
                countNull = 0;

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
                                System.out.println("(" + numVialRow + ", " + i + ")");
                            } else {
                                countPour2++;
                            }
                        }
                        break;
                    }
                }
                if (i != matrix.length - 1) {
                    continue;
                }

                for (int j = 0; j < matrix.length; j++) {
                    if (isEmpty(matrix[j]) && numVialRow != j) {
//                        System.out.println();
//                        System.out.println("deleter j: " + j + "   i: " + i);
//                        System.out.println();
                        countNull = matrix[j].length;
                        for (int k = 0; k < maxCountRow; k++) {
                            matrix[j][matrix[j].length - k - 1] = neededTopRow;
                            System.out.println("(" + numVialRow + ", " + j + ")");
                        }
                        break;
                    }
                }



            }
        } else {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < matrix.length - (matrix.length - M); i++) {
                int top = findTop(matrix[i]);
                int pos = findPositionTop(matrix[i]);
                if (top == neededTopCol && pos != matrix[i].length - 1) {
//                    System.out.println("top: " + top + "   pos: " + pos);
                    list.add(i);
                    matrix[i][pos] = 0;
                }
            }
//            System.out.println(Arrays.deepToString(matrix));

            int countNull = 0;
            for (int i = 0; i < matrix.length; i++) {
                countNull = 0;
                if (isEmpty(matrix[i])) {
                    countNull = matrix[i].length;
                    for (int j = 0; j < maxCountCol; j++) {
                        matrix[i][matrix[i].length - j - 1] = neededTopCol;
                    }
                    for (Integer integer : list) {
                        System.out.println("(" + integer + ", " + i + ")");
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
                                System.out.println("(" + numVialCol + ", " + i + ")");
                            } else {
                                countPour++;
                            }
                        }
                        break;
                    }
                }
            }
        }

        System.out.println();
        for (int i = 0; i < matrix.length; i++) {
            System.out.println("Пробирка " + i + ": " + Arrays.toString(matrix[i]));
        }

        return matrix;
    }

    public static boolean isCanPour(int[][] matrix, int M, int top, int posI, int posJ, int count) {
        boolean isLastPos = false;
        if (count != 1) {
            isLastPos = posJ + count - 1 == matrix[0].length - 1;
        }
        for (int i = 0; i < matrix.length; i++) {
            int topInEachArr = findTop(matrix[i]);
            int posInEachArr = findPositionTop(matrix[i]);

            boolean isLastPosArr = false;
            if (count != 1) {
                int countInEachArr = 1;
                for (int j = posInEachArr; j < matrix[i].length - 1; j++) {
                    countInEachArr = 1;
                    if (matrix[i][j] == matrix[i][j + 1]) {
                        countInEachArr++;
                    }
                }
                isLastPosArr = posInEachArr + countInEachArr - 1 == matrix[i].length - 1;
            }

//            System.out.print("posArr: " + posInEachArr + " count: " + count + " topArr: " + topInEachArr + " top: " + top + "posI: " + posI + " i: " + i + " isLastPos " + isLastPos + " isLastPosArr " + isLastPosArr + "       ");
            if ((posInEachArr >= count && topInEachArr == top && posI != i || isEmpty(matrix[i])) && (!isLastPos || isLastPosArr)) {
                return true;
            }
        }
        return false;
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