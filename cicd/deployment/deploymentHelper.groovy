def getPermanentProjectsConfiguration() {
  def permanentProjects = []
  
  permanentProjects.push([
    environment: 'dev',
    branchName: 'master',
    isBranchNameRegex: false,
    ocpProjectName: 'demo-springboot-permanent',
    ocpDcName: 'demo-springboot',
    ocpDeploymentStrategy: 'single',
    sourceImageStreamUrl: 'docker-registry.default.svc:5000/cicd-secure-docker/demo-app'
  ])

  return permanentProjects
}

def isBranchPromotable(def branchName) {
    def permanentProjects = getPermanentProjectsConfiguration()
    def isBranchPromotable = false

    for (def projectDefinition in permanentProjects) {
        if (projectDefinition.isBranchNameRegex) {
            if (projectDefinition.branchName ==~ branchName) {
                isBranchPromotable = true
                break
            }
        }
        
        if (projectDefinition.branchName == branchName) {
            isBranchPromotable = true
            break
        }
    }

    return isBranchPromotable
}

def getDeploymentsInfo(def branchName, def environment) {
  def deploymentsInfo = []
  def permanentProjects = getPermanentProjectsConfiguration()

  for (def projectDefinition in permanentProjects) {
        if (projectDefinition.isBranchNameRegex) {
            if (projectDefinition.branchName ==~ branchName) {
                deploymentsInfo.push(projectDefinition)
            }
        }
        else if (projectDefinition.branchName == branchName) {
            deploymentsInfo.push(projectDefinition)
        }
    }

  return deploymentsInfo
}

def run(def command, def throwException = false) {
  def result = [ output: null, error: false, exception: null ]
  try {
    result.output = sh(script: command, returnStdout: true).trim()
  }
  catch(Exception ex) {
    result.error = true
    result.exception = ex
  }

  if (throwException && result.error) {
    throw result.exception
  }

  return result
}

def monitorApplicationDeployment(def projectName, def appName) {
  // Wait until the deployment has been marked as completed. Protect the checking loop with a timeout.
  def rcVersion = run("oc get dc ${appName} -o jsonpath='{.status.latestVersion}' -n ${projectName}", true).output
  def rcPhase = ""
  def maxRetries = 12
  def retries = 0

  while (retries < maxRetries) {
    rcPhase = run("oc get rc ${appName}-${rcVersion} -o jsonpath='{.metadata.annotations.openshift\\.io/deployment\\.phase}' -n ${projectName}", true).output
    if (rcPhase == "Complete") {
      println "Deployment complete successfully"
      break
    }
    
    retries++
    println "Waiting 5s for the deployment to complete [retry ${retries} of ${maxRetries}]..."
    sleep 5
  }

  if (rcPhase != "Complete") {
    throw new Exception("The deployment of the application did not complete or it failed")
  }
}

def deployApplication(def deploymentFolder, def environment, def projectName, def appName, def imageStreamName, def appVersion) {
  dir (deploymentFolder) {
    if (environment == 'cicd') {
      sh """
        oc process -f app-template-cicd.yaml -p PROJECT_NAME='${projectName}' -p APPLICATION_NAME='${appName}' -p IMAGE_STREAM_NAME='${imageStreamName}' -p TAG_NAME='${appVersion}'|oc apply -n ${projectName} -f -
      """
      monitorApplicationDeployment(projectName, appName)
    }
    else {
      // The pipeline will invoke this method as many times as needed per all different environemnts and deployments
      // where this new version has to be deployed
      sh """
        oc process -f app-template.yaml -p PROJECT_NAME='${projectName}' -p APPLICATION_NAME='${appName}' -p IMAGE_STREAM_NAME='${imageStreamName}' -p TAG_NAME='${appVersion}'|oc apply -n ${projectName} -f -
      """
      monitorApplicationDeployment(projectName, appName)
    }
  }
}

return this