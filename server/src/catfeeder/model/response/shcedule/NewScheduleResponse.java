package catfeeder.model.response.shcedule;

import catfeeder.model.Schedule;
import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
public class NewScheduleResponse extends GeneralResponse {
    private Schedule newSchedule;

    public NewScheduleResponse() {}

    public NewScheduleResponse(Schedule newSchedule) {
        this.newSchedule = newSchedule;
        this.success = true;
    }
}
