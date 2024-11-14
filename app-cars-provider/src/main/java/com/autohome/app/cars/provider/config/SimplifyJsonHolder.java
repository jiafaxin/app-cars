package com.autohome.app.cars.provider.config;


public class SimplifyJsonHolder {

    private static ThreadLocal<Boolean> version;
    public static void use(){
        try {
            version = new ThreadLocal<>();
            version.set(true);
        }catch (Exception e){

        }
    }
    public static boolean isUse(){
        try {
            if(version == null){
                return false;
            }
            boolean use = version.get() == null ? false : version.get().booleanValue();
            version.remove();
            return use;
        }catch (Exception e){
            return false;
        }
    }

}
