package catfeeder.model.response.schedule;

import catfeeder.model.Schedule;
import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class ScheduleResponse extends GeneralResponse {
    @XmlElement
    private Schedule schedule;

    public ScheduleResponse() {}

    public ScheduleResponse(Schedule schedule) {
        this.schedule = schedule;
        this.success = true;
    }
}
