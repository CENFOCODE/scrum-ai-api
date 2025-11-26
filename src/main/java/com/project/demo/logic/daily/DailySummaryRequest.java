package com.project.demo.logic.daily;

import java.util.List;

public class DailySummaryRequest {

    private Long simulationId;
    private Answers answers;
    private Board board;
    private String aiSummary;

    public DailySummaryRequest() {
    }


    public DailySummaryRequest(String aiSummary) {
        this.aiSummary = aiSummary;
    }
    public Long getSimulationId(){
        return simulationId;
    }

    public void setSimulationId(Long simulationId) {
        this.simulationId = simulationId;
    }

    public String getAiSummary() { return aiSummary; }

    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public Answers getAnswers() {
        return answers;
    }

    public void setAnswers(Answers answers) {
        this.answers = answers;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public static class Answers {
        private String yesterday;
        private String today;
        private String impediments;

        public String getYesterday() {
            return yesterday;
        }

        public void setYesterday(String yesterday) {
            this.yesterday = yesterday;
        }

        public String getToday() {
            return today;
        }

        public void setToday(String today) {
            this.today = today;
        }

        public String getImpediments() {
            return impediments;
        }

        public void setImpediments(String impediments) {
            this.impediments = impediments;
        }
    }

    public static class Board {
        private List<Task> todo;
        private List<Task> inProgress;
        private List<Task> qa;
        private List<Task> done;

        public List<Task> getTodo() {
            return todo;
        }

        public void setTodo(List<Task> todo) {
            this.todo = todo;
        }

        public List<Task> getInProgress() {
            return inProgress;
        }

        public void setInProgress(List<Task> inProgress) {
            this.inProgress = inProgress;
        }

        public List<Task> getQa() {
            return qa;
        }

        public void setQa(List<Task> qa) {
            this.qa = qa;
        }

        public List<Task> getDone() {
            return done;
        }

        public void setDone(List<Task> done) {
            this.done = done;
        }
    }

}
