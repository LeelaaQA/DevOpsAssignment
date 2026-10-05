# Evidence Guide

Put your REAL screenshots in this folder after you run the project. Do not add edited or fake images.
Suggested file names (same numbers as `docs/execution-guide.md`):

| # | File name | What it must show |
|---|---|---|
| 01 | 01_docker_version.png | `docker --version` and Docker running |
| 02 | 02_compose_up.png | `docker compose -f docker/docker-compose.yml up -d` finished |
| 03 | 03_compose_ps.png | 4 containers "Up" (hub, chrome, firefox, edge) |
| 04 | 04_grid_ui_3_nodes.png | http://localhost:4444/ui with Chrome, Firefox and Edge nodes |
| 05 | 05_chrome_run.png | Console of `-Dbrowser=chrome` incl. "Actual browser" line and `Tests run` summary |
| 06 | 06_firefox_run.png | Same for Firefox |
| 07 | 07_edge_run.png | Same for Edge |
| 08 | 08_all_browsers_run.png | Console of `testng-all-browsers.xml` run |
| 09 | 09_testng_report.png | `target\surefire-reports\index.html` or `emailable-report.html` |
| 10 | 10_jenkins_job_config.png | Jenkins job: Pipeline script from SCM + repo URL |
| 11 | 11_jenkins_console_result.png | Jenkins Console Output ending with the final result |
| 12 | 12_jenkins_build_page.png | Build page with test results |
| 13 | 13_git_push.png | `git push -u origin main` output |
| 14 | 14_github_repo.png | GitHub repository showing the project files |
| 15 | 15_live_browser_view.png (optional) | http://localhost:7900 (or 7901/7902) while a test runs |

If a test fails (for example Amazon shows a CAPTCHA), keep that screenshot too and mention it honestly in your submission.
