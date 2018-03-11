package com.geotask.myapplication.Controllers.LocalFilesOps;

import com.geotask.myapplication.DataClasses.GTData;

public final class SaveFileCommand implements Command {

    private GTData dataFile;

    public SaveFileCommand(final GTData dataFile) {
        this.dataFile = dataFile;
    }

    @Override
    public void execute() {
    }
}
