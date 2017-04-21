# Nubot Simulator
This version of nubot simulator is slightly modified from https://github.com/domardfern/Nubot-Simulator
It is a tool built to help researchers model their Nubot configurations and rulesets.

# Features
* Video Export
* Simulation Speed Changes
* Configuration Editing

# Using the Simulator
* IDE: IntelliJ IDEA CE
* 2 modes to run
    * GUI
    * Script (No GUI for customized usage)
* 3 blocks in the configuration file (example: .rules files in conf/)
    * States: x y state
    * Bonds: M1.x M1.y M2.x M2.y bondTYPE
    * Rules: M1.state M2.state bondTYPE Direction M1'.state M2'.state bondTYPE' Direction'
    (Directions: N, NE, E, SE, S, SW, W, NW) 
    (Bond Types: 0 - null, 1 - rigid, 2 - flexible.)
