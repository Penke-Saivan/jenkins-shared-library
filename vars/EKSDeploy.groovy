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
            appVersion = configMap.get("appVersion")
            ACC_ID = '131676642204'
            PROJECT = configMap.get("project")
            COMPONENT = configMap.get("component")
            DEPLOY_TO = configMap.get("DEPLOY_TO")
            REGION = 'us-east-1'
        }
        //  after the timeout abort the pipeline
        options {
            timeout(time: 30, unit: 'MINUTES')
            disableConcurrentBuilds()
        }
        // parameters {
        //     string(name: 'appVersion', description:'which version')
        //     choice(name: 'DEPLOY_TO', choices: ['qa', 'dev', 'prod'], description: 'Pick something')
        // }

        // ------------------- build stage -----------------------
        stages {
                stage('Deploy') {
                steps {
                    script {
                        withAWS(region: 'us-east-1', credentials: 'aws-creds') {
                            sh """
                        set -e     
                        #to fail pipeline automatically
                        ls -l
                        pwd

                        aws eks update-kubeconfig --name ${PROJECT}-${DEPLOY_TO} --region ${REGION}
                        kubectl get nodes
                        ls -l
                        echo ${DEPLOY_TO}-${appVersion}
                        sed -i "s/IMAGE_VERSION/${appVersion}/" values.yaml
                        helm upgrade --install ${COMPONENT} -f values-${DEPLOY_TO}.yaml -n ${PROJECT} --atomic --wait --timeout=5m .
                        #kubectl apply -f ${COMPONENT}-${DEPLOY_TO}.yaml 
                        #refer the catlogue-deploy (above command is for argocd helm application)
                       """
                        }
                    }
                }
                }
// values.yaml-helm

// deployment:
//   imageRepo: 131676642204.dkr.ecr.us-east-1.amazonaws.com/roboshop/catalogue # tonyduck/catalogue
//   imageVersion: IMAGE_VERSION #this version is substiuted by sed editor in the run time
// service:
//   servicePort: 8080 # default value
                stage("Functional-Tests-TOBEADDED"){
                    when{
                        expression { DEPLOY_TO == "dev"}
                    }
                    steps{
                        script{
                            sh """
                                echo "FUNCTIONAL-TESTING in DEV-ENVIRONMENT"
                                """
                        }
                    }
                }
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
