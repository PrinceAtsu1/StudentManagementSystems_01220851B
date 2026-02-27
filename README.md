# Mymidsematsu — Student Management System (JavaFX + SQLite)

A beginner-friendly **Student Management System** desktop application built with:

- **Java** (JDK 21)
- **JavaFX** for the GUI
- **SQLite** for the database (via **JDBC**)
- **Maven** for dependency management and running tests/builds

This project follows a **layered architecture** so the UI stays clean and business rules stay in the service layer.

---

## Features

### Authentication
- **Register** a new account (email + password)
- **Login** with saved credentials
- After login, you are taken to the **Dashboard**

### Dashboard
Displays summary statistics:
- Total students
- Active students count
- Inactive students count
- Average GPA

Includes quick navigation cards to:
- Students
- Reports
- Import / Export
- Settings

### Student Record Management
- Add a new student
- View students in a table
- Update a student
- Delete a student (with confirmation)
- Search by **Student ID** or **Full Name**
- Filter by **Level**, **Programme**, **Status**
- Sort by **GPA** and by **Full Name**

### Reports & Analytics
- **Top performers**: Top 10 students by GPA (filter by programme + level)
- **At-risk students**: students below a GPA threshold (default **2.0**, configurable)
- **GPA distribution** summary
- **Programme summary**: total students per programme + average GPA

### Import / Export
- Export full student list to **CSV**
- Export top performers report to **CSV**
- Export at-risk report to **CSV**
- Import students from CSV with validation:
  - Invalid rows are **skipped**
  - Invalid rows are written to an **import error report** CSV
  - Duplicate Student IDs are rejected and reported
- All exported and error-report files are saved into a project folder named **`data/`**

---

## Data Validation Rules (UI + Service Layer)

Validation is enforced in both the **UI layer** and **service layer**.

- **Student ID**: required, 4–20 characters, letters & digits only, unique
- **Full name**: required, 2–60 characters, must not contain digits
- **Programme**: required
- **Level**: must be one of: 100, 200, 300, 400
- **GPA**: 0.0 to 4.0
- **Email**: must contain `@` and `.`
- **Phone number**: 10 digits only (string, leading 0 allowed)
- **Status**: Active / Inactive

If validation fails, a clear message is shown and nothing is saved.

---

## Project Structure (Layered Design)

Typical structure (your package names may match this):

```
src/main/java/com/template/
  Domain/                # Core models (Student, etc.)
  repository/            # Repository interfaces
    sqlite/              # SQLite implementations
  service/               # Business rules + validation + reports
  ui/controllers/        # JavaFX controllers (NO SQL here)
  util/                  # DB connection, navigation, logging, helpers

src/main/resources/
  *.fxml                 # JavaFX views
  style.css              # Teal theme + UI styling
```

Key rule: **Controllers do not contain SQL** and avoid heavy business logic.  
Controllers call **services only**.

---

## Database

- Database: **SQLite**
- Access: **JDBC**
- Student table uses:
  - `student_id` as **PRIMARY KEY**
  - `NOT NULL` constraints for required fields
  - `CHECK` constraints for GPA and Level
- Uses **PreparedStatements** (no SQL concatenation with user input)

The database file is stored in the project-controlled folder:

```
data/students.db
```

> If your DB filename is slightly different in your code (e.g., `app.db`), update this README accordingly.

---

## CSV Import Format

Your importer expects the student fields in this order:

1. Student ID
2. Full Name
3. Programme
4. Level
5. GPA
6. Email
7. Phone Number
8. Date Added (YYYY-MM-DD)
9. Status (Active/Inactive)

**Recommended headers (most common):**
```
student_id,full_name,programme,level,gpa,email,phone,date_added,status
```

Some importers also accept title headers like:
```
Student ID,Full Name,Programme,Level,GPA,Email,Phone Number,Date Added,Status
```

If import shows **Imported: 0**, check the saved `data/import_error_report_*.csv` for the reason.

---

## Setup (Windows)

### 1) Install required tools
- **JDK 21** (the project was built/tested with JDK 21)
- **JavaFX SDK 21** (download from Gluon/OpenJFX)
- **IntelliJ IDEA** (Community or Ultimate)
- **Maven** (IntelliJ includes it, but system Maven also works)

### 2) Open the project
Open the folder:
```
mymidsematsu
```

### 3) Configure JavaFX VM Options (required when running from IntelliJ)
Create a file named:

```
RUN_VM_OPTIONS.txt
```

Example content (edit to your JavaFX path):
```
JDK: 21

--module-path "C:\javafx\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml
```

In IntelliJ:
- Run → Edit Configurations…
- Add the VM options above to your run configuration.

---

## Run the App

### Option A: Run in IntelliJ
- Ensure VM options are configured
- Run the `Main` class

### Option B: Run with Maven (if configured)
From the project root:
```bash
mvn clean javafx:run
```

---

## Testing

This project includes unit tests for:
- validation logic
- report calculations
- repository/database methods

Run tests:
```bash
mvn test
```

> Store your Maven test output inside an `evidence/` folder as required:
```
evidence/test_output.txt
```

---

## Logging

Logs are written to a file inside the `data/` folder.
The app logs:
- app start and close
- add/update/delete operations
- import summary
- export completion
- database failures

For privacy: **do not log full personal records**.

---

## Troubleshooting

### “BorderPane is not a valid type” (FXML load error)
Make sure your FXML root includes the correct import OR uses a valid root tag:
- `<BorderPane ...>` is valid only if JavaFX is configured correctly and modules are loaded.
- Confirm your VM options include `javafx.controls, javafx.fxml`.

### Import shows “Imported: 0, Skipped: N”
- Check your CSV headers/order
- Check phone numbers (treat phone as a string, not a number — keep leading 0)
- Open the generated error report in:
  `data/import_error_report_*.csv`

### “UNIQUE constraint failed: students.student_id”
You imported/added a duplicate Student ID. Use a new unique ID.

---

## Author / Notes
Project name: **Mymidsematsu**  
Platform: **Windows**  
Theme: **Teal green** (styled in `style.css`)
 
ENJOY !!
---
