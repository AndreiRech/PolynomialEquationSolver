import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;

public class Polinomial {
    private static Scanner in = new Scanner(System.in);
    private static final double MINIMO = Double.POSITIVE_INFINITY;
    private static final double MAXIMO = Double.NEGATIVE_INFINITY;
    private static final double LIMITE = 1e-6;

    public static void main(String[] args) {
        double root;
        System.out.println("--------------- [ CALCULADOR DE POLINOMIOS ] ---------------");

        int grau = readGrau();
        ArrayList<Double> coeficientes = readCoeficientes(grau);

        double quotasKojima = metodoKojima(coeficientes, grau);
        double quotaCauchy = metodoCauchy(coeficientes, grau);
        double quotasMaximo[] = metodoMaximoMinimo(coeficientes, grau);
        double bestQuotas[] = computeQuotas(quotasKojima, quotaCauchy, quotasMaximo);

        if (verificaRaiz(coeficientes, bestQuotas, grau) == true) {
            root = metodoSecante(coeficientes, bestQuotas, grau);
        } else {
            root = metodoFalsaPosicao(coeficientes, bestQuotas, grau);
        }

        printaResultados(grau, coeficientes, bestQuotas, root);
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

    private static double metodoKojima(ArrayList<Double> coeficientes, int grau) {
        double quotasKojima[] = new double[grau];
        double soma;
    
        for(int i = 1; i <= grau; i++) {
            quotasKojima[i - 1] = Math.pow(Math.abs(coeficientes.get(i) / coeficientes.get(0)), (1.0 / (double) i));
        }
    
        Arrays.sort(quotasKojima);
        soma = quotasKojima[quotasKojima.length - 1] + quotasKojima[quotasKojima.length - 2];
    
        //System.out.printf("\nCota de Kojima: %.2f" ,soma);
        return soma;
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
        } while (dif >= LIMITE);

        //System.out.printf("\nCota de Cauchy: %.2f\n", soma);
        return soma;
    }

    private static double[] metodoMaximoMinimo(ArrayList<Double> coeficientes, int grau) {
        double valores[] = new double[2];
        double maximo =  MAXIMO;
        
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

        //System.out.printf("Cota Máxima: %.2f\nCota Mínima: %.2f\n", valores[0], valores[1]);
        return valores;
    }

    private static double[] computeQuotas(double quotasKojima, double quotasCauchy, double[] quotasMaximo) {
        double bestQuotas[] = new double[2];
        ArrayList<Double> cotas = new ArrayList<>();

        cotas.add(quotasCauchy);
        cotas.add(quotasKojima);
        adicionaQuotas(cotas, quotasMaximo);

        bestQuotas[0] = menorQuota(cotas);

        cotas.removeAll(Arrays.asList(menorQuota(cotas)));

        bestQuotas[1] = menorQuota(cotas);

        //System.out.printf("\nMelhores cotas: [%.2f | %.2f]", bestQuotas[0], bestQuotas[1]);
        return bestQuotas;
    }

    private static void adicionaQuotas(ArrayList<Double> cotas, double[] quotas) {
        for (double c : quotas) 
            cotas.add(c);
    }

    private static double menorQuota(ArrayList<Double> cotas) {
        double menor = MINIMO;

        for (double c : cotas) 
            if (c < menor) menor = c;
        return menor;
    }

    private static double metodoSecante(ArrayList<Double> coeficientes, double[] bestQuotas, int grau) {
        double res=0, dif=0;
        int count=0;
        double x0 = (bestQuotas[0] + bestQuotas[1]) / 2;
        double x1 = bestQuotas[0] + 0.01;
        double fx0 = fX(coeficientes, x0, grau);
        double fx1 = fX(coeficientes, x1, grau);

        do {
            dif = res;

            fx0 = fX(coeficientes, x0, grau);
            fx1 = fX(coeficientes, x1, grau);

            res = x1 - ((fx1 * (x1 - x0)) / (fx1 - fx0));

            x0 = x1;
            x1 = res;

            dif = res - dif;

            count++;
            if(count == 5000) break;
        } while (dif >= LIMITE);

        //System.out.printf("\n\nMétodo Secante: %.2f" ,res);
        return res;
    }

    private static double metodoFalsaPosicao(ArrayList<Double> coeficientes, double[] bestQuotas, int grau) {
        double res=0, dif=0;
        double x0 = bestQuotas[0];
        double x1 = bestQuotas[1];
        double fx0 = fX(coeficientes, x0, grau);
        double fx1 = fX(coeficientes, x1, grau);

        do {
            dif = res;

            fx0 = fX(coeficientes, x0, grau);
            fx1 = fX(coeficientes, x1, grau);

            res = ((x0 * fx1) - (x1 * fx0)) / (fx1 - fx0);

            x0 = x1;
            x1 = res;

            dif = res - dif;
        } while (dif >= LIMITE);

        //System.out.printf("\nMétodo Falsa-Posição: %.2f" ,res);
        return res;
    }

    private static double fX(ArrayList<Double> coeficientes, double valor, int grau) {
        double resultado = 0;
        int index = grau;

        for (double coef : coeficientes) {
            resultado += coef * Math.pow(valor, index);
            index--;
        }

        return resultado;
    }

    private static boolean verificaRaiz(ArrayList<Double> coeficientes, double[] bestQuotas, int grau) {
        double x0 = bestQuotas[0];
        double x1 = bestQuotas[1];
        double fx0 = fX(coeficientes, x0, grau);
        double fx1 = fX(coeficientes, x1, grau);

        if(fx0 * fx1 <= 0) {
            return false;
        } else {
            return true;
        }
    }

    private static void printaResultados(int grau, ArrayList<Double> coeficientes, double bestQuotas[], double root) {
        System.out.println("\n ---------- [ APRESENTAÇÃO DOS RESULTADOS ] ----------");

        System.out.println("Grau do polinomio: " + grau);
        
        System.out.print("Equação do polinomio: ");
        printaEquacao(grau, coeficientes);

        System.out.printf("\nMelhores quotas: [%.2f | %.2f]", bestQuotas[0], bestQuotas[1]);

        System.out.printf("\nRaiz: %.2f" ,root);
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
}
