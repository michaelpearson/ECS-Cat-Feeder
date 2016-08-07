package catfeeder.model.response.schedule;

import catfeeder.model.Schedule;
import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collection;
import java.util.List;

public class ScheduleListResponse extends GeneralResponse {
    @XmlElement
    Collection<Schedule> schedules;

    public ScheduleListResponse() {}

    public ScheduleListResponse(List<Schedule> schedules) {
        this.schedules = schedules;
        this.success = true;
    }
}
