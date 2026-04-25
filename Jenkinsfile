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
                git branch: 'main', url: 'https://github.com/AbdulMoizAbbasi496/selenium-login-test.git'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test -B'
            }
        }

        stage('Publish Test Results') {
            steps {
                // always() here so it runs even if tests fail
                junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            }
        }
    }

    post {
        always {
            script {
                sh "git config --global --add safe.directory ${env.WORKSPACE}"

                def committer = sh(
                    script: "git log -1 --pretty=format:'%ae'",
                    returnStdout: true
                ).trim()

                // Remove any accidental double @
                committer = committer.replaceAll("@+", "@")

                echo "Committer email: ${committer}"

                def xmlExists = sh(
                    script: "ls target/surefire-reports/*.xml 2>/dev/null | wc -l",
                    returnStdout: true
                ).trim()

                int total = 0
                int passed = 0
                int failed = 0
                int skipped = 0
                def details = ""

                if (xmlExists != "0") {
                    def raw = sh(
                        script: 'grep -h "<testcase" target/surefire-reports/*.xml || echo ""',
                        returnStdout: true
                    ).trim()

                    if (raw) {
                        raw.split('\n').each { line ->
                            total++
                            def nameMatch = (line =~ /name="([^"]+)"/)
                            def name = nameMatch ? nameMatch[0][1] : "Unknown"

                            if (line.contains("<failure")) {
                                failed++
                                details += "${name} — FAILED\n"
                            } else if (line.contains("<skipped") || line.contains("</skipped>")) {
                                skipped++
                                details += "${name} — SKIPPED\n"
                            } else {
                                passed++
                                details += "${name} — PASSED\n"
                            }
                        }
                    }
                } else {
                    details = "No test reports found.\n"
                }

                def status = currentBuild.result ?: 'SUCCESS'

                def emailBody = """
Build #${env.BUILD_NUMBER} — ${status}

Committer: ${committer}

TEST SUMMARY
━━━━━━━━━━━━━━━━━━━━
Total Tests:   ${total}
Passed:        ${passed}
Failed:        ${failed}
Skipped:       ${skipped}

Detailed Results:
${details}

Build URL: ${env.BUILD_URL}
"""

                emailext(
                    to: "${committer}, qasimalik@gmail.com",
                    subject: "Build #${env.BUILD_NUMBER} — ${status} | selenium-login-test",
                    body: emailBody
                )
            }
        }
    }
}
