
// package com.company;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;

public class CCS16 {
    // main function

    public static byte[] randomBytes;

    public static void main(String[] args) throws Exception {
        runDPF();
        testRunTime();
    }

    // 测试函数运行时间
    public static void testRunTime() throws Exception {
        long genTime, evalTime;
        long startTime, endTime;

        //  测试需要的参数
        randomBytes = new byte[4];
        new SecureRandom().nextBytes(randomBytes);
        int lambda = 20;
        String alpha = "1001101";
        int n = alpha.length();
        int beta = 202306;
        String x = "1001101";

        // 屏蔽输出
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);

        startTime = System.currentTimeMillis();
        String[] k = Gen(lambda, alpha, beta, 100);   
        endTime = System.currentTimeMillis();
        genTime = endTime - startTime;
        
        startTime = System.currentTimeMillis();
        int eval0 = Eval(0, k[0], x, alpha, lambda);
        endTime = System.currentTimeMillis();
        evalTime = endTime - startTime;

        // 输出测试结果
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        System.out.printf("Gen time: %d ms.\n", genTime);
        System.out.printf("Eval time: %d ms.\n", evalTime);
    }

    // 运行DPF方案
    public static void runDPF() throws Exception{
        randomBytes = new byte[4];
        new SecureRandom().nextBytes(randomBytes);

        int lambda = 20;
        String alpha = "1001101";
        int n = alpha.length();
        int beta = 202306;

        // Generate k[0] and k[1]
        String[] k = Gen(lambda, alpha, beta, 100);

        // Evaluate f(0) and f(1) for a given x
        String x = "1001101";
        int eval0 = Eval(0, k[0], x, alpha, lambda);
        // int eval0 = 111;
        int eval1 = Eval(1, k[1], x, alpha, lambda);

        System.out.println("*****************In Main*****************");
        // Print the results
        System.out.println("Size of alpha: " + n);
        System.out.println("alpha: " + alpha);
        System.out.println("beta: " + beta);
        System.out.println("k[0]: " + k[0]);
        System.out.println("k[1]: " + k[1]);
        System.out.println("x: " + x);
        System.out.println("f(0): " + eval0);
        System.out.println("f(1): " + eval1);
        System.out.println("sum:" + (eval0 + eval1));

        if ((eval0 + eval1) == beta) {
            System.out.println("VERIFICATION PASSED!");
        }
    }

    // Gen(1^{\lambda}, \alpha, \beta, \mathbb{G})
    static String[] Gen(int lambda, String alpha, int beta, int mathbbG) throws Exception {
        System.out.println("----------------------------IN Gen----------------------------");
        String[] result = new String[2];
        String one = "1";
        String zero = "0";
        int Keep, Lose, L = 0, R = 1;
        String[][] s = new String[1000][2];

        s[0][0] = generateRandomBinaryString(lambda);
        s[0][1] = generateRandomBinaryString(lambda);

        System.out.println("s[0][0]:" + s[0][0]);
        System.out.println("s[0][1]:" + s[0][1]);

        String[][] t = new String[1000][2];
        t[0][0] = "0";
        t[0][1] = XOR(t[0][0], one);

        System.out.println("t[0][0]:" + t[0][0]);
        System.out.println("t[0][1]:" + t[0][1]);

        int n = alpha.length();
        String[] CW = new String[1000];
        for (int i = 1; i <= n; i++) {
            String[] s0 = new String[2];
            String[] s1 = new String[2];
            String[] t0 = new String[2];
            String[] t1 = new String[2];
            String Gresult0 = G(s[i - 1][0]);

            // System.out.println("Gresutlt_0:" + Gresult0);

            s0[0] = Gresult0.substring(0, lambda);
            t0[0] = Gresult0.substring(lambda, lambda + 1);
            s0[1] = Gresult0.substring(lambda + 1, 2 * lambda + 1);
            t0[1] = Gresult0.substring(2 * lambda + 1, 2 * lambda + 2);
            String Gresult1 = G(s[i - 1][1]);

            s1[0] = Gresult1.substring(0, lambda);
            t1[0] = Gresult1.substring(lambda, lambda + 1);
            s1[1] = Gresult1.substring(lambda + 1, 2 * lambda + 1);
            t1[1] = Gresult1.substring(2 * lambda + 1, 2 * lambda + 2);

            // ! mark
            System.out.println("-1:s[" + (i - 1) + "][1]:" + s[i-1][1]);
            System.out.println("Gresutlt_1:" + Gresult1);
            // System.out.println("tau[" + i + "]:" + 
            System.out.println("sL:" + s0[0]);

            if (alpha.charAt(i - 1) == zero.charAt(0)) {
                Keep = L;
                Lose = R;
                // System.out.println("----- L -----");
            } else {
                Keep = R;
                Lose = L;
                // System.out.println("----- R -----");
            }
            String sCW = XOR(s0[Lose], s1[Lose]);
            String[] tCW = new String[2];
            tCW[0] = XOR(t0[0], t1[0], alpha.substring(i - 1, i), one);
            tCW[1] = XOR(t0[1], t1[1], alpha.substring(i - 1, i));
            CW[i] = sCW.concat(tCW[0]).concat(tCW[1]);

            System.out.println("CW[" + i + "]:" + CW[i]);

            if (t[i - 1][0].equals("0")) {
                s[i][0] = s0[Keep];
                t[i][0] = t0[Keep];
            } else {
                s[i][0] = XOR(s0[Keep], sCW);
                t[i][0] = XOR(t0[Keep], tCW[Keep]);
            }
            if (t[i - 1][1].equals("0")) {
                s[i][1] = s1[Keep];
                t[i][1] = t1[Keep];
            } else {
                s[i][1] = XOR(s1[Keep], sCW);
                t[i][1] = XOR(t1[Keep], tCW[Keep]);
            }

            System.out.println("s[" + i + "][1]:" + s[i][1]);
        }

        System.out.println("s[" + n + "][0]:" + s[n][0]);
        System.out.println("s[" + n + "][1]:" + s[n][1]);
        System.out.println("t[" + n + "][0]" + t[n][0]);
        System.out.println("t[" + n + "][1]" + t[n][1]);

        CW[n + 1] = String.valueOf(
                (int) (Math.pow(-1, Integer.parseInt(t[n][1]))) * (beta - convert(s[n][0]) + convert(s[n][1])));

        System.out.println("CW[" + (n + 1) + "]:" + CW[n + 1]);

        String[] k = new String[2];
        k[0] = s[0][0].concat(t[0][0]);
        k[1] = s[0][1].concat(t[0][1]);
        for (int i = 1; i <= n + 1; i++) {
            k[0] = k[0].concat(CW[i]);
            k[1] = k[1].concat(CW[i]);
        }
        result = k;
        return result;
    }

    // Eval(b, kb, x)
    static int Eval(int b, String kb, String x, String alpha, int lambda) throws Exception {
        System.out.println("----------------------------IN Eval--------------------------");
        int result;
        String one = "1";
        String zero = "0";
        StringBuilder zeroString = new StringBuilder();
        for (int i = 0; i < 2 * lambda + 2; i++) {
            zeroString.append("0");
        }

        final int MAX_SIZE = 1000;
        String[] s = new String[MAX_SIZE];
        String[] t = new String[MAX_SIZE];
        String[] cw = new String[MAX_SIZE];

        // parse s0, t0
        s[0] = kb.substring(0, lambda);
        t[0] = kb.substring(lambda, lambda + 1);
        System.out.println("s[0]:" + s[0]);
        System.out.println("t[0]:" + t[0]);
        // parse CW

        int n = alpha.length();
        int j = lambda + 1;
        for (int i = 1; i <= n; i++) {
            cw[i] = kb.substring(j, j + lambda + 2);
            j += lambda + 2;
            System.out.println("cw[" + i + "]:" + cw[i]);
        }
        cw[n + 1] = kb.substring(j);
        System.out.println("cw[" + (n + 1) + "]:" + cw[n + 1]);
        // line 2-7
        String sCW;
        String[] tCW = new String[2];
        String[] tau = new String[MAX_SIZE];
        for (int i = 1; i <= n; i++) {
            // Parse CW[i]
            sCW = cw[i].substring(0, lambda);
            tCW[0] = cw[i].substring(lambda, lambda + 1);
            tCW[1] = cw[i].substring(lambda + 1, lambda + 2);

            // ! mark 
            System.out.println("-1:s[" + (i - 1) + "]:" + s[i-1]);
            System.out.println("Gresutlt:" + G(s[i - 1]));

            // tau
            if (t[i - 1].equals("0")) {
                tau[i] = G(s[i - 1]);
            } else {
                String str = sCW.concat(tCW[0]).concat(sCW).concat(tCW[1]);
                tau[i] = XOR(G(s[i - 1]), str);
            }

            // parse tau
            String sL, tL, sR, tR;
            sL = tau[i].substring(0, lambda);
            tL = tau[i].substring(lambda, lambda + 1);
            sR = tau[i].substring(lambda + 1, 2 * lambda + 1);
            tR = tau[i].substring(2 * lambda + 1, 2 * (lambda + 1));

            System.out.println("sL:" + sL);

            // line 6
            if (x.charAt(i - 1) == zero.charAt(0)) {
                s[i] = sL;
                t[i] = tL;
                System.out.println("----- L -----");
            } else {
                s[i] = sR;
                t[i] = tR;
                System.out.println("----- R -----");
            }

            // System.out.println("s[" + i +"]:" + s[i]);
        }

        System.out.println("s[" + n + "]:" + s[n]);
        System.out.println("t[" + n + "]:" + t[n]);

        result = (b % 2 == 0 ? 1 : -1) * (convert(s[n]) + convert(t[n]) * Integer.parseInt(cw[n + 1]));
        return result;
    }

    // convert:
    static int convert(String s) {
        // ensure sizeof(s)=lambda
        int result = Integer.parseInt(s, 2) + 996;
        return result;
    }

    public static String XOR(String... binaryStrings) {
        int maxLength = binaryStrings[0].length(); // 获取输入二进制字符串中的最大长度
        for (String binaryString : binaryStrings) {
            if (binaryString.length() != maxLength) {
                throw new IllegalArgumentException("输入的二进制字符串长度不一致");
            }
        }
        BigInteger result = BigInteger.ZERO;
        for (String binaryString : binaryStrings) {
            BigInteger binaryValue = new BigInteger(binaryString, 2); // 将二进制字符串转换为 BigInteger 类型
            result = result.xor(binaryValue); // 对所有二进制数进行异或操作
        }
        String binaryResult = result.toString(2); // 将异或结果转换为二进制字符串
        while (binaryResult.length() < maxLength) {
            // 如果返回的二进制字符串长度不足，前面用0进行填充
            binaryResult = "0" + binaryResult;
        }
        return binaryResult;
    }

    public static String generateRandomBinaryString(int m) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < m; i++) {
            sb.append(random.nextInt(2));
        }
        return sb.toString();
    }


    public static String G(String s) throws Exception {

        BigInteger sInt = new BigInteger(s, 2);
        BigInteger randomInt = new BigInteger(1, randomBytes);
        BigInteger sumInt = sInt.add(randomInt);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(sumInt.toByteArray());

        // 取哈希值的前 n 个比特
        int n = s.length() * 2 + 2;
        byte[] firstNBits = Arrays.copyOf(hashBytes, (n + 7) / 8);
        firstNBits[0] &= (byte) (0xff >> (8 - n % 8));

        BigInteger firstNBitsInt = new BigInteger(1, firstNBits);
        String result = firstNBitsInt.toString(2);

        // 补齐长度
        while (result.length() < n) {
            result = "0" + result;
        }

        return result;
    }

}
