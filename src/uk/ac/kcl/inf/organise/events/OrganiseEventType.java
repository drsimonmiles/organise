package uk.ac.kcl.inf.organise.events;

public enum OrganiseEventType {
    opening, closing,
    projectAdded, projectDeleted, projectRenamed,
    taskAdded, taskCompleted, taskDeleted, taskOrderChanged,
    taskTextChanged, taskPriorityChanged, taskAllocatedChanged, taskNotesChanged,
    triggerAdded, triggerDeleted,
    taskChildAdded, taskChildRemoved,
    taskInFocus, anchorChanged,
    keyPressed, keyTyped, keyReleased
}
