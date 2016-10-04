package catfeeder.model;

/**
 * Created by Rob on 3/10/2016.
 */
public enum LearnStage {
    STAGE_ONE,
    STAGE_TWO,
    STAGE_THREE,
    OFF;

    public static LearnStage getLearnStage(String stageString){
        switch(stageString){
            case "stage_one":
                return STAGE_ONE;
            case "stage_two":
                return STAGE_TWO;
            case "stage_three":
                return STAGE_THREE;
            case "off":
                return OFF;
            default:
                return OFF;
        }
    }
}
