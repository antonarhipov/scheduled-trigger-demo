import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.schedule

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2019.1"

project {

    buildType(BuildC)
    buildType(BuildD)
    buildType(BuildA)
    buildType(BuildB)
}

object BuildA : BuildType({
    name = "BuildA (Schedule Trigger is here!!!)"

    artifactRules = "file.txt"

    steps {
        script {
            scriptContent = "echo %build.number% > file.txt"
            param("org.jfrog.artifactory.selectedDeployableServer.downloadSpecSource", "Job configuration")
            param("org.jfrog.artifactory.selectedDeployableServer.useSpecs", "false")
            param("org.jfrog.artifactory.selectedDeployableServer.uploadSpecSource", "Job configuration")
        }
    }

    triggers {
        schedule {
            schedulingPolicy = cron {
                minutes = "*"
            }
            branchFilter = ""
            triggerBuild = onWatchedBuildChange {
                buildType = "${BuildC.id}"
            }
            withPendingChangesOnly = false
        }
    }
})

object BuildB : BuildType({
    name = "BuildB"

    steps {
        script {
            scriptContent = "cat file.txt"
            param("org.jfrog.artifactory.selectedDeployableServer.downloadSpecSource", "Job configuration")
            param("org.jfrog.artifactory.selectedDeployableServer.useSpecs", "false")
            param("org.jfrog.artifactory.selectedDeployableServer.uploadSpecSource", "Job configuration")
        }
    }

    dependencies {
        dependency(BuildA) {
            snapshot {
                runOnSameAgent = true
            }

            artifacts {
                artifactRules = "file.txt"
            }
        }
    }
})

object BuildC : BuildType({
    name = "BuildC (Run config this manually)"

    artifactRules = "file.txt"

    steps {
        script {
            scriptContent = "echo %build.number% > file.txt"
            param("org.jfrog.artifactory.selectedDeployableServer.downloadSpecSource", "Job configuration")
            param("org.jfrog.artifactory.selectedDeployableServer.useSpecs", "false")
            param("org.jfrog.artifactory.selectedDeployableServer.uploadSpecSource", "Job configuration")
        }
    }
})

object BuildD : BuildType({
    name = "BuildD"

    steps {
        script {
            scriptContent = "cat file.txt"
            param("org.jfrog.artifactory.selectedDeployableServer.downloadSpecSource", "Job configuration")
            param("org.jfrog.artifactory.selectedDeployableServer.useSpecs", "false")
            param("org.jfrog.artifactory.selectedDeployableServer.uploadSpecSource", "Job configuration")
        }
    }

    dependencies {
        dependency(BuildC) {
            snapshot {
                runOnSameAgent = true
            }

            artifacts {
                artifactRules = "file.txt"
            }
        }
    }
})
