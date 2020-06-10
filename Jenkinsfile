#!/usr/bin/env groovy
@Library('jenkins-library@master') _

pipeline {
    agent {
        node {
            label 'docker'
        }
    }

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '5', daysToKeepStr: '5'))
    }

    parameters {
        choice(
                name: "testTarget",
                choices: "latest\ntesting",
                description: "Set the DukeCon target environment for testing"
        )
    }

    triggers {
        pollSCM('*/3 * * * *')
    }

    stages {
        stage('API Test') {
            steps {
                sh "mvn ${mvnProfile(params.testTarget)} clean test"
            }
        }
    }
    post {
        always {
            sendNotification currentBuild.result
        }
        failure {
            // notify users when the Pipeline fails
            mail to: 'gerd@aschemann.net',
                    subject: "Failed DukeCon API Test Pipeline: ${currentBuild.fullDisplayName}",
                    body: "Something is wrong with ${env.BUILD_URL}"
        }
    }
}

def mvnProfile(testTarget) {
    if ("latest" != testTarget) {
        return "-P ${testTarget}"
    }
    return ""
}