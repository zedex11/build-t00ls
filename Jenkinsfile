node('centos') {
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
    stage('Testing') {
        parallel(
            'Pre-integration-test': {
                sh("${mvn} -f helloworld-project/helloworld-ws/pom.xml pre-integration-test")
            },
            'Integration-test': {
                sh("echo mvn integration-test")
            },
            'Post-integration-test': {
                sh("echo mvn post-integration-test")
            }
        )
    }

    stage('Triggering job and fetching artefact after finishing'){
        build job: 'MNTLAB-shryshchanka-child1-build-job', parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: 'shryshchanka']]
        copyArtifacts(projectName: 'MNTLAB-shryshchanka-child1-build-job');
    }
    stage('Packaging and Publishing results'){
        sh """
        cp helloworld-project/helloworld-ws/target/helloworld-ws.war .
        tar -czf pipeline-shryshchanka-${BUILD_NUMBER}.tar.gz helloworld-ws.war Jenkinsfile output.txt
        cat<<EOF>helloworld-project/helloworld-ws/src/main/webapp/index.html
        <html>
        <head>
        <title>shryshchanka</title>
        </head>
        <body>
        <h1>Hello! Bellow information about this build:<h1>
        <code>Created: Siarhei Hryshchanka <br>
        <code>BUILD_NUMBER: ${BUILD_NUMBER}<br>
        <code>JOB_NAME: ${JOB_NAME}<br>
        </body>
        </html>
EOF
        sudo docker login docker.k8s.shryshchanka.playpit.by -u admin -p devopslab
        sudo docker build -t docker.k8s.shryshchanka.playpit.by/helloworld-shryshchanka:${BUILD_NUMBER} .
        sudo docker push docker.k8s.shryshchanka.playpit.by/helloworld-shryshchanka:${BUILD_NUMBER}
        sudo docker image prune -f
        """
        nexusArtifactUploader artifacts: [
            [artifactId: 'pipeline-shryshchanka', classifier: '', file: 'pipeline-shryshchanka-${BUILD_NUMBER}.tar.gz', type: 'tar.gz']
        ], 
        credentialsId: 'fd995f9d-21e0-458d-8d02-63e40e2c9daa', 
        groupId: 'task.module10', 
        nexusUrl: 'nexus.k8s.shryshchanka.playpit.by', 
        nexusVersion: 'nexus3', 
        protocol: 'https', 
        repository: 'maven-releases', 
        version: '${BUILD_NUMBER}'
    }

    stage('deploy') {
        node('gcp-k8s'){
            git branch: 'shryshchanka', credentialsId: '32894695-c296-4b7d-a9d1-d66d35a9b476', url: 'git@github.com:zedex11/build-t00ls.git'
            sh """
            sed -i "/- image:/s/custom/${BUILD_NUMBER}/" tomcat.yaml
            kubectl apply -f tomcat.yaml
            """
        }
    }

}

