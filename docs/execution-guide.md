# Execution Guide (run everything on your Windows machine)

Run all commands in **Command Prompt** from the project root (the folder with `pom.xml`).
Nothing in this guide has been executed for you. Fill in the placeholders with your REAL results.

## 1. Verify Docker
```
docker --version
docker compose version
docker info
```
Expected: versions print and `docker info` shows a running server (Docker Desktop open).
`[SCREENSHOT 01: docker --version / docker info]`

## 2. Start Selenium Grid
```
docker compose -f docker/docker-compose.yml up -d
```
Expected: 4 containers created/started (selenium-hub, chrome, firefox, edge nodes).
`[SCREENSHOT 02: docker compose up -d output]`

## 3. Verify Grid
```
docker compose -f docker/docker-compose.yml ps
curl http://localhost:4444/status
```
Browser: http://localhost:4444/ui -> Chrome, Firefox and Edge nodes visible.
`[SCREENSHOT 03: docker compose ps]`  `[SCREENSHOT 04: Grid UI with 3 nodes]`

## 4. Run Chrome
```
mvn clean test -Dbrowser=chrome
```
Look for `Actual browser : chrome ...` and a Maven summary `Tests run: ...`.
`[SCREENSHOT 05: Chrome run]`

## 5. Run Firefox
```
mvn clean test -Dbrowser=firefox
```
`[SCREENSHOT 06: Firefox run]`

## 6. Run Edge
```
mvn clean test -Dbrowser=edge
```
`[SCREENSHOT 07: Edge run]`

## 7. Run all required tests on all browsers
```
mvn clean test -DsuiteXmlFile=testng-all-browsers.xml
```
`[SCREENSHOT 08: all-browsers run summary]`

## 8. Generate / open the TestNG report
```
start target\surefire-reports\index.html
start target\surefire-reports\emailable-report.html
```
`[SCREENSHOT 09: TestNG report]`

## 9. Run Jenkins
Follow "One-time Jenkins setup" in `README.md`, then **Build with Parameters** -> `all` -> Build.
`[SCREENSHOT 10: Jenkins job configuration]` `[SCREENSHOT 11: Console Output with final result]` `[SCREENSHOT 12: Build page / test result trend]`

## 10. Push to GitHub
```
git init
git add .
git commit -m "DevOps assignment: Amazon Selenium Grid automation"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repo>.git
git push -u origin main
```
`[SCREENSHOT 13: git push output]` `[SCREENSHOT 14: GitHub repository page]`

Stop the Grid when finished: `docker compose -f docker/docker-compose.yml down`

## My actual results (fill in after running)
| Browser | Tests run | Passed | Failed | Skipped | Notes |
|---|---|---|---|---|---|
| Chrome | `TO BE FILLED` | | | | |
| Firefox | `TO BE FILLED` | | | | |
| Edge | `TO BE FILLED` | | | | |
| Jenkins build # | `TO BE FILLED` | Result: | | | |

## Amazon limitations
- Amazon can show a CAPTCHA or robot check to automated sessions. The framework detects it, fails the test with a clear message and saves a screenshot. It does **not** bypass it.
- Results depend on network, region and time. If tests fail only because of a robot check, say so honestly in your submission and include the screenshot as evidence.
- Only public pages are tested: home page and product search. No login, cart or payment.
- If Amazon changes its HTML, update the locators in `src/main/java/pages/`.
