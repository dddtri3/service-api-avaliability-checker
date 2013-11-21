package com.drfirst.mis.service_api_avaliability_checker;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 
 * @author Allen Chiao
 *
 */

public class FileNameFilter implements FilenameFilter {

    private String fileExtension;
    public FileNameFilter(String fileExtension) {
       this.fileExtension = fileExtension;
    }

    @Override
    public boolean accept(File directory, String fileName) {
        return (fileName.endsWith(this.fileExtension));
    }

}
