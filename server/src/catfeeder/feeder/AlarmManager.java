package catfeeder.feeder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class AlarmManager implements Runnable {

    private static final List<Alarm> alarms = new ArrayList<>();
    private static final AlarmManager singleton = new AlarmManager();
    private final Thread alarmThread;

    private AlarmManager() {
        alarmThread = new Thread(this);
        alarmThread.start();
    }

    private Alarm getNextAlarm() {
        return alarms.stream().min((a1, a2) -> a1.getDate().compareTo(a2.getDate())).orElse(null);
    }

    @FunctionalInterface
    interface Callback {
        void apply();
    }

    private static class Alarm {
        private final Date date;
        private final Callback callback;

        Alarm(Date date, Callback callback) {
            this.date = date;
            this.callback = callback;
        }

        public Date getDate() {
            return date;
        }

        private Callback getCallback() {
            return callback;
        }

        @Override
        public String toString() {
            return "Alarm: " + date.toString();
        }
    }

    @Override
    public void run() {
        while (!alarmThread.isInterrupted()) {
            try {
                Alarm nextAlarm = getNextAlarm();
                if(nextAlarm == null) {
                    synchronized (alarmThread) {
                        alarmThread.wait();
                    }
                    continue;
                }
                long milliseconds = nextAlarm.getDate().getTime() - System.currentTimeMillis();
                if(milliseconds > 0) {
                    synchronized (alarmThread) {
                        alarmThread.wait(milliseconds);
                    }
                }
                milliseconds = nextAlarm.getDate().getTime() - System.currentTimeMillis();
                if(milliseconds < 0) {
                    alarms.remove(nextAlarm);
                    try {
                        nextAlarm.getCallback().apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch(InterruptedException ignore) {}
        }
    }

    static void registerAlarm(Date date, Callback callback) {
        alarms.add(new Alarm(date, callback));
        synchronized (singleton.alarmThread) {
            singleton.alarmThread.notify();
        }
    }
}
