pipeline {
	agent any

    tools {
        jdk "java21"
        gradle "gradle-8-4"
    }
	
	stages {
		stage('Configuration check') {
			steps {
				echo 'SUCCESS: Jenkins repository link is working correctly'
			}
		}

        stage("Build") {
            steps {
                sh '''./gradlew build clean '''
                echo "Build stage completed"
            }
        }

        stage("Testi") {
            steps {
                sh "./gradlew test"
                echo "Test stage completed"
            }
        }
	}

}
