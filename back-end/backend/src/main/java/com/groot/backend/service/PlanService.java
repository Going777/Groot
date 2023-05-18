package com.groot.backend.service;

import com.google.firebase.auth.FirebaseAuthException;

public interface PlanService {

    void deletePlan(Long planId);
    void alarmPlan() throws FirebaseAuthException;
    void changeDate();
}
