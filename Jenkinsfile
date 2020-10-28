node('centos') {
    stage('Preparation (Checking out)'){
        git branch: 'shryshchanka', 
        credentialsId: '32894695-c296-4b7d-a9d1-d66d35a9b476', 
        url: 'git@github.com:zedex11/build-t00ls.git'
    }
    stage('Building code'){
        def mvn = tool (name: 'Maven', type: 'maven') + '/bin/mvn'
        sh "${mvn} -f helloworld-project/helloworld-ws/pom.xml  package"
    }
    stage('Sonar scan'){
	    def mvn = tool (name: 'Maven', type: 'maven') + '/bin/mvn'
        def sonar = 'org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746'
        withSonarQubeEnv('Sonar'){
            sh "${mvn} -f helloworld-project/helloworld-ws/pom.xml ${sonar}:sonar"
        }
	}

}