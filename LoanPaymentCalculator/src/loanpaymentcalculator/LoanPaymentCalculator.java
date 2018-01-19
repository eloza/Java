package loanpaymentcalculator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class LoanPaymentCalculator {
    // Console app for calculating loan amortization
    // with the option to export the resulting table to a CSV file
    private static Scanner scanner = new Scanner(System.in);
    private static double monthlyRate;
    
    private static double calculate(double principal, int numberOfPayments){
        double amortization = 0.00;
        
        if(monthlyRate > 0){
            amortization = (principal * monthlyRate * Math.pow(1 + monthlyRate, (double)numberOfPayments)) /
                     (Math.pow(1 + monthlyRate, (double)numberOfPayments) - 1);
        } else {
            amortization = principal / numberOfPayments;
        }
        
        return amortization;
    }
    
    public static void printTable(double principal, int numberOfPayments){
        String dashes = "--------------------------------------------------------------------------------";
        Object[] tableHeaders = {"Payment #", "Amount Due", "Interest Due", "Principal Due", "Amount Left"};
        System.out.println("\n" + dashes + "\n\t\t\t\tLoan Payoff Table\n" + dashes);
        System.out.format("%5s %12s %18s %18s %16s \n", tableHeaders);
        System.out.println(dashes);
        double monthlyPayment = calculate(principal, numberOfPayments);
        double interestPayment = 0.0;
        double principalDue = 0.0;
        double principalRemaining = principal;
        Object[][] tableRows = new Object[numberOfPayments][];

        for(int i = 0; i < numberOfPayments; i++) {
            interestPayment = monthlyRate * principalRemaining;
            principalDue = monthlyPayment - interestPayment;
            principalRemaining = Math.abs(principalRemaining - principalDue);
            Object[] tableRow = {i + 1, monthlyPayment, interestPayment, principalDue, principalRemaining};
            tableRows[i] = tableRow;
            System.out.format("%5d%17.2f%19.2f%19.2f%17.2f\n", tableRow);
        }
        
        promptToSaveCsv(tableRows);
    }

    private static void promptToSaveCsv(Object[][] tableRows){
        // Ask user if they would like to save the table to CSV file
        System.out.print("\nSave the table as a CSV file? [Yes/No]: ");
        String response = scanner.next();
        
        if("yes".equalsIgnoreCase(response)){
            try {
                FileWriter fileWriter = new FileWriter("LoanPaymentTable.csv");
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                StringBuilder stringBuilder = new StringBuilder();

                for (Object[] row : tableRows) {
                    for (Object column : row) {
                        if(stringBuilder.length()!= 0){
                            stringBuilder.append(',');
                        }
                        stringBuilder.append(column);
                    }
                    stringBuilder.append("\n");
                    bufferedWriter.write(stringBuilder.toString());
                    stringBuilder.setLength(0);
                }

                bufferedWriter.close();
                System.out.println("\tTable saved as LoanPaymentTable.csv");
            } catch(IOException e){
                e.printStackTrace();
            }
        } else {
            // Exits the program after this satement
            System.out.println("\tTable not saved as a CSV file\n");
        }
    }  
    
    public static void main(String[] args) {
        String stars = "*******************************************";
        System.out.println(stars + "\n\tLoan Payment Calculator\n" + stars);
        System.out.print("Enter the loan amount: ");
        double principal = scanner.nextDouble();

        System.out.print("Enter the interest rate (APR format is 00.00): ");
        double interest = scanner.nextDouble();

        System.out.print("Enter the total number of payments: ");
        int numberOfPayments = scanner.nextInt();

        monthlyRate = (double) interest / 1200;
        
        double loanPayment = calculate(principal, numberOfPayments);
        
        System.out.printf(stars + "\n\tThe loan payment amount is %.2f\n", loanPayment);
        printTable(principal, numberOfPayments);
    }
}