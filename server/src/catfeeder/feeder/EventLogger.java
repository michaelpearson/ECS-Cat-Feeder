package catfeeder.feeder;

import catfeeder.model.LogEntry;

import java.sql.SQLException;

public interface EventLogger {
    LogEntry logEvent(LogEntry.EventType eventType) throws SQLException;
}
