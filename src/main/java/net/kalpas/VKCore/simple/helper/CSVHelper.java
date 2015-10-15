package net.kalpas.VKCore.simple.helper;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Joiner;

public class CSVHelper implements Closeable {

    private BufferedWriter file;
    private int            columns       = 0;
    private boolean        isHeaderAdded = false;

    private Logger         logger        = LogManager.getLogger(CSVHelper.class);

    public CSVHelper(String fileName) throws IOException {
        file = new BufferedWriter(new FileWriter(new File(fileName + ".csv"), true));
    }

    public void writeHeader(String... header) throws IOException {
        file.write(Joiner.on("\t").join(header) + "\n");
        isHeaderAdded = true;
        columns = header.length;
    }

	public void writeHeader(List<String> header) throws IOException {
		file.write(Joiner.on("\t").join(header) + "\n");
		isHeaderAdded = true;
		columns = header.size();
	}

    public void writeRow(String... values) throws IOException {
        if (!isHeaderAdded) {
            logger.warn("header wasn't added");
        }
        if (values.length != columns) {
            logger.warn("header has {} columns, while {} values were given", columns, values.length);
        }
        file.write(Joiner.on("\t").join(values) + "\n");

    }

	public void writeRow(List<String> values) throws IOException {
		if (!isHeaderAdded) {
			logger.warn("header wasn't added");
		}
		if (values.size() != columns) {
			logger.warn("header has {} columns, while {} values were given", columns, values.size());
		}
		file.write(Joiner.on("\t").join(values) + "\n");

	}

    @Override
    public void close() throws IOException {
        file.flush();
        file.close();
    }

}
