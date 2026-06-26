# Asset Hierarchy Candidate Tree

This is an asset-based candidate hierarchy, not a final feature model.

## Main tree

- asset1 ControlUnit.java [type=common, owners=6/6]
  - asset9 ITriggerListener.java [type=variable, owners=5/6] -- parent-child, confidence=high
  - asset10 Request.java [type=variable, owners=5/6] -- parent-child, confidence=high
- asset2 Elevator.java [type=common, owners=6/6]
- asset3 MainWindow.java [type=common, owners=6/6]
  - asset11 FloorChooseDialog.java [type=variable, owners=2/6] -- parent-child, confidence=high
- asset4 ITickListener.java [type=common, owners=6/6]
- asset5 SimulationUnit.java [type=common, owners=6/6]
- asset6 JBackgroundPanel.java [type=common, owners=6/6]
- asset7 FloorComposite.java [type=common, owners=6/6]
- asset8 ElevatorState.java [type=common, owners=6/6]

## Alternative parents

- asset11 FloorChooseDialog.java
  - asset1 ControlUnit.java -- parent-child, confidence=medium
  - asset2 Elevator.java -- parent-child, confidence=medium
  - asset4 ITickListener.java -- parent-child, confidence=medium
  - asset5 SimulationUnit.java -- parent-child, confidence=medium
  - asset6 JBackgroundPanel.java -- parent-child, confidence=high
  - asset7 FloorComposite.java -- parent-child, confidence=high
  - asset8 ElevatorState.java -- parent-child, confidence=medium
  - asset9 ITriggerListener.java -- parent-child, confidence=medium
  - asset10 Request.java -- parent-child, confidence=medium
- asset9 ITriggerListener.java
  - asset2 Elevator.java -- parent-child, confidence=medium
  - asset3 MainWindow.java -- parent-child, confidence=medium
  - asset4 ITickListener.java -- parent-child, confidence=high
  - asset5 SimulationUnit.java -- parent-child, confidence=medium
  - asset6 JBackgroundPanel.java -- parent-child, confidence=medium
  - asset7 FloorComposite.java -- parent-child, confidence=medium
  - asset8 ElevatorState.java -- parent-child, confidence=medium
- asset10 Request.java
  - asset2 Elevator.java -- parent-child, confidence=medium
  - asset3 MainWindow.java -- parent-child, confidence=medium
  - asset4 ITickListener.java -- parent-child, confidence=high
  - asset5 SimulationUnit.java -- parent-child, confidence=medium
  - asset6 JBackgroundPanel.java -- parent-child, confidence=medium
  - asset7 FloorComposite.java -- parent-child, confidence=medium
  - asset8 ElevatorState.java -- parent-child, confidence=medium
