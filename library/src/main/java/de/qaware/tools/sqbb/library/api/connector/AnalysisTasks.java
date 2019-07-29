package de.qaware.tools.sqbb.library.api.connector;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Analysis tasks for a project.
 */
public class AnalysisTasks {
    /**
     * Tasks in the queue waiting to be executed.
     */
    private final List<Task> queue;
    /**
     * Last executed task.
     */
    @Nullable
    private final Task lastFinished;

    /**
     * Ctor.
     *
     * @param queue        tasks in the queue waiting to be executed.
     * @param lastFinished last executed task.
     */
    public AnalysisTasks(List<Task> queue, @Nullable Task lastFinished) {
        this.queue = queue;
        this.lastFinished = lastFinished;
    }

    public List<Task> getQueue() {
        return queue;
    }

    @Nullable
    public Task getLastFinished() {
        return lastFinished;
    }

    /**
     * A single analysis task.
     */
    public static class Task {
        /**
         * Status of the task.
         */
        private final Status status;

        /**
         * Ctor.
         *
         * @param status status
         */
        public Task(Status status) {
            this.status = status;
        }

        public Status getStatus() {
            return status;
        }
    }

    /**
     * Status of a analysis task.
     */
    public enum Status {
        PENDING,
        FAILED,
        SUCCESS;

        /**
         * Converts the value from the SonarQube API to the enum value.
         *
         * @param value value from the SonarQube API
         * @return enum value
         */
        public static Status fromSonar(String value) {
            switch (value) {
                case "PENDING":
                case "IN_PROGRESS":
                    return PENDING;
                case "FAILED":
                    return FAILED;
                case "SUCCESS":
                    return SUCCESS;
                default:
                    throw new IllegalArgumentException("Unknown sonar status '" + value + "'");
            }
        }
    }
}
