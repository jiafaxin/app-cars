package com.autohome.car.tools;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CompareJson {

    List<String> errors = new ArrayList<>();


    List<String> exFields = new ArrayList<>();

    List<String> exStrs = new ArrayList<>();

    String rootNode = "";

    public CompareJson exclude(String... exfs) {
        exFields = new ArrayList<>();
        if (exfs == null) {
            return this;
        }
        for (String exf : exfs) {
            exFields.add(exf);
        }
        return this;
    }


    public CompareJson excludeResult(String... exs) {
        exStrs = new ArrayList<>();
        if (exs == null) {
            return this;
        }
        for (String exf : exs) {
            exStrs.add(exf);
        }
        return this;
    }

    public CompareJson setRootNode(String nodePath) {
        rootNode = nodePath;
        return this;
    }

    public CompletableFuture compareUrlAsync(String url1, String url2) {
        return compareUrlAsync(url1, url2, "", "");
    }


    static List<String> urls = new ArrayList<>();


    public CompletableFuture compareUrlAsync(String url1, String url2, String host1, String host2) {
        return HttpClient.getString(url1, "UTF-8", host1).thenCombineAsync(HttpClient.getString(url2, "UTF-8", host2), (a, b) -> {
            try {
                compare(a, b);
            } catch (Exception e) {
                System.out.println("对比报错了========================================================================================================");
                System.out.println(url1);
                System.out.println(url2);
                System.out.println(e);
                return null;
            }

            if (errors.size() > 0) {
//                if (!urls.contains(url1)) {
//                    urls.add(url1);
//                    System.out.println(url1);
//                }
                boolean realError = false;
                for (String error : errors) {
                    if (!realError(error))
                        continue;

                    realError = true;
                    System.out.println(error);
                }
                if (realError) {
                    System.out.println("A: " + url1);
                    System.out.println("B: " + url2);
                    System.out.println("==================================================================================================================");
                }
            }
            return null;
        }).exceptionally(e -> {
            System.out.println("error:" + url2);
            return null;
        });
    }

    boolean realError(String error) {
        for (String exStr : exStrs) {
            if (error.indexOf(exStr) >= 0) {
                return false;
            }
        }
        return true;
    }


    public void compare(String oldJson, String newJson) {
        JSONObject oldObj = new JSONObject(oldJson);
        JSONObject newObj = new JSONObject(newJson);

        if (StringUtils.isNotBlank(rootNode)) {
            compare("root", getByPath(oldObj, rootNode), getByPath(newObj, rootNode));
            return;
        }

        compare("root", oldObj, newObj);
    }


    public Object getByPath(Object obj, String path) {
        if (obj == null) {
            return obj;
        }
        //只支持jsonobject节点
        if (!(obj instanceof JSONObject)) {
            return obj;
        }
        int index = path.indexOf(".");
        if (index < 0)
            return ((JSONObject) obj).get(path);
        if (!((JSONObject) obj).keySet().contains(path.substring(0, index))) {
            return JSONObject.NULL;
        }
        Object newObj = ((JSONObject) obj).get(path.substring(0, index));
        return getByPath(newObj, path.substring(index + 1));
    }


    public void compare(String key, Object oldObj, Object newObj) {
        if (exFields.contains(key) || exFields.contains(key.replaceAll("\\[\\d+\\]", "[*]")))
            return;

        if (oldObj == null && newObj == null) {
            return;
        }

        if (oldObj.equals(JSONObject.NULL) == true && newObj.equals(JSONObject.NULL) == true) {
            return;
        }

        if (oldObj.equals(JSONObject.NULL) && !newObj.equals(JSONObject.NULL)) {
            if (newObj instanceof JSONArray) {
                if (((JSONArray) newObj).length() == 0) {
                    return;
                }
            }

            if (newObj instanceof String) {
                if (((String) newObj).equals("")) {
                    return;
                }
            }

            errors.add(key + " : A 结果为null，B 结果不为null");
            return;
        }
        if ((!oldObj.equals(JSONObject.NULL)) && (newObj.equals(JSONObject.NULL) || newObj.equals(JSONObject.NULL) == true)) {
            errors.add(key + " : A 结果不为null，B 结果为null");
            return;
        }

        if (oldObj instanceof JSONArray) {
            if (!(newObj instanceof JSONArray)) {
                errors.add(key + " : A 为JSONArray，B 不是");
            }
            JSONArray oldArray = (JSONArray) oldObj;
            JSONArray newArray = (JSONArray) newObj;
            int oldLen = oldArray.length();
            for (int i = 0; i < oldLen; i++) {
                //过滤数组中的null
                if (oldArray.isNull(i)) {
                    i--;
                    oldLen--;
                    continue;
                }
                if (newArray.length() < i + 1) {
                    errors.add(key + "[" + i + "] A 存在，B 不存在");
                    continue;
                }
                compare(key + "[" + i + "]", oldArray.get(i), newArray.get(i));
            }

            return;
        }
        if (oldObj instanceof JSONObject) {
            if (!(newObj instanceof JSONObject)) {
                errors.add(key + " : A 为JSONObject，B 不是");
                return;
            }

            JSONObject oldObjC = (JSONObject) oldObj;
            JSONObject newObjC = (JSONObject) newObj;

            for (String s : oldObjC.keySet()) {
                String newKey = key + "." + s;
                if (exFields.contains(newKey) || exFields.contains(newKey.replaceAll("\\[\\d+\\]", "[*]"))) {
                    continue;
                }
                if (!newObjC.keySet().contains(s)) {
                    errors.add(newKey + " : A 存在，B 不存在" + " [" + oldObjC.get(s) + "] => [" + "]");
                    continue;
                }
                Object oldValue = oldObjC.get(s);
                Object newValue = newObjC.get(s);
                compare(newKey, oldValue, newValue);
            }
            return;
        }
        if (oldObj.equals(newObj)) {
            return;
        } else if (oldObj.equals(null) && newObj.equals("")) {
            return;
        } else if (isDouble(oldObj) && isDouble(newObj) && Double.parseDouble(oldObj.toString()) == Double.parseDouble(newObj.toString())) {
            return;
        }
        errors.add(key + ": A、B值不一致 [" + oldObj.toString() + "] => [" + newObj.toString() + "]");
    }

    public boolean isDouble(Object v) {
        try {
            Double.parseDouble(v.toString());
            return true;
        } catch (NumberFormatException ee) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
