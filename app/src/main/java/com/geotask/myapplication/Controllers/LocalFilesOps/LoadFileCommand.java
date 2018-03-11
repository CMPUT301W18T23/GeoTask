package com.geotask.myapplication.Controllers.LocalFilesOps;

import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import java.lang.reflect.Type;

public class LoadFileCommand implements Command {

    private String filename;
    private Type type;
    private GTData data;

    public LoadFileCommand(final String filename, final Type type) {
        this.filename = filename;
        this.type = type;
        if(type.equals(Bid.class)) {
            data = new Bid();
        } else if (type.equals(Task.class)) {
            data = new Task();
        } else if (type.equals(User.class)) {
            data = new User();
        }
    }

    @Override
    public void execute() {
        data.loadFile();
    }
}
