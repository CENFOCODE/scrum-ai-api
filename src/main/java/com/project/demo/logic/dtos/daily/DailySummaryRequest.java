package com.project.demo.logic.dtos.daily;

import java.util.List;

public class DailySummaryRequest {

    private Answers answers;
    private Board board;

    // GETTERS & SETTERS
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


    // -------------------------
    // SUBCLASE: Answers
    // -------------------------
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


    // -------------------------
    // SUBCLASE: Board
    // -------------------------
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
