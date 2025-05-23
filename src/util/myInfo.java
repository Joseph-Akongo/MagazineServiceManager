/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: myInfo.java
 * Purpose: Display my info.
 */

package util;

// My Infomation
public class myInfo {

    public void printMyInfo() {
        System.out.println(getInfo());
    }

    public String getInfo() {
        return """
            ------------------------------------------------
            Student Information
            ------------------------------------------------
            Joseph Akongo
            Student Number: 33255426
            Mode of Enrolment: Full-time
            Tutor Name: A Z M Ehtesham Chowdhury
            Tutorial Attendance Day: Wednesday
            Tutorial Attendance Time: 12:30pm
            ------------------------------------------------
            """;
    }
}

