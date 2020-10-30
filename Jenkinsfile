def sent_mail(err, STAGE_NAME){
    mail bcc: '', 
    body: "ERROR: ${err} OCCURRED in BUILD_NUMBER: ${BUILD_NUMBER}, JOB_NAME: ${JOB_NAME}, BUILD_URL: ${BUILD_URL}, NODE_NAME: ${NODE_NAME}, on the STAGE: ${STAGE_NAME}", 
    cc: '', 
    from: 'zedex15@yandex.ru', 
    replyTo: '', 
    subject: "ERROR CI: Project name -> ${JOB_NAME}",
    to: 'zedex15@yandex.ru'
}
def push_docker(DOCKER_REGISTRY, USER, PASSWD, DOCKER_TAG){
    sh """
    sudo docker login ${DOCKER_REGISTRY} -u ${USER} -p ${PASSWD}
    sudo docker build -t ${DOCKER_TAG} .
    sudo docker push ${DOCKER_TAG}
    sudo docker image prune -f
    """
}
def push_nexus(nexusUrl,repository,groupId,artifactId,file,type,credentialsId){
    nexusArtifactUploader artifacts: [
    [artifactId: "${artifactId}", classifier: '', file: "${file}", type: "${type}"]
    ], 
    credentialsId: "${credentialsId}", 
    groupId: "${groupId}", 
    nexusUrl: "${nexusUrl}", 
    nexusVersion: 'nexus3', 
    protocol: 'https', 
    repository: "${repository}", 
    version: '${BUILD_NUMBER}'
}

node('centos') {
    def mvn = tool (name: 'Maven', type: 'maven') + '/bin/mvn'
    try {
        stage('Preparation (Checking out)'){
            git branch: 'shryshchanka', 
            credentialsId: '32894695-c296-4b7d-a9d1-d66d35a9b476', 
            url: 'git@github.com:zedex11/build-t00ls.git'
        }
    } catch(err) {
        def STAGE_NAME = 'Preparation (Checking out)'
        sent_mail("${err}", "${STAGE_NAME}")
    }
    try {
        stage('Building code'){
            sh "${mvn} -f helloworld-project/helloworld-ws/pom.xml  package"
        }
    } catch(err) {
        def STAGE_NAME = 'Building code'
        sent_mail("${err}", "${STAGE_NAME}")
    }
    try {
        stage('Sonar scan'){
            def sonar = 'org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746'
            withSonarQubeEnv('Sonar'){
                sh "${mvn} -f helloworld-project/helloworld-ws/pom.xml ${sonar}:sonar"
            }
        }
    } catch(err) {
        def STAGE_NAME = 'Sonar scan'
        sent_mail("${err}", "${STAGE_NAME}")
    }
    try {
        stage('Testing') {
            sh """
            GIT_COMMIT=`git log -n 1 --pretty=format:"%H"`
            cp helloworld-project/helloworld-ws/target/helloworld-ws.war .
            tar -czf pipeline-shryshchanka-${BUILD_NUMBER}.tar.gz helloworld-ws.war Jenkinsfile output.txt
            cat<<EOF>helloworld-project/helloworld-ws/src/main/webapp/index.html
            <html>
            <head>
            <title>shryshchanka</title>
            </head>
            <body>
            <h1>Hello! Bellow information about this build:<h1>
            <code>Created: ${BUILD_USER} <br>
            <code>BUILD_NUMBER: ${BUILD_NUMBER}<br>
            <code>JOB_NAME: ${JOB_NAME}<br>
            <code>GIT_COMMIT: ${GIT_COMMIT}<br>
            </body>
            </html>
EOF
            """
            parallel(
                'Pre-integration-test': {
                    sh("${mvn} -f helloworld-project/helloworld-ws/pom.xml clean verify -P pre-integration-test")
                },
                'Integration-test': {
                    sh("echo mvn integration-test")
                },
                'Post-integration-test': {
                    sh("echo mvn post-integration-test")
                }
            )
        }
    } catch(err) {
        def STAGE_NAME = 'Testing'
        sent_mail("${err}", "${STAGE_NAME}")
    }
    try {
        stage('Triggering job and fetching artefact after finishing'){
            build job: 'MNTLAB-shryshchanka-child1-build-job', parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: 'shryshchanka']]
            copyArtifacts(projectName: 'MNTLAB-shryshchanka-child1-build-job');
        }
    } catch(err) {
        def STAGE_NAME = 'Triggering job and fetching artefact after finishing'
        sent_mail("${err}", "${STAGE_NAME}")
    }
    try {
        stage('Packaging and Publishing results'){
            //nexus push
            def nexusUrl = 'nexus.k8s.shryshchanka.playpit.by'
            def repository = 'maven-releases'
            def groupId = 'task.module10'
            def artifactId = 'pipeline-shryshchanka'
            def file = 'pipeline-shryshchanka-${BUILD_NUMBER}.tar.gz'
            def type = 'tar.gz'
            def credentialsId = 'fd995f9d-21e0-458d-8d02-63e40e2c9daa'
            push_nexus("${nexusUrl}","${repository}","${groupId}","${artifactId}","${file}","${type}","${credentialsId}")
            //docker push
            def DOCKER_REGISTRY = 'docker.k8s.shryshchanka.playpit.by'
            def USER = 'admin'
            def PASSWD = 'devopslab'
            def DOCKER_TAG = 'docker.k8s.shryshchanka.playpit.by/helloworld-shryshchanka:${BUILD_NUMBER}'
            push_docker("${DOCKER_REGISTRY}","${USER}","${PASSWD}","${DOCKER_TAG}")
        }
    } catch(err) {
        def STAGE_NAME = 'Triggering job and fetching artefact after finishing'
        sent_mail("${err}", "${STAGE_NAME}")
    }
    stage 'Asking for manual approval'
        timeout(time: 120, unit: 'SECONDS') { // change to a convenient timeout for you
            input(
            id: 'Approve', message: 'Do you approve artefact build?', ok: 'yes'
            )
        }
    try {
        stage('deploy') {
            node('gcp-k8s'){
                git branch: 'shryshchanka', credentialsId: '32894695-c296-4b7d-a9d1-d66d35a9b476', url: 'git@github.com:zedex11/build-t00ls.git'
                sh """
                sed -i "/- image:/s/custom/${BUILD_NUMBER}/" tomcat.yaml
                kubectl apply -f tomcat.yaml
                """
            }
        }
    } catch(err) {
        def STAGE_NAME = 'Asking for manual approval'
        sent_mail("${err}", "${STAGE_NAME}")
    }
    stage('Sending status'){
        mail bcc: '', 
        body: "Build: ${BUILD_NUMBER} job: ${JOB_NAME} build_url: ${BUILD_URL} node_name: ${NODE_NAME} was successful", 
        cc: '', 
        from: 'zedex15@yandex.ru', 
        replyTo: '', 
        subject: "JOB STATUS: Project name -> ${JOB_NAME}",
        to: 'zedex15@yandex.ru'
    }

}