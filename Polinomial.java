import java.util.Scanner;
import java.util.ArrayList;
import java.lang.Math;

public class Polinomial {
    private static Scanner in = new Scanner(System.in);
    private static final double EPSILON = 1e-6;

    public static void main(String[] args) {
        System.out.println("--------------- [ CALCULADOR DE POLINOMIOS ] ---------------");

        int grau = readGrau();
        ArrayList<Double> coeficientes = readCoeficientes(grau);

        double quotasKojima[] = metodoKojima(coeficientes, grau);
        double quotaCauchy = metodoCauchy(coeficientes, grau);
        double quotasMaximo[] = metodoMaximoMinimo(coeficientes, grau);
        //double bestQuotas[] = computeQuotas(quotasKojima, quotasCauchy, quotasMaximo);

        //double root = computeRoot(bestQuotas);

        //printaResultados(grau, coeficientes, bestQuotas, root);
    }

    private static int readGrau() {
        System.out.println("\n ---------- [ LEITURA DO GRAU ] ----------");
        int grau = 0;

        do {
            System.out.print("Informe o grau correspondente ao polinomio: ");
            grau = in.nextInt();

            if(grau < 0) System.out.println("O valor inserido é inválido [ grau >= 0 ]");
        } while (grau < 0);

        return grau;
    }

    private static ArrayList<Double> readCoeficientes(int grau) {
        System.out.println("\n ---------- [ LEITURA DOS COEFICIENTES ] ----------");
        ArrayList<Double> coeficientes = new ArrayList<>();

        for (int i=grau; i>=0; i--) {
            System.out.printf("Digite o coeficiente de x^%d: ", i);
            coeficientes.add(in.nextDouble());
        }

        return coeficientes;
    }

    private static double[] metodoKojima(ArrayList<Double> coeficientes, int grau) {
        double quotasKojima[] = new double[grau];

        for(int i=1; i<=grau; i++) {
            quotasKojima[i-1] = Math.pow(Math.abs(coeficientes.get(i) / coeficientes.get(0)), (1/ (double) i));
        }

        System.out.print("\nCota de Kojima: ");
        printaQuotas(grau, quotasKojima);
        return quotasKojima;
    }

    private static double metodoCauchy(ArrayList<Double> coeficientes, int grau) {
        double novosValores[] = new double[coeficientes.size()];
        double soma=0, aux=0, dif=0;

        for (int i=1; i<coeficientes.size(); i++) {
            novosValores[i-1] = Math.abs(coeficientes.get(i) / coeficientes.get(0));
        }

        do {
            dif = soma;
            aux = 0;

            for (int i=0; i<grau; i++) {
                int expoente = grau-1-i;

                aux += novosValores[i] * Math.pow(soma, expoente);
            }
            
            aux = Math.pow(aux, (1 / (double) grau));
            soma = aux;

            dif = soma - dif;
        } while (dif >= 1e-6);

        System.out.printf("\nCota de Cauchy: %.2f\n", soma);
        return soma;
    }

    private static double[] metodoMaximoMinimo(ArrayList<Double> coeficientes, int grau) {
        double valores[] = new double[2];
        double maximo =  Double.NEGATIVE_INFINITY;
        
        for (int i=1; i<=grau; i++) {
            double valor = Math.abs(coeficientes.get(i));
            if (valor > maximo) 
                maximo = valor;
        }

        valores[0] = 1 + (maximo / Math.abs(coeficientes.get(0)));

        for (int i=0; i<grau; i++) {
            double valor = Math.abs(coeficientes.get(i));
            if (valor > maximo) 
                maximo = valor;
        }

        valores[1] = 1 / (1 + (maximo / Math.abs(coeficientes.get(grau))));

        System.out.printf("Máximo: %.2f\nMínimo: %.2f\n", valores[0], valores[1]);
        return valores;
    }

    private static double[] computeQuotas(double[] quotasKojima, double[] quotasCauchy, double[] quotasMaximo) {
        double bestQuotas[] = new double[2];

        return bestQuotas;
    }

    private static double metodoSecante(ArrayList<Double> coeficientes) {
        return 0;
    }

    private static double metodoFalsaPosicao(ArrayList<Double> coeficientes) {
        return 0;
    }

    private static double computeRoot(double bestQuotas[]) {
        double root = 0;

        return root;
    }

    private static void printaResultados(int grau, ArrayList<Double> coeficientes, double bestQuotas[], double root) {
        System.out.println("\n ---------- [ APRESENTAÇÃO DOS RESULTADOS ] ----------");

        System.out.println("Grau do polinomio: " + grau);
        
        System.out.print("Equação do polinomio: ");
        printaEquacao(grau, coeficientes);

        System.out.print("\nMelhores quotas: ");
        printaQuotas(grau, bestQuotas);

        System.out.println("\nRaiz: " +root);
    }

    private static void printaEquacao(int grau, ArrayList<Double> coeficientes) {
        for (int i = grau; i >= 0; i--) {
            double coeficiente = coeficientes.get(grau - i);
            if (coeficiente != 0) {
                if (coeficiente > 0 && i != grau) {
                    System.out.print(" + ");
                } else if (coeficiente < 0) {
                    System.out.print(" - ");
                    coeficiente = Math.abs(coeficiente);
                }

                if (coeficiente != 1 || i == 0) {
                    System.out.printf("%.2f", coeficiente);
                }

                if (i > 0) {
                    System.out.print("x");
                    if (i > 1) {
                        System.out.printf("^%d", i);
                    }
                }
            }
        }
    }

    private static void printaQuotas(int grau, double quotas[]) {
        for (int i=0; i<grau; i++) {
            if (quotas[i] == 0) continue;
            System.out.printf("[ %.2f ] ", quotas[i]);
        }
    }
}