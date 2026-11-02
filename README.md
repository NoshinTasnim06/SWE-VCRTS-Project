# SWE-VCRTS-Project

This project aims to manage computational jobs and pool vehicular computational power from stationary vehicles in a parking lot. The software will pool unused resources and act as a cloud server. This will provide users with computational power in exchange for monetary compensation.

### Objective:

The objective is to provide a system that:
* Registers new vehicles in the available resource pool.
* Registers new users who may submit jobs. 
* Collects and stores vehicle and user information.
* Tracks job progress in a visual report accessible to the user. 
* Manages resource allocation and payments.

### Milestone 1: Requirements Specification 
* Project Specification Document: Completed

### Milestone 2: GUI
* Vehicular Cloud Console GUI: Completed.
* User can select whether they are a Vehicle Owner or a Task Client.
* Owner information and vehicle information can be entered through the GUI.
* Client information and job information can be entered through the GUI.
* User information is stored in text files with timestamps.
* The system supports multiple users and entries.

### Milestone 3: UML
* Design Class: Completed.
* Identified necessary classes, associations, multiplicities, attributes, and methods.
* Designed relationhisp between vehicles, owners, clients, jobs, and the VC controller
* Incorporated specific characteristics such as job status, redundancy, checkpoint management into the system desgin.

### Milestone 4: Class Implementation
* Class Implementation: Completed.
* Implemented Java source files for each class defined in the UML Design Class.
* Integrated the Java classes with the existing Vehicular Cloud Console GUI.
* Added a priority field to support priority-based scheduling.
* Calculated Average Waiting Time and Completion Time.
