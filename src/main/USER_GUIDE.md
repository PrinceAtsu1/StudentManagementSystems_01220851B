# USER GUIDE — Mymidsematsu Student Management System

This guide explains how to run and use the JavaFX + SQLite Student Management System on Windows.

---

## 1) Requirements

- JDK 21
- JavaFX SDK 21
- IntelliJ IDEA
- Maven (comes with IntelliJ)

---

## 2) Running the Project (IntelliJ)

### Step 1 — Open the Project
1. Open IntelliJ IDEA
2. Click Open
3. Select your project folder: mymidsematsu

### Step 2 — Add JavaFX VM Options
1. Open RUN_VM_OPTIONS.txt
2. Copy the VM options line
3. Go to Run → Edit Configurations
4. Select your Main run configuration
5. Paste into the VM options field
6. Click Apply → OK

Example:
--module-path "C:\javafx\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml

### Step 3 — Run
Click the green Run button on the Main class.

---

## 3) Login & Register

### Register
- Click Register
- Enter email and password
- Submit

### Login
- Enter registered email + password
- Click Login
- You will be redirected to the Dashboard

---

## 4) Dashboard

The dashboard displays:

- Total students
- Active students
- Inactive students
- Average GPA

You can navigate to:
- Students
- Reports
- Import / Export
- Settings

---

## 5) Students Page

Features:
- Add student
- Edit student
- Delete student
- Search by ID or name
- Filter by programme, level, status
- Sort by GPA or Name

Validation rules:
- Student ID: 4–20 letters/digits, unique
- Full name: 2–60 chars, no digits
- Programme: required
- Level: 100, 200, 300, 400
- GPA: 0.0–4.0
- Email: must contain @ and .
- Phone: 10 digits
- Status: Active/Inactive

---

## 6) Reports Page

Reports available:
- Top Performers (Top 10 GPA)
- At Risk Students (Below GPA threshold)
- GPA Distribution
- Programme Summary

---

## 7) Import / Export

### Export
Exports saved automatically in:
data/

- Export Full Student List
- Export Top Performers
- Export At Risk Report

### Import CSV
1. Drag & drop or select CSV
2. Click Import Student Records
3. View summary
4. If errors exist, open error report from data/

CSV format expected:
student_id,full_name,programme,level,gpa,email,phone,date_added,status

Example:
01234567,Akosua Mensah,Computer Science,200,3.8,akosua.mensah@mail.com,0241234567,2026-02-20,Active

---

## 8) Settings

- Change At-Risk GPA threshold
- Manage programme list

Changes update automatically in Reports and Students.

---

## 9) Database

- SQLite database
- Located in data/ folder
- Student ID is PRIMARY KEY
- Uses PreparedStatements

---

## 10) Troubleshooting

If Import shows 0 imported:
- Check CSV headers
- Check phone format
- Check import error report in data/

If UNIQUE constraint error appears:
- Duplicate Student ID exists

---

END OF USER GUIDE
