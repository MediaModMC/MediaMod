workflow "Build Mod" {
  on = "push"
}

action "Check" {
  uses = "MrRamych/gradle-actions/openjdk-8@2.1"
}

action "Run setupDecompWorkspace" {
  uses = "MrRamych/gradle-actions/openjdk-8@2.1"
  args = "setupDecompWorkspace"
}

action "Run build" {
  uses = "MrRamych/gradle-actions/openjdk-8@2.1"
  args = "build"
}
