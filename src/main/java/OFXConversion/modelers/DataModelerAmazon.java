package OFXConversion.modelers;

import OFXConversion.data.TransactionList;
import OFXConversion.data.Transactions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DataModelerAmazon {


    private List<String> transactionTokenList = new ArrayList<>();
    private List<String> transactionList = new ArrayList<>();
    private Double finalBalance = 0.0;


    public TransactionList createTransactionList(String sourceFileName, Double initialBalance) throws IOException {

        TransactionList translistFinal = new TransactionList();
        try (BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName))) {
            DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);


            translistFinal.setInitialBalance(initialBalance);
            String lineOfStatement;
            Boolean isHeader = true;

            //initialise final balance.
            finalBalance = initialBalance;

            while ((lineOfStatement = inputStream.readLine()) != null) {

                // first line is the header so ignore it
                if (!isHeader) {
                    // Amazon sometimes seem to insert " double quotes before string hence need to remove them all
                    String cleanLineOfStatement = lineOfStatement.replace("\"","");

                    String tokens[] = cleanLineOfStatement.split(",");
                    // we know the tokens are
                    // Date	Description	Amount(GBP)
                    if (tokens.length > 1) {
                        Transactions trans = new Transactions();

                        trans.setTransactionDate(LocalDate.parse(tokens[0], myformatter));
                        // TODO - Purge transaction data as follows, currently a manual process
                        // remove commas and Fin: /Auth:  words (note space after :)
                        // open in npp - remove £ symbol

                        trans.setTransactionDetails(tokens[1]);
                        trans.setTransactionAmount(Double.parseDouble(tokens[2]));

                        translistFinal.getTransactionsList().add(trans);
                        finalBalance = finalBalance + trans.getTransactionAmount();
                    }
                }

                if (isHeader)
                    isHeader = false;
            }
            translistFinal.setFinalBalance(finalBalance);

            inputStream.close();
        }
        return translistFinal;
    }
}
