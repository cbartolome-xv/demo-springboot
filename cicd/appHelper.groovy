// Returns an application name code ()
def getAppName() {
    return "springboot-demo"
}

def getAppFriendlyName() {
    return "CI/CD :: Spring Boot Demo Application"
}

def getAdditionalAdminPeopleForTemporaryProject() {
    println "test:::::"
    return [
        users: [ "carlos" ],
        groups: [ ]
    ]
}

def getPerformanceTestsSuites(def performanceTestsFolder, def applicationName, def buildNumber) {
    def performanceTestsSuites = []

    try {
        def files = ''
        dir(performanceTestsFolder) {
            files = sh(script: "ls -1 *.jmx", returnStdout: true).trim() 
        }

        def testFileNames = files.split('\n')

        for (int i=0; i<testFileNames.size(); i++) {
            def file = testFileNames[i]
            def fileName = file.replaceAll('.jmx','')
            performanceTestsSuites.push([name: "jmeter-test-suite", applicationName: "${applicationName}", filename: "${fileName}", buildNumber: "${buildNumber}"])
        }
    }
    catch(Exception ex) {
        println("No performance tests suites found.");
    }
    
    return performanceTestsSuites
}

def getIntegrationTestsInfo(def projectName) {
    return [isAtive: false, command: '', args: '']
}

def getChangeRequestInfo() {
    return [
            
            externalReference: "",
            shortDescription: "",
            description: "",
            environment: "",
            start_date_time: ""
       
    ]
}

return this