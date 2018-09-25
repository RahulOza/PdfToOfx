package pdftoofx;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.sf.ofx4j.io.v1.OFXV1Writer;


class OfxGen {

    Map<String, String> ofxHeader = new HashMap< >();

    private static final String amazonAccountId = "44000000";
    private static final int nameLimit = 32;


   void ofxFileWriteAmazon (TransactionList transactionList, String fileName){

       String ofxExtn=".ofx";
       String fileSuffix = new SimpleDateFormat("_dd_MM_yyyy_hh_mm_ss").format(new Date());
       String filePath = "C:/Users/ozara/IdeaProjects/OFXs/";
       DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("yyyyMMdd110000.000", Locale.ENGLISH);
       Integer fitid = 0001;
       String fitIdPart = new SimpleDateFormat("ddMMyyyyhhmmssS").format(new Date());
       String fitIdPref = "R";

      // ofxFileName = filePath + ofxFileName + fileSuffix + ofxExtn;
       String ofxFileName = fileName.substring(0,fileName.length()-4) + ofxExtn;

      // Create file name
        try {
            OFXV1Writer ofxv1Writer = new OFXV1Writer(new PrintWriter(ofxFileName));

            ofxv1Writer.writeHeaders(ofxHeader);

            ofxv1Writer.setWriteAttributesOnNewLine(true);

            ofxv1Writer.writeStartAggregate("OFX");
            ofxv1Writer.writeStartAggregate("SIGNONMSGSRSV1");
            ofxv1Writer.writeStartAggregate("SONRS");
            ofxv1Writer.writeStartAggregate("STATUS");

            ofxv1Writer.writeElement("CODE","0");
            ofxv1Writer.writeElement("SEVERITY","INFO");
            ofxv1Writer.writeElement("MESSAGE","OK");

            ofxv1Writer.writeEndAggregate("STATUS");

            ofxv1Writer.writeElement("DTSERVER",transactionList.transactionsListFinal.get(0).transactionDate.format( myformatter) + "[0]");
            ofxv1Writer.writeElement("LANGUAGE","ENG");

            ofxv1Writer.writeEndAggregate("SONRS");
            ofxv1Writer.writeEndAggregate("SIGNONMSGSRSV1");

            ofxv1Writer.writeStartAggregate("CREDITCARDMSGSRSV1");
            ofxv1Writer.writeStartAggregate("CCSTMTTRNRS");

            ofxv1Writer.writeElement("TRNUID","0");

            ofxv1Writer.writeStartAggregate("STATUS");

            ofxv1Writer.writeElement("CODE","0");
            ofxv1Writer.writeElement("SEVERITY","INFO");
            ofxv1Writer.writeElement("MESSAGE","OK");

            ofxv1Writer.writeEndAggregate("STATUS");
            ofxv1Writer.writeStartAggregate("CCSTMTRS");

            ofxv1Writer.writeElement("CURDEF","GBP");

            ofxv1Writer.writeStartAggregate("CCACCTFROM");
            ofxv1Writer.writeElement("ACCTID",amazonAccountId);
            ofxv1Writer.writeEndAggregate("CCACCTFROM");

            // Bank transactions
            ofxv1Writer.writeStartAggregate("BANKTRANLIST");

            ofxv1Writer.writeElement("DTSTART",transactionList.transactionsListFinal.get(0).transactionDate.format(myformatter) + "[0]");
            ofxv1Writer.writeElement("DTEND",transactionList.transactionsListFinal.get(transactionList.transactionsListFinal.size()-1).transactionDate.format(myformatter) + "[0]");

            for (Transactions t: transactionList.transactionsListFinal ) {
                ofxv1Writer.writeStartAggregate("STMTTRN");
                if(t.transactionAmount > 0) {
                    ofxv1Writer.writeElement("TRNTYPE", "DEBIT");
                }
                else{
                    ofxv1Writer.writeElement("TRNTYPE", "CREDIT");
                }
                ofxv1Writer.writeElement("DTPOSTED",t.transactionDate.format(myformatter) + "[0]");
                t.transactionAmount = -(t.transactionAmount);
                ofxv1Writer.writeElement("TRNAMT",t.transactionAmount.toString());
                ofxv1Writer.writeElement("FITID",fitIdPref + fitIdPart + fitid++);
                ofxv1Writer.writeElement("NAME",t.transactionDetails.substring(0,Math.min(t.transactionDetails.length(),nameLimit)));
                ofxv1Writer.writeElement("MEMO",t.transactionDetails);


                ofxv1Writer.writeEndAggregate("STMTTRN");
            }

            ofxv1Writer.writeEndAggregate("BANKTRANLIST");
            ofxv1Writer.writeStartAggregate("LEDGERBAL");

            ofxv1Writer.writeElement("BALAMT", transactionList.finalBalance.toString());
            ofxv1Writer.writeElement("DTASOF",transactionList.transactionsListFinal.get(transactionList.transactionsListFinal.size()-1).transactionDate.format(myformatter) + "[0]");

            ofxv1Writer.writeEndAggregate("LEDGERBAL");
            ofxv1Writer.writeEndAggregate("CCSTMTRS");
            ofxv1Writer.writeEndAggregate("CCSTMTTRNRS");
            ofxv1Writer.writeEndAggregate("CREDITCARDMSGSRSV1");

            ofxv1Writer.writeEndAggregate("OFX");

            ofxv1Writer.close();
        }
        catch(Exception e){
            e.printStackTrace();

        }

    }
}