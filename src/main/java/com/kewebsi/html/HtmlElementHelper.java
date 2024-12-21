package com.kewebsi.html;

import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.util.CommonUtils;

import java.util.HashMap;

public class HtmlElementHelper {

    public static void checkForDupicateIds(HtmlElement root) {
        HashMap<String, HtmlElement> ids = new HashMap<>();
        checkForDupicateIds(root, ids);
    }

    public static void checkForDupicateIds(HtmlElement recursiveRoot, HashMap<String, HtmlElement> ids) {
        String currentRootId = recursiveRoot.getId();
        if (CommonUtils.hasInfo(currentRootId)) {
            if (ids.containsKey(currentRootId)) {
                throw new CodingErrorException("Duplicate key " + currentRootId + " found for " + recursiveRoot);
            }
            ids.put(currentRootId, recursiveRoot);
        }

        if (recursiveRoot.getChildren() != null) {
            for (var childRun : recursiveRoot.getChildren()) {
                checkForDupicateIds(childRun, ids);
            }
        }
    }


}
