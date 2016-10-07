package catfeeder.model.response.status.log;

import catfeeder.model.LogEntry;
import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
@XmlAccessorType(XmlAccessType.NONE)
public class LogEntryResponse extends GeneralResponse {

    @XmlElement
    private List<LogEntry> logEntries;

    @XmlElement
    private int totalSize;

    @XmlElement
    private int offset;

    public LogEntryResponse() {}

    public LogEntryResponse(List<LogEntry> logEntries, int totalSize, int offset) {
        this.logEntries = logEntries;
        this.totalSize = totalSize;
        this.offset = offset;
        this.success = true;
    }


}
