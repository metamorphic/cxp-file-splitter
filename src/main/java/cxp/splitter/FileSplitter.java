package cxp.splitter;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

/**
 * @author akhtet
 */
public class FileSplitter {

    private ObjectMapper mapper;

    public FileSplitter() {
        this.mapper = new ObjectMapper();
    }

    public List<String> process(String dataIn) throws Exception {
        File f = new File(dataIn);
        Map<String, Object> record = new HashMap<String, Object>();
        record.put("filename", f.getName());
        BufferedReader br = new BufferedReader(new FileReader(dataIn));
        String line;
        List<String> dataOut = new ArrayList<String>();
        while ((line = br.readLine()) != null) {
            record.put("data", line);
            dataOut.add(mapper.writeValueAsString(record));
        }
        return dataOut;
    }
}
