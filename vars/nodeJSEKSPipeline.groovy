//default function- call
def call(Map configMap) {
    pipeline {
    // ------------pre-build-----------------

        // agent configured
        agent {
            node {
                label 'AGENT-1'
            }
        }

        // environment variables
        environment {
            COURSE = 'Jenkins'
            appVersion = ''
            ACC_ID = '131676642204'
            PROJECT = 'roboshop'
            COMPONENT = 'catalogue'
        }
        //  after the timeout abort the pipeline
        options {
            timeout(time: 60, unit: 'MINUTES')
            disableConcurrentBuilds()
        }

        // ------------------- build stage -----------------------
        stages {
            stage('BUILD') {
                steps {
                    script {
                        sh """
                        echo "Building----------A-Read-Version"
                        echo "COurse we learn is : $COURSE"

                       """
                    }
                }
            }
            stage('Read-Version') {
            // input {
            //     message "Should we continue?"
            //     ok "Yes, we should."
            //     submitter "alice,bob"
            //     parameters {
            //         string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')
            //     }}

                steps {
                    script {
                        def packageJSON = readJSON file: 'package.json'
                        appVersion = packageJSON.version
                        echo "app verison: ${appVersion}"
                    }
                }
            }

                stage('Install Deps') {
                steps {
                    script {
                        sh '''
                            npm install

                       '''
                    }
                }
                }

            stage('Unit Test') {
                steps {
                    script {
                        sh '''
                        npm test
                    '''
                    }
                }
            }
        //Here you need to select scanner tool and send the analysis to server
        // stage('Sonar Scan') {
        //     environment {
        //         def scannerHome = tool 'sonar-8.0'
        //     }
        //     steps {
        //         script {
        //             withSonarQubeEnv('sonar-server') {
        //                 sh  "${scannerHome}/bin/sonar-scanner"
        //             }
        //         }
        //     }
        // }
        // stage('Quality Gate') {
        //     steps {
        //         timeout(time: 1, unit: 'HOURS') {
        //             // Wait for the quality gate status
        //             // abortPipeline: true will fail the Jenkins job if the quality gate is 'FAILED'
        //             waitForQualityGate abortPipeline: true
        //             // ERROR: Pipeline aborted due to quality gate failure: ERROR
        //         }
        //     }
        // }
//Depandabot

            // stage('Dependabot Security Gate') {
            //     environment {
            //         GITHUB_OWNER = 'Penke-Saivan'
            //         GITHUB_REPO  = 'catalogue-pipeline'
            //         GITHUB_API   = 'https://api.github.com'
            //         GITHUB_TOKEN = credentials('GITHUB_TOKEN')
            //     }

            //     steps {
            //         script {
            //         /* Use sh """ when you want to use Groovy variables inside the shell.
            //         Use sh ''' when you want the script to be treated as pure shell. */
            //             sh '''
            //         echo "Fetching Dependabot alerts..."

            //         response=$(curl -s \
            //             -H "Authorization: token ${GITHUB_TOKEN}" \
            //             -H "Accept: application/vnd.github+json" \
            //             "${GITHUB_API}/repos/${GITHUB_OWNER}/${GITHUB_REPO}/dependabot/alerts?per_page=100")

            //         echo "${response}" > dependabot_alerts.json

            //         high_critical_open_count=$(echo "${response}" | jq '[.[]
            //             | select(
            //                 .state == "open"
            //                 and (.security_advisory.severity == "high"
            //                     or .security_advisory.severity == "critical")
            //             )
            //         ] | length')

            //         echo "Open HIGH/CRITICAL Dependabot alerts: ${high_critical_open_count}"

            //         if [ "${high_critical_open_count}" -gt 0 ]; then
            //             echo "❌ Blocking pipeline due to OPEN HIGH/CRITICAL Dependabot alerts"
            //             echo "Affected dependencies:"
            //             echo "$response" | jq '.[]
            //             | select(.state=="open"
            //             and (.security_advisory.severity=="high"
            //             or .security_advisory.severity=="critical"))
            //             | {dependency: .dependency.package.name, severity: .security_advisory.severity, advisory: .security_advisory.summary}'
            //             exit 1
            //         else
            //             echo "✅ No OPEN HIGH/CRITICAL Dependabot alerts found"
            //         fi
            //         '''
            //         }
            //     }
            // }

            // Build images
            stage('Build Images') {
                steps {
                    script {
                        withAWS(region:'us-east-1', credentials:'aws-creds') {
    // do something-withAWS keeps aws creds ready for the code in this block

                                    sh """
                echo "Building Image and pushing to ECR"
                aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${ACC_ID}.dkr.ecr.us-east-1.amazonaws.com
                docker build -t ${ACC_ID}.dkr.ecr.us-east-1.amazonaws.com/${PROJECT}/${COMPONENT}:${appVersion} .
                docker images
                docker push ${ACC_ID}.dkr.ecr.us-east-1.amazonaws.com/${PROJECT}/${COMPONENT}:${appVersion}

                """
                        }
                    }
                }
            }

            // stage('Trivy Scan ') {
            //     steps {
            //         script {
            //             sh """
            //             trivy image \
            //                 --scanners vuln \
            //                 --severity HIGH,CRITICAL,MEDIUM \
            //                 --pkg-types os \
            //                 --exit-code 1 \
            //                 --format table \
            //                 ${ACC_ID}.dkr.ecr.us-east-1.amazonaws.com/${PROJECT}/${COMPONENT}:${appVersion}
            //         """
            //         }
            //     }
            // }

        //                 stage('Build Images') {
        //     steps {
        //         script{
        //             sh """
        //                     docker build -t catalogue:${appVersion} .
        //                     docker images

        //                """
        //         }
        //     }
        // }
        // stage('Deploy') {

        //     when {
        //         // ver important for deployment
        //         expression {
        //              "${params.DEPLOY}" == "true"
        //         }

        //         }

            //     steps {
            //         script{
            //             sh """
            //                 echo "Building----------A-Deployment------------------WHEN-CONDITION"
            //                """
            //         }
            //     }
            // }
            }

        // --------------------------post build--------------------
        post {
            always {
                echo 'I will always say Hello again!'
                cleanWs()
            }
            success {
                echo 'Success---------------'
            }
            failure {
                echo 'failure---------------'
            }
            aborted {
                echo 'someone -aborted--------------------------------'
                echo 'may be timeeout  -aborted--------------------------------'
            }
        }
        }
    }
