package com.autohome.app.cars.service.services.enums;


public enum ComputerRoom {
    All(0),
    LangfangDown(1),
    YizhuangDown(2);

    ComputerRoom(int code){
        setCode(code);
    }

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static ComputerRoom convertByValue(int code){
        for (ComputerRoom value : ComputerRoom.values()) {
            if(value.getCode()==code)
                return value;
        }
        return ComputerRoom.All;
    }

    public boolean anyDown(){
        if(this.equals(LangfangDown) || this.equals(YizhuangDown))
            return true;
        return false;
    }

    public boolean langfangDown(){
        return this.equals(LangfangDown);
    }

    public boolean yizhuangDown(){
        return this.equals(YizhuangDown);
    }
}
