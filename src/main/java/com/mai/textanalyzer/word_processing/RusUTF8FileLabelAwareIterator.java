/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mai.textanalyzer.word_processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.NonNull;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.documentiterator.LabelsSource;

/**
 * Подправленная версия FileLabelAwareIterator для работы с русскими текстами в
 * UTF-8
 *
 * @author Sergey
 */
public class RusUTF8FileLabelAwareIterator implements LabelAwareIterator {

    protected List<File> files;
    protected AtomicInteger position = new AtomicInteger(0);
    protected LabelsSource labelsSource;
    private final Map<String, Integer> domentsSizeMap;

    /*
        Please keep this method protected, it's used in tests
     */
    protected RusUTF8FileLabelAwareIterator() {
        domentsSizeMap = new HashMap<>();
    }

    protected RusUTF8FileLabelAwareIterator(@NonNull List<File> files, @NonNull LabelsSource source, Map<String, Integer> domentsSizeMap) {
        this.files = files;
        this.labelsSource = source;
        this.domentsSizeMap = domentsSizeMap;
    }

    @Override
    public boolean hasNextDocument() {
        return position.get() < files.size();
    }

    public int getDocumentsSize(String topic) {
        Integer size = domentsSizeMap.get(topic);
        if (size != null) {
            return size;
        }
        return 0;
    }

    @Override
    public LabelledDocument nextDocument() {
        File fileToRead = files.get(position.getAndIncrement());
        String label = fileToRead.getParentFile().getName();
        try {
            LabelledDocument document = new LabelledDocument();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToRead), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line).append(" ");
            }

            reader.close();

            document.setContent(builder.toString());
            document.addLabel(label);

            try {
                reader.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return document;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        return hasNextDocument();
    }

    @Override
    public LabelledDocument next() {
        return nextDocument();
    }

    @Override
    public void remove() {
        // no-op
    }

    @Override
    public void shutdown() {
        // no-op
    }

    @Override
    public void reset() {
        position.set(0);
    }

    @Override
    public LabelsSource getLabelsSource() {
        return labelsSource;
    }

    public int getSize() {
        return files.size();
    }

    public static class Builder {

        protected List<File> foldersToScan = new ArrayList<>();

        public Builder() {

        }

        /**
         * Root folder for labels -> documents. Each subfolder name will be
         * presented as label, and contents of this folder will be represented
         * as LabelledDocument, with label attached
         *
         * @param folder folder to be scanned for labels and files
         * @return
         */
        public Builder addSourceFolder(@NonNull File folder) {
            foldersToScan.add(folder);
            return this;
        }

        public RusUTF8FileLabelAwareIterator build() {
            // search for all files in all folders provided
            List<File> fileList = new ArrayList<>();
            List<String> labels = new ArrayList<>();
            Map<String, Integer> domentsSizeMap = new HashMap<>();

            for (File file : foldersToScan) {
                if (!file.isDirectory()) {
                    continue;
                }

                File[] files = file.listFiles();
                if (files == null || files.length == 0) {
                    continue;
                }

                for (File fileLabel : files) {
                    if (!fileLabel.isDirectory()) {
                        continue;
                    }

                    if (!labels.contains(fileLabel.getName())) {
                        labels.add(fileLabel.getName());
                    }

                    File[] docs = fileLabel.listFiles();
                    if (docs == null || docs.length == 0) {
                        continue;
                    }

                    domentsSizeMap.put(fileLabel.getName(), docs.length);

                    for (File fileDoc : docs) {
                        if (!fileDoc.isDirectory()) {
                            fileList.add(fileDoc);
                        }
                    }
                }
            }
            LabelsSource source = new LabelsSource(labels);
            RusUTF8FileLabelAwareIterator iterator = new RusUTF8FileLabelAwareIterator(fileList, source, domentsSizeMap);

            return iterator;
        }
    }
}
