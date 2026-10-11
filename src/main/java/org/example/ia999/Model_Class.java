package org.example.ia999;
import java.time.LocalDate;

public class Model_Class {
    private Integer id;
    private String task_text;
    private LocalDate task_date;
    private boolean completed;

    public Model_Class(Integer id, String task_text, LocalDate task_date, boolean completed) {
        this.id = id;
        this.task_text = task_text;
        this.task_date = task_date;
        this.completed = completed;
    }
    public Integer getId() {
        return id;
    }
    public String getTask_text() {
        return task_text;
    }
    public LocalDate getTask_date() {
        return task_date;
    }
    public boolean isCompleted() {
        return completed;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public void setTask_text(String task_text) {
        this.task_text = task_text;
    }
    public void setTask_date(LocalDate task_date) {
        this.task_date = task_date;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}



