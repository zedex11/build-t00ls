node {
    stage('Preparation (Checking out)'){
        git branch: 'shryshchanka', 
        credentialsId: '32894695-c296-4b7d-a9d1-d66d35a9b476', 
        url: 'git@github.com:zedex11/build-t00ls.git'
    }
    def mvn = tool (name: 'Maven', type: 'maven') + '/bin/mvn'
    stage('Building code'){
        sh "${mvn} -f helloworld-project/helloworld-ws/pom.xml  package"
    }
    stage('Sonar scan'){
        def sonar = 'org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746'
        withSonarQubeEnv('Sonar'){
            sh "${mvn} -f helloworld-project/helloworld-ws/pom.xml ${sonar}:sonar"
        }
    }
    stage('Testing'){
        sh "${mvn} -f helloworld-project/helloworld-ws/pom.xml pre-integration-test"
        sh "echo mvn integration-test"
        sh "echo mvn post-integration-test"
    }
    stage('Triggering job and fetching artefact after finishing'){
        build job: 'MNTLAB-shryshchanka-child1-build-job', parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: 'shryshchanka']]
        copyArtifacts(projectName: 'MNTLAB-shryshchanka-child1-build-job');
    }
    stage('Packaging and Publishing results'){
        sh "cp helloworld-project/helloworld-ws/target/helloworld-ws.war ."
        sh "tar cvzf pipeline-shryshchanka-$BUILD_NUMBER.tar.gz helloworld-ws.war Jenkinsfile output.txt"
    }
}