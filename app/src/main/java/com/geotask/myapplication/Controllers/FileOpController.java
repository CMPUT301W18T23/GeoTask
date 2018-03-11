package com.geotask.myapplication.Controllers;

import com.geotask.myapplication.Controllers.LocalFilesOps.Command;

public class FileOpController {

    public void DoToFile(final Command command){
        command.execute();
    }
}
