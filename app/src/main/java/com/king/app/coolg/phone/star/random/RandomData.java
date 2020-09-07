package com.king.app.coolg.phone.star.random;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/9/7 14:53
 */
public class RandomData {

    private String name;

    private List<Long> candidateList;

    private List<Long> markedList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getCandidateList() {
        return candidateList;
    }

    public void setCandidateList(List<Long> candidateList) {
        this.candidateList = candidateList;
    }

    public List<Long> getMarkedList() {
        return markedList;
    }

    public void setMarkedList(List<Long> markedList) {
        this.markedList = markedList;
    }
}
