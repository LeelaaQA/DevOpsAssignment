# StarAgile DevOps Assignment: Amazon Automation on Selenium Grid (Docker + Jenkins)

## Objective
Automate the public Amazon website with Selenium WebDriver, run the **same tests on Chrome, Firefox and Edge** through a **Selenium Grid started with Docker**, push the code to **GitHub**, and **trigger a Jenkins job** that runs the tests and prints the result.

> Official requirement: *"Write Automation script for Amazon application using Docker, test it on three different browsers like Chrome, Firefox and Edge with Selenium Grid setup and push your code on GitHub and trigger job in Jenkins to print the result."*

## Technologies
Java 21, Maven, Selenium WebDriver 4.48.0, TestNG 7.11.0, Maven Surefire 3.5.5, Docker Compose, Selenium Grid 4 (images 4.48.0), Git/GitHub, Jenkins (Windows).

## Project structure
```
DevOpsAssignment/
├── pom.xml                      dependencies + Surefire/TestNG settings
├── testng.xml                   suite for ONE browser (default chrome)
├── testng-all-browsers.xml      suite for Chrome + Firefox + Edge in one run
├── Jenkinsfile                  pipeline (Windows, uses bat)
├── docker/
│   ├── docker-compose.yml       Hub + Chrome + Firefox + Edge nodes
│   └── wait-for-grid.ps1        waits until the Grid is ready
├── src/main/java/pages/         BasePage, HomePage, SearchResultsPage (Page Object Model)
├── src/main/java/utils/         DriverFactory, ConfigReader
├── src/main/resources/config.properties
├── src/test/java/tests/         BaseTest, AmazonHomePageTest, AmazonSearchTest
└── docs/                        research paper, execution guide, evidence/
```

## Prerequisites (Windows 11)
Java 21, Maven 3.9+, Git, Docker Desktop (running), internet access. Check in **Command Prompt**:
```
java -version
mvn -version
git --version
docker --version
docker compose version
```

## 1. Start the Docker Selenium Grid
Run from the project root (the folder that contains `pom.xml`):
```
docker compose -f docker/docker-compose.yml up -d
```
The first run downloads four images (a few GB); later runs are fast.

## 2. Verify the Grid
```
docker compose -f docker/docker-compose.yml ps
curl http://localhost:4444/status
```
Open **http://localhost:4444/ui** and confirm three nodes: Chrome, Firefox, Edge. Wait 20-40 seconds after start if nodes are missing.
Optional live view of a browser while tests run: Chrome http://localhost:7900, Firefox http://localhost:7901, Edge http://localhost:7902 (no password).

## 3. Run the tests
| Goal | Command |
|---|---|
| Chrome (default) | `mvn clean test -Dbrowser=chrome` |
| Firefox | `mvn clean test -Dbrowser=firefox` |
| Edge | `mvn clean test -Dbrowser=edge` |
| **All three, one after another** | `mvn clean test -DsuiteXmlFile=testng-all-browsers.xml` |
| Another Amazon site | add `-DbaseUrl=https://www.amazon.com` |
| Local browser, no Grid (debug only) | `mvn clean test -Dexecution=local -Dbrowser=chrome` |

Do not combine `-Dbrowser` with `testng-all-browsers.xml` (it would override all three).

## 4. Test report
After a run open (Command Prompt):
```
start target\surefire-reports\index.html
start target\surefire-reports\emailable-report.html
```
Failure screenshots (if any) are in `target\screenshots\`.

## 5. Stop the Grid
```
docker compose -f docker/docker-compose.yml down
```

## How Chrome, Firefox and Edge run through the Grid
1. The test asks for a browser (`-Dbrowser=...` or the TestNG `browser` parameter).
2. `DriverFactory` builds `ChromeOptions`, `FirefoxOptions` or `EdgeOptions` and creates a `RemoteWebDriver` pointing to `http://localhost:4444`.
3. The **Hub** (container `selenium-hub`) receives the request and compares it with what each node offers.
4. It forwards the session to the matching **node container** (`chrome`, `firefox` or `edge`), which opens the real browser inside Docker.
5. Selenium commands travel Test -> Hub -> Node -> Browser; results travel back. Each node runs 1 session at a time, so the three browsers run one after another in `testng-all-browsers.xml`.

## How Jenkins triggers and runs the project
1. **Trigger:** you click *Build with Parameters* (manual) **or** Jenkins notices a new commit on GitHub (the Jenkinsfile polls every ~5 minutes with `pollSCM`).
2. Jenkins reads the `Jenkinsfile` from the GitHub repository and runs the stages:
   `Checkout` -> `Verify Tools` -> `Start Selenium Grid` (`docker compose up -d` + wait script) -> `Run Tests` (Maven/TestNG) -> `Publish TestNG Results` (JUnit-format results + archived reports + summary printed in the console).
3. The `post { always }` block runs `docker compose down`, so the Grid stops even if tests fail.
4. The console ends with the real result (`SUCCESS` or `FAILURE`).

### One-time Jenkins setup (on your Windows machine)
1. Install/start Jenkins (http://localhost:8080) with the suggested plugins (Git, Pipeline).
2. **New Item** -> name `amazon-selenium-grid` -> **Pipeline** -> OK.
3. Pipeline -> Definition: **Pipeline script from SCM** -> SCM: **Git** -> Repository URL: your GitHub repo URL -> Branch: `*/main` -> Script Path: `Jenkinsfile` -> Save.
4. Click **Build with Parameters** -> choose `all` -> **Build**. Open **Console Output**.
5. Jenkins must run as a Windows user that can use Docker Desktop and has Java, Maven and Git on PATH (see Troubleshooting).

## Push the code to GitHub
Create an empty repository on github.com (no README), then in Command Prompt from the project root:
```
git init
git add .
git commit -m "DevOps assignment: Amazon Selenium Grid automation"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repo>.git
git push -u origin main
```
When asked to sign in, use the browser sign-in of Git Credential Manager (or a Personal Access Token). Never save a token inside the project files.

## Expected evidence (screenshots)
See `docs/evidence/README.md` for the exact list and file names. Put your real screenshots there after you run everything.

## Amazon limitations (honest note)
Amazon may show a CAPTCHA or "robot check" to automated browsers. This project does **not** try to bypass it. If it appears, the affected tests **fail with a clear message** and a screenshot is saved in `target\screenshots\`. Results can differ by network, region and time. Never write in the report that tests passed unless they actually did. Page layouts also change; locators are kept simple (see `pages/`) and are the first thing to adjust if Amazon changes its HTML.

## Troubleshooting
| Problem | Fix |
|---|---|
| `Cannot connect to the Docker daemon` | Start Docker Desktop and wait until it says "running" |
| Port 4444 already in use | Stop the other program, or run `docker compose -f docker/docker-compose.yml down` |
| Nodes missing in the Grid UI | Wait 30 s, then `docker compose -f docker/docker-compose.yml logs edge` |
| Image tag cannot be pulled | In `docker-compose.yml` replace `4.48.0-20260905` with `4.48.0` (4 places) |
| `Could not start a '<browser>' session` | Grid not running or still starting; check http://localhost:4444/ui |
| Edge node will not start | The Edge image supports amd64 (normal Windows PC), not ARM |
| `mvn` / `java` not recognized in Jenkins | Add them to the **system** PATH and restart the Jenkins service/process |
| Jenkins cannot reach Docker | Run Jenkins as your own Windows user (not LocalSystem), with Docker Desktop running |
| Test fails with "robot check (CAPTCHA)" | Amazon blocked automation; document it, keep the screenshot, try again later or another network |

## Conclusion
The project shows the full DevOps loop: code in Git/GitHub, an automated Jenkins pipeline, infrastructure as code with Docker Compose, and cross-browser test execution on a Selenium Grid with a generated test report.
