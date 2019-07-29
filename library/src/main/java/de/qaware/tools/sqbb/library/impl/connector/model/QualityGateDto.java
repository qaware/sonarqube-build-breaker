package de.qaware.tools.sqbb.library.impl.connector.model;

// Result from api/qualitygates/project_status?projectKey=... call
public class QualityGateDto {
    private ProjectStatusDto projectStatus;

    public ProjectStatusDto getProjectStatus() {
        return projectStatus;
    }

    public static class ProjectStatusDto {
        private String status;

        public String getStatus() {
            return status;
        }
    }
}
