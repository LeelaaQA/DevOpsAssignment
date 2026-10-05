// Declarative pipeline for Jenkins running on WINDOWS (uses 'bat', not 'sh').
// Requirements on the Jenkins machine: JDK 21, Maven, Git, Docker Desktop (all on PATH).
pipeline {
    agent any

    options {
        timeout(time: 40, unit: 'MINUTES')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    parameters {
        choice(name: 'BROWSER',
               choices: ['all', 'chrome', 'firefox', 'edge'],
               description: 'Which browser(s) to run on the Selenium Grid')
    }

    // Trigger: Jenkins checks GitHub every ~5 minutes and builds when there is a new commit.
    // You can always start a build manually with "Build with Parameters".
    triggers {
        pollSCM('H/5 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Tools') {
            steps {
                bat 'java -version'
                bat 'mvn -version'
                bat 'docker --version'
                bat 'docker compose version'
            }
        }

        stage('Start Selenium Grid') {
            steps {
                bat 'docker compose -f docker/docker-compose.yml up -d'
                bat 'powershell -NoProfile -ExecutionPolicy Bypass -File docker/wait-for-grid.ps1'
                bat 'docker compose -f docker/docker-compose.yml ps'
            }
        }

        stage('Run Tests') {
            steps {
                // catchError: if tests fail, mark the build FAILED but still publish results and stop the Grid.
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    script {
                        if (params.BROWSER == 'all') {
                            bat 'mvn -B clean test -DsuiteXmlFile=testng-all-browsers.xml'
                        } else {
                            bat "mvn -B clean test -Dbrowser=${params.BROWSER}"
                        }
                    }
                }
            }
        }

        stage('Publish TestNG Results') {
            steps {
                junit allowEmptyResults: true, testResults: 'target/surefire-reports/TEST-*.xml'
                archiveArtifacts artifacts: 'target/surefire-reports/**, target/screenshots/**', allowEmptyArchive: true
                // Print the pass/fail summary lines into the Jenkins console
                bat(returnStatus: true, script: 'findstr /C:"Tests run:" target\\surefire-reports\\*.txt')
            }
        }
    }

    post {
        always {
            // Stop Selenium Grid even when a stage failed
            bat(returnStatus: true, script: 'docker compose -f docker/docker-compose.yml down')
            echo "Final build result: ${currentBuild.currentResult}"
        }
    }
}
