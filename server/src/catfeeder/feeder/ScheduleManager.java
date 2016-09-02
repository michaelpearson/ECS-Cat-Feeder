package catfeeder.feeder;

import catfeeder.model.CatFeeder;
import catfeeder.model.Schedule;
import catfeeder.model.ScheduledItem;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ScheduleManager implements Iterable<ScheduledItem> {

    private List<ScheduledItem> scheduledItems;
    private final Calendar calendar = Calendar.getInstance();

    public ScheduleManager(Dao<Schedule, Integer> scheduleDao, CatFeeder cf) throws SQLException {
        List<Schedule> allSchedules = scheduleDao.queryForEq("feeder_id", cf);

        //If the scheduled event is not recurring and has a start date after now
        List<Schedule> filteredSchedules = new ArrayList<>();
        filteredSchedules.addAll(allSchedules.stream().filter(s -> !s.isRecurring() && s.getStartDate().after(new Date())).collect(Collectors.toList()));

        //If any recurring event has no end date or has an end date in the future.
        filteredSchedules.addAll(allSchedules.stream().filter(s -> s.isRecurring() && (s.getEndDate() == null || s.getEndDate().after(new Date()))).collect(Collectors.toList()));

        calendar.setTime(new Date());
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        scheduledItems = filteredSchedules.stream().flatMap(s -> {
            s.populateDeliveries(month, year);
            return s.getDerivedDeliveries().stream();
        }).collect(Collectors.toList());

        scheduledItems.sort((s1, s2) -> s1.getDateTime().compareTo(s2.getDateTime()));
    }

    @Override
    public Iterator<ScheduledItem> iterator() {
        return scheduledItems.iterator();
    }
}
