package tech.ikora.evolution.results;

import tech.ikora.analytics.Difference;
import tech.ikora.evolution.differences.DifferentiableMatcher;
import tech.ikora.evolution.differences.TimeLine;

import java.util.ArrayList;
import java.util.List;

public class TimeLineResults {
    private List<TimeLine> timeLines;
    private DifferentiableMatcher timeLineMatcher;
    private List<TimeLine> timeLineNotChanged;

    public TimeLineResults(){
        timeLines = new ArrayList<>();

        timeLineMatcher = null;
        timeLineNotChanged = null;
    }

    public void update(Difference difference){
        if(difference == null){
            return;
        }

        if(difference.isEmpty()){
            return;
        }

        if(difference.getLeft() != null){
            for(TimeLine timeLine : timeLines){
                if(timeLine.add(difference)){
                    return;
                }
            }
        }

        TimeLine timeLine = new TimeLine();
        timeLine.add(difference);

        timeLines.add(timeLine);
    }

    public List<TimeLine> getTimeLines() {
        return timeLines;
    }

    public DifferentiableMatcher getTimeLinesMatches() {
        if(timeLineMatcher == null){
            List<TimeLine> timeLineChanged = new ArrayList<>(timeLines);
            timeLineChanged.removeAll(getNotChanged());

            timeLineMatcher = DifferentiableMatcher.match(timeLineChanged, 0.8);
        }

        return timeLineMatcher;
    }

    public List<TimeLine> getNotChanged(){
        if(timeLineNotChanged == null){
            timeLineNotChanged = new ArrayList<>();

            for(TimeLine timeLine: timeLines){
                if(!timeLine.hasChanged()){
                    timeLineNotChanged.add(timeLine);
                }
            }
        }

        return timeLineNotChanged;
    }
}
