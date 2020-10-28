node('centos') {
    stage('SCM Checkout'){
        git branch: 'shryshchanka', 
        credentialsId: 'root', 
        url: 'git@github.com:zedex11/build-t00ls.git'
    }
    stage('Compile-Package'){
        sh 'mvn package'
    }

}