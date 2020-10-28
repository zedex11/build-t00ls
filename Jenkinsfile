node('centos') {
    stage('SCM Checkout'){
        git branch: 'shryshchanka', 
        credentialsId: '32894695-c296-4b7d-a9d1-d66d35a9b476', 
        url: 'git@github.com:zedex11/build-t00ls.git'
    }
    stage('Compile-Package'){
        def mvn = tool (name: 'Maven', type: 'maven') + '/bin/mvn'
        sh "${mvn} -f helloworld-project/helloworld-ws/pom.xml  package"
    }
    stage('Sonar Publish'){
	    def mvn = tool (name: 'Maven', type: 'maven') + '/bin/mvn'
        withSonarQubeEnv('Sonar'){
            sh "${mvn} org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar"
        }
	}

}