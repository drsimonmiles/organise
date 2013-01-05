package uk.ac.kcl.inf.organise.data;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import uk.ac.kcl.inf.organise.events.EventBus;
import uk.ac.kcl.inf.organise.events.OrganiseEventType;
import uk.ac.kcl.inf.organise.events.TaskDeleteEdit;
import uk.ac.kcl.inf.organise.rules.Rule;

public class Database {
    public static final String EVENTS_PROJECT = "Events";
    private final EventBus _bus;
    private final List<Task> _tasks;
    private final List<Rule> _rules;
    
    public Database (EventBus bus) {
        _tasks = new LinkedList<> ();
        _rules = new LinkedList<> ();
        _bus = bus;
    }
    
    public void addRule (Rule rule) {
        _rules.add (rule);
    }
    
    public void addTask (Task newTask) {
        if (!newTask.isTriggerOnly () && !existingProject (newTask.getProject ())) {
            _tasks.add (newTask);
            _bus.event (OrganiseEventType.projectAdded).project (newTask.getProject ()).fire ();
        } else {
            _tasks.add (newTask);
        }
        _bus.event (OrganiseEventType.taskAdded).task (newTask).project (newTask.getProject ()).fire ();
    }
    
    public void addTask (Task newTask, Task followTask) {
        int index = 0;
        
        if (followTask != null) {
            index = _tasks.indexOf (followTask) + 1;
        }
        if (!existingProject (newTask.getProject ())) {
            _tasks.add (index, newTask);
            _bus.event (OrganiseEventType.projectAdded).project (newTask.getProject ()).fire ();
        } else {
            _tasks.add (index, newTask);
        }
        _bus.event (OrganiseEventType.taskAdded).task (newTask).project (newTask.getProject ()).fire ();
    }
    
    public void clear () {
        for (Task task : getTasks ()) {
            deleteTask (task);
        }
    }
    
    public void completeTask (Task task) {
        _bus.event (OrganiseEventType.taskCompleted).task (task).fire ();
        deleteTask (task);
    }

    private void deleteProjectIfNecessary (String name) {
        for (Task task : _tasks) {
            if (task.getProject ().equals (name)) {
                return;
            }
        }
        _bus.event (OrganiseEventType.projectDeleted).project (name).fire ();
    }
    
    public void deleteRule (Rule rule) {
        _rules.remove (rule);
    }
    
    public void deleteTask (Task task) {
        int index = _tasks.indexOf (task);
        
        if (index == 0) {
            _bus._undo.addEdit (new TaskDeleteEdit (this, task, null));
        } else {
            _bus._undo.addEdit (new TaskDeleteEdit (this, task, _tasks.get (index - 1)));
        }
        _tasks.remove (task);
        _bus.event (OrganiseEventType.taskDeleted).task (task).fire ();
        deleteProjectIfNecessary (task.getProject ());
    }
    
    public void deleteTasks (Collection<Task> tasks) {
        for (Task task : tasks) {
            deleteTask (task);
        }
    }
    
    private boolean existingProject (String name) {
        for (Task task : _tasks) {
            if (task.getProject ().equals (name)) {
                return true;
            }
        }
        return false;
    }
    
    public List<String> getProjects () {
        List<String> projects = new LinkedList<> ();
        
        for (Task task : _tasks) {
            if (!projects.contains (task.getProject ())) {
                projects.add (task.getProject ());
            }
        }
        Collections.sort (projects);
        
        return projects;
    }
    
    public List<Task> getPriorityTasks (Priority priority) {
        List<Task> found = new LinkedList<> ();
        
        for (Task task : _tasks) {
            if (task.getPriority ().equals (priority)) {
                found.add (task);
            }
        }
        
        return found;
    }
    
    public List<Task> getProjectTasks (String project) {
        List<Task> found = new LinkedList<> ();
        
        for (Task task : _tasks) {
            if (task.getProject ().equals (project)) {
                found.add (task);
            }
        }
        
        return found;
    }
    
    public List<Task> getTasks () {
        return _tasks;
    }
    
    public List<Rule> getTaskRules (Task task) {
        List<Rule> rules = new LinkedList<> ();
        
        for (Rule rule : _rules) {
            if (rule.getOwner () == task) {
                rules.add (rule);
            }
        }
        
        return rules;
    }
    
    public boolean isTrigger (Task triggering) {
        for (Task task : _tasks) {
            for (Trigger trigger : task.getTriggers ()) {
                if (trigger.getTriggeringTasks ().contains (triggering)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void moveTaskAfter (Task task, Task afterTask) {
        int origin = _tasks.indexOf (task);
        int destination = _tasks.indexOf (afterTask) + 1;
        
        if (origin < destination) {
            _tasks.add (destination, task);
            _tasks.remove (task);
        } else {
            _tasks.remove (task);
            _tasks.add (destination, task);
        }
        _bus.event (OrganiseEventType.taskOrderChanged).task (task).fire ();
    }
    
    public void renameProject (String from, String to) {
        for (Task task : _tasks) {
            if (task.getProject ().equals (from)) {
                task.setProject (to);
            }
        }
        _bus.event (OrganiseEventType.projectRenamed).project (to).prior (from).fire ();
    }
}
