package de.qaware.tools.sqbb.library.impl.connector.model;

import de.qaware.tools.sqbb.library.api.connector.AnalysisTasks;

import javax.annotation.Nullable;
import java.util.List;

// Result from api/ce/component?component=... call
public class AnalysisTasksDto {
    private List<TaskDto> queue;
    @Nullable
    private TaskDto current;

    public List<TaskDto> getQueue() {
        return queue;
    }

    @Nullable
    public TaskDto getCurrent() {
        return current;
    }

    public static class TaskDto {
        private String status;

        public String getStatus() {
            return status;
        }

        public AnalysisTasks.Task toDto() {
            return new AnalysisTasks.Task(AnalysisTasks.Status.fromSonar(getStatus()));
        }
    }
}
