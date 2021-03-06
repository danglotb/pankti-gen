package se.kth.castor.panktigen.parsers;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

public class CSVFileParser {
    static final String csvParentFQNField = "parent-FQN";
    static final String csvMethodNameField = "method-name";
    static final String csvParamListField = "param-list";
    static final String csvReturnTypeField = "return-type";

    public static List<InstrumentedMethod> parseCSVFile(String filePath) {
        List<InstrumentedMethod> instrumentedMethods = new ArrayList<>();
        try {
            Reader in = new FileReader(filePath);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            for (CSVRecord record : records) {
                String parentFQN = record.get(csvParentFQNField);
                String methodName = record.get(csvMethodNameField);
                String params = record.get(csvParamListField);
                String returnType = record.get(csvReturnTypeField);

                params = params.replaceAll("\\s", "");

                List<String> paramList = new ArrayList<>();
                if (!params.isEmpty()) {
                    paramList = new ArrayList<>(Arrays.asList(params.split(",")));
                }

                instrumentedMethods.add(new InstrumentedMethod(parentFQN, methodName, paramList, returnType));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(instrumentedMethods);
        return instrumentedMethods;
    }
}
