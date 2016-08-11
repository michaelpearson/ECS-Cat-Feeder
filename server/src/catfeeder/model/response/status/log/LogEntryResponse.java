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

    public LogEntryResponse() {}

    public LogEntryResponse(List<LogEntry> logEntries) {
        this.logEntries = logEntries;
        this.success = true;
    }


}
