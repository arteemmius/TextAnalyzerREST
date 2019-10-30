/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mai.textanalyzer.ui.file_upload;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.Logger;

/**
 *
 * @author s.belov
 */
public class DocUploader implements Receiver, SucceededListener {

    private static final long serialVersionUID = -127675472190466761L;
    private static final Logger LOG = Logger.getLogger(DocUploader.class);

    private ByteArrayOutputStream outputBuffer = null;

    private String mimeType;

    private String fileName;

    public String getDoc() throws UnsupportedEncodingException {
        return new String(outputBuffer.toByteArray(), "UTF-8");
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        this.fileName = filename;
        this.mimeType = mimeType;
        outputBuffer = new ByteArrayOutputStream();
        return outputBuffer;
    }

    @Override
    public void uploadSucceeded(Upload.SucceededEvent event) {

    }

    public String getMimeType() {
        return mimeType;
    }

    public String getFileName() {
        return fileName;
    }

}
