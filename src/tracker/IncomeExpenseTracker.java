package tracker;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class IncomeExpenseTracker {

    public static void showMonthlySummary(ArrayList<Transaction> transactions) {
        int totalIncome = 0;
        int totalExpense = 0;

        System.out.println("\n--- Monthly Summary ---");

        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        for (Transaction tran : transactions) {
            if (tran.dt.getMonthValue() == currentMonth && tran.dt.getYear() == currentYear) {
                if (tran.isIncome) {
                    totalIncome += tran.Amount;
                } else {
                    totalExpense += tran.Amount;
                }
            }
        }

        int balance = totalIncome - totalExpense;

        System.out.println("Total Income: Rs." + totalIncome);
        System.out.println("Total Expense: Rs." + totalExpense);
        System.out.println("Net Balance: Rs." + balance);
    }

    public static void selectTransaction(ArrayList<Transaction> transactions) {
        System.out.println("\n------------------");
        System.out.println("* All Transaction *");
        System.out.println("------------------");
        for (Transaction tran : transactions) {
            String type = tran.isIncome ? "Income" : "Expense";
            System.out.println("Amount : " + tran.Amount + "\nCategory : " + tran.Category + "\nDate : " + tran.dt + "\nType :" + type);
            System.out.println("------------------");
        }
        System.out.println("--------END-------");
    }

    public static String toJSON(Transaction t) {
        return "{"
                + "\"Amount\":" + t.Amount + ","
                + "\"Category\":\"" + t.Category + "\","
                + "\"Date\":\"" + t.dt.toString() + "\","
                + "\"IsIncome\":" + t.isIncome
                + "}";
    }

    public static Transaction fromJSON(String json) {
        json = json.trim().substring(1, json.length() - 1); // remove { and }
        String[] fields = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        int amount = 0;
        String category = "";
        LocalDate date = null;
        boolean isIncome = false;

        for (String field : fields) {
            String[] keyValue = field.split(":", 2);
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");

            switch (key) {
                case "Amount":
                    amount = Integer.parseInt(value);
                    break;
                case "Category":
                    category = value;
                    break;
                case "Date":
                    date = LocalDate.parse(value);
                    break;
                case "IsIncome":
                    isIncome = Boolean.parseBoolean(value);
                    break;
            }
        }

        return new Transaction(amount, category, date, isIncome);
    }

    public static String encodeToJson(ArrayList<Transaction> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(toJSON(list.get(i)));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static ArrayList<Transaction> decodeFromJson(String json) {
        ArrayList<Transaction> list = new ArrayList<>();
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);

        String[] items = json.split("(?<=\\}),\\s*(?=\\{)");
        for (String item : items) {
            if (!item.trim().startsWith("{")) item = "{" + item;
            if (!item.trim().endsWith("}")) item = item + "}";
            list.add(fromJSON(item.trim()));
        }
        return list;
    }

    public static void saveToFile(String fileName, ArrayList<Transaction> transactions) {
        String str = "";
    	try {
            File file = new File(fileName);
            FileWriter writer = new FileWriter(file);
            String json = encodeToJson(transactions);
            writer.write(json);
            writer.close();
            str = file.getAbsolutePath();
            System.out.println("Saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    	
    }

    public static ArrayList<Transaction> loadFromFile(String fileName) {
        ArrayList<Transaction> list = new ArrayList<>();
        try {
            File file = new File(fileName);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();
            list = decodeFromJson(json.toString());
            System.out.println("Loaded " + list.size() + " transactions from " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return list;
    }

    public static void main(String[] args) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        int input = 0;

        do {
            System.out.println("\n1. Add Transaction");
            System.out.println("2. View Monthly Summary");
            System.out.println("3. View All Transactions");
            System.out.println("4. Save to File");
            System.out.println("5. Load from File");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            input = sc.nextInt();

            switch (input) {
                case 1:
                    System.out.println("Enter Amount:");
                    int Amount = sc.nextInt();

                    System.out.println("Enter Category:");
                    String Category = sc.next();

                    System.out.println("Is Transaction Income? (yes / no):");
                    String isIncomeStr = sc.next();
                    boolean isIncome = isIncomeStr.equalsIgnoreCase("yes");

                    System.out.println("Enter Date (yyyy-mm-dd):");
                    String dtStr = sc.next();

                    LocalDate dt = LocalDate.parse(dtStr);

                    Transaction t = new Transaction(Amount, Category, dt, isIncome);
                    transactions.add(t);
                    System.out.println("Transaction Saved.");
                    break;

                case 2:
                    showMonthlySummary(transactions);
                    break;

                case 3:
                    selectTransaction(transactions);
                    break;

                case 4:
                    System.out.println("Enter file name to save (e.g., data.json):");
                    String saveFile = sc.next();
                    saveToFile(saveFile, transactions);
                    break;

                case 5:
                    System.out.println("Enter file name to load (e.g., data.json):");
                    String loadFile = sc.next();
                    transactions = loadFromFile(loadFile);
                    break;

                case 6:
                    System.out.println("Exiting... Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        } while (input != 6);
    }
}
