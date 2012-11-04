package kalpas.simple.helper;

import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Converter {

    private static Logger          logger           = Logger.getLogger(Converter.class);

    static final DateTimeFormatter formatterFull    = DateTimeFormat.forPattern("dd.MM.yyyy").withZoneUTC();
    static final DateTimeFormatter formatterPartial = DateTimeFormat.forPattern("dd.MM").withZoneUTC()
                                                            .withDefaultYear(1972);

    // TODO conversion should go here


}
