package catfeeder.model;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.IntType;

import java.sql.SQLException;
import java.util.Arrays;

public enum LearnStage {
    OFF(0),
    STAGE_ONE(1),
    STAGE_TWO(2),
    STAGE_THREE(3);

    private int stageId;

    LearnStage(int stageId) {
        this.stageId = stageId;
    }

    public int getStageId() {
        return stageId;
    }

    public static LearnStage getLearnStage(int selectedStage){
        return Arrays.stream(LearnStage.values()).filter(s -> s.stageId == selectedStage).findFirst().orElse(OFF);
    }

    public static class Persister extends IntType {
        private static final Persister singleton = new Persister();

        protected Persister() {
            super(SqlType.INTEGER, new Class<?>[] { LearnStage.class });
        }

        public static Persister getSingleton() {
            return singleton;
        }

        @Override
        public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
            return super.javaToSqlArg(fieldType, ((LearnStage)javaObject).getStageId());
        }

        @Override
        public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
            return super.sqlArgToJava(fieldType, LearnStage.getLearnStage((int)sqlArg), columnPos);
        }
    }
}
