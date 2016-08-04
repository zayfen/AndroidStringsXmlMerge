package com.ken;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        File fileSourceXml = new File(Constants.SOURCE_PATH);
        File fileTargetXml = new File(Constants.TARGET_PATH);
        StringsMergeUtil.mergeStringsXml(fileSourceXml, fileTargetXml);
    }
}
