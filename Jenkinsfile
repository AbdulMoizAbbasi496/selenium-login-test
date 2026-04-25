pipeline {
    agent {
        docker {
            image 'markhobson/maven-chrome'
            args '-u root:root -v /var/lib/jenkins/.m2:/root/.m2'
        }
    }

    stages {

        stage('Clone Repository') {
            steps {
                git branch: 'main',
                url: 'https://github.com/AbdulMoizAbbasi496/selenium-login-test.git'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test -B'
            }
        }

        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }
    }

    post {
        always {
            script {

                sh "git config --global --add safe.directory ${env.WORKSPACE}"

                // Get committer email safely
                def committer = sh(
                    script: "git log -1 --pretty=format:'%ae' || echo ''",
                    returnStdout: true
                ).trim()

                // fallback if invalid email
                if (!committer || !committer.contains("@")) {
                    committer = "moiz45573@gmail.com"
                }

                // Parse test results safely
                def raw = sh(
                    script: "grep -h \"<testcase\" target/surefire-reports/*.xml || true",
                    returnStdout: true
                ).trim()

                int total = 0
                int passed = 0
                int failed = 0
                int skipped = 0
                def details = ""

                if (raw) {
                    raw.split('\n').each { line ->
                        total++

                        def match = (line =~ /name="([^"]+)"/)
                        def name = match ? match[0][1] : "unknown"

                        if (line.contains("<failure")) {
                            failed++
                            details += "${name} — FAILED\n"
                        } else if (line.contains("<skipped")) {
                            skipped++
                            details += "${name} — SKIPPED\n"
                        } else {
                            passed++
                            details += "${name} — PASSED\n"
                        }
                    }
                }

                // ✅ FIX: correct build status logic
                if (failed == 0) {
                    currentBuild.result = 'SUCCESS'
                } else {
                    currentBuild.result = 'FAILURE'
                }

                def status = currentBuild.result

                def emailBody = """
Build #${env.BUILD_NUMBER} — ${status}

Committer: ${committer}

Total Tests: ${total}
Passed: ${passed}
Failed: ${failed}
Skipped: ${skipped}

Details:
${details}

Build URL: ${env.BUILD_URL}
"""

                // ✅ FIX: prevent email crash from failing build
                try {
                    emailext(
                        to: "${committer}, moiz45573@gmail.com",
                        subject: "Build #${env.BUILD_NUMBER} - ${status}",
                        body: emailBody
                    )
                } catch (Exception e) {
                    echo "Email failed but build continues: ${e}"
                }
            }
        }
    }
}
