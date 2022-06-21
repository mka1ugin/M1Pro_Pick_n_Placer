package com.dobot.api;

import java.io.IOException;
import java.net.Socket;

public class Dashboard {
    private Socket socketClient = new Socket();

    private static String SEND_ERROR = ":send error";

    private String ip = "";

    private Double[] ToolVectorActual = { 0.0, 0.0, 0.0, 0.0 };
    private Double[] QActual = { 0.0, 0.0, 0.0, 0.0 };

    public boolean isConnected() {

        return this.socketClient.isConnected();

    }

    public boolean connect(String ip, int port) {
        boolean ok = false;
        try {
            socketClient = new Socket(ip, port);
            this.ip = ip;
            Logger.instance.log("Dashboard connect success");
            ok = true;
        } catch (Exception e) {
            // Logger.instance.log("Connect failed:" + e.getMessage());
            System.out.println("Connect failed:" + e.getMessage());
        }
        return ok;
    }

    public void disconnect() {
        if (!socketClient.isClosed()) {
            try {
                socketClient.shutdownOutput();
                socketClient.shutdownInput();
                socketClient.close();
                Logger.instance.log("Dashboard closed");
            } catch (Exception e) {
                Logger.instance.log("Dashboard Close Socket Exception:" + e.getMessage());
            }
        } else {
            Logger.instance.log("this ip is not connected");
        }
    }

    public String DigitalOutputs(int index, boolean status) {
        if (socketClient.isClosed()) {
            Logger.instance.log("device does not connected!!!");
            return "device does not connected!!!";
        }
        int statusInt = status ? 1 : 0;
        String str = "DO(" + index + "," + statusInt + ")";
        if (!sendData(str)) {
            return str + ":send error";
        }

        //return waitReply(5000);
        return "";
    }

    public String clearError() {
        if (socketClient.isClosed()) {
            Logger.instance.log("device does not connected!!!");
            return "device does not connected!!!";
        }
        String str = "ClearError()";
        if (!sendData(str)) {
            return str + SEND_ERROR;
        }

        //return waitReply(5000);
        return "";
    }

    public String PowerOn() {
        if (socketClient.isClosed()) {
            Logger.instance.log("device does not connected!!!");
            return "device does not connected!!!";
        }
        String str = "PowerOn()";
        if (!sendData(str)) {
            return str + SEND_ERROR;
        }

        return waitReply(15000);
    }

    public String PowerOff() {
        return emergencyStop();
    }

    public String emergencyStop() {
        if (socketClient.isClosed()) {
            Logger.instance.log("device does not connected!!!");
            return "device does not connected!!!";
        }
        String str = "EmergencyStop()";
        if (!sendData(str)) {
            return str + SEND_ERROR;
        }
        return waitReply(15000);
    }

    public String enableRobot() {
        if (socketClient.isClosed()) {
            Logger.instance.log("device does not connected!!!");
            return "device does not connected!!!";
        }

        String str = "EnableRobot()";
        if (!sendData(str)) {
            return str + SEND_ERROR;
        }

        return waitReply(20000);
    }

    public String disableRobot() {
        if (socketClient.isClosed()) {
            Logger.instance.log("device does not connected!!!");
            return "device does not connected!!!";
        }

        String str = "DisableRobot()";
        if (!sendData(str)) {
            return str + SEND_ERROR;
        }

        return waitReply(20000);
    }

    public String resetRobot() {
        if (socketClient.isClosed()) {
            Logger.instance.log("device does not connected!!!");
            return "device does not connected!!!";
        }

        String str = "ResetRobot()";
        if (!sendData(str)) {
            return str + SEND_ERROR;
        }

        return waitReply(20000);
    }

    public String speedFactor(int ratio) {
        if (socketClient.isClosed()) {
            Logger.instance.log("device does not connected!!!");
            return "device does not connected!!!";
        }
        String str = "SpeedFactor(" + ratio + ")";
        if (!sendData(str)) {
            return str + SEND_ERROR;
        }
        //return readRaw(5000);
        return "";
    }

    public String speedJ(int ratio) {
        if (socketClient.isClosed()) {
            Logger.instance.log("device does not connected!!!");
            return "device does not connected!!!";
        }
        String str = "SpeedJ(" + ratio + ")";
        if (!sendData(str)) {
            return str + SEND_ERROR;
        }
        //return waitReply(5000);
        return "";
    }

    public String speedL(int ratio) {
        if (socketClient.isClosed()) {
            Logger.instance.log("device does not connected!!!");
            return "device does not connected!!!";
        }
        String str = "SpeedL(" + ratio + ")";
        if (!sendData(str)) {
            return str + SEND_ERROR;
        }
        //return waitReply(5000);
        return "";
    }

    public String AccJ(int ratio) {
        if (socketClient.isClosed()) {
            Logger.instance.log("device does not connected!!!");
            return "device does not connected!!!";
        }
        String str = "AccJ(" + ratio + ")";
        if (!sendData(str)) {
            return str + SEND_ERROR;
        }
        //return waitReply(5000);
        return "";
    }

    public String AccL(int ratio) {
        if (socketClient.isClosed()) {
            Logger.instance.log("device does not connected!!!");
            return "device does not connected!!!";
        }
        String str = "AccL(" + ratio + ")";
        if (!sendData(str)) {
            return str + SEND_ERROR;
        }
        //return waitReply(5000);
        return "";
    }

    public boolean sendData(String str) {
        try {
            Logger.instance.log("Send to:" + this.ip + ":" + socketClient.getPort() + ":" + str);
            socketClient.getOutputStream().write((str).getBytes());
        } catch (IOException e) {
            Logger.instance.log("Exception:" + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean sendDataSilent(String str) {
        try {
            socketClient.getOutputStream().write((str).getBytes());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public String waitReply(int timeout) {
        String reply = "";
        try {
            if (socketClient.getSoTimeout() != timeout) {
                socketClient.setSoTimeout(timeout);
            }
            byte[] buffer = new byte[1024]; // 缓冲
            int len = socketClient.getInputStream().read(buffer);// 每次读取的长度（正常情况下是1024，最后一次可能不是1024，如果传输结束，返回-1）
            reply = new String(buffer, 0, len, "UTF-8");
            ErrorInfoHelper.getInstance().parseResult(reply);
            Logger.instance.log("Receive from:" + this.ip + ":" + socketClient.getPort() + ":" + reply);

        } catch (IOException e) {
            Logger.instance.log("Exception:" + e.getMessage());
            return reply;
        }
        return reply;
    }

    public boolean getPose() {

        boolean readOk = false;

        if (socketClient.isClosed()) {
            return readOk;
        }

        String str = "GetPose()";
        if (!sendDataSilent(str)) {
            return readOk;
        }

        String responce = readRaw(15000);

        parseSixDoubles(responce, this.ToolVectorActual);

        readOk = true;

        return readOk;
    }

    public boolean getAngle() {

        boolean readOk = false;

        if (socketClient.isClosed()) {
            return readOk;
        }

        String str = "GetAngle()";
        if (!sendDataSilent(str)) {
            return readOk;
        }

        String responce = readRaw(15000);

        parseSixDoubles(responce, this.QActual);

        readOk = true;

        return readOk;
    }

    public Double[] ToolVectorActual() {
        return this.ToolVectorActual;
    }

    public Double[] QActual() {
        return this.QActual;
    }

    public String readRaw(int timeout) {
        String reply = "";
        try {
            if (socketClient.getSoTimeout() != timeout) {
                socketClient.setSoTimeout(timeout);
            }
            byte[] buffer = new byte[1024]; // buffer
            int len = socketClient.getInputStream().read(buffer);// 每次读取的长度（正常情况下是1024，最后一次可能不是1024，如果传输结束，返回-1）
            reply = new String(buffer, 0, len, "UTF-8");
        } catch (IOException e) {
            Logger.instance.log("Exception:" + e.getMessage());
            return reply;
        }
        return reply;
    }

    private boolean parseSixDoubles(String data, Double[] dataParsed) {
        boolean result = false;

        int iBegPos = data.indexOf('{');
        if (iBegPos < 0) {
            return result;
        }
        int iEndPos = data.indexOf('}',
                iBegPos + 1);
        if (iEndPos < 0) {
            return result;
        }

        if (iEndPos - iBegPos <= 1) {
            return result;
        }

        data = data.substring(iBegPos + 1, iEndPos);

        String[] six_doubles = data.split(",");

        if (six_doubles.length != 6) {
            return result;
        }

        for (int i = 0; i < dataParsed.length; i++) {
            dataParsed[i] = Double.parseDouble(six_doubles[i]);
        }

        result = true;

        return result;
    }

    private int parseOneInt(String data) {

        int result = -999;

        int iBegPos = data.indexOf('{');
        if (iBegPos < 0) {
            return result;
        }
        int iEndPos = data.indexOf('}',
                iBegPos + 1);
        if (iEndPos < 0) {
            return result;
        }

        if (iEndPos - iBegPos <= 1) {
            return result;
        }

        data = data.substring(iBegPos + 1, iEndPos);

        if (data.contains(".") || data.contains(",")) {
            return result;
        }

        result = Integer.parseInt(data);

        return result;
    }

    public String robotMode() {

        String result = "";

        if (socketClient.isClosed()) {
            return result;
        }

        String str = "RobotMode()";
        if (!sendDataSilent(str)) {
            return result;
        }

        String responce = "";

        while (!responce.contains("RobotMode()")) {
            responce = readRaw(15000);
        }

        int modeInt = parseOneInt(responce);

        switch (modeInt) {
            case -1:
                return "NO_CONTROLLER";
            case 0:
                return "NO_CONNECTED";
            case 1:
                return "ROBOT_MODE_INIT";
            case 2:
                return "ROBOT_MODE_BRAKE_OPEN";
            case 3:
                return "ROBOT_RESERVED";
            case 4:
                return "ROBOT_MODE_DISABLED";
            case 5:
                return "ROBOT_MODE_ENABLE";
            case 6:
                return "ROBOT_MODE_BACKDRIVE";
            case 7:
                return "ROBOT_MODE_RUNNING";
            case 8:
                return "ROBOT_MODE_RECORDING";
            case 9:
                return "ROBOT_MODE_ERROR";
            case 10:
                return "ROBOT_MODE_PAUSE";
            case 11:
                return "ROBOT_MODE_JOG";
        }

        return String.format("UNKNOW：RobotMode=" + responce + "");
    }

    public String robotModeRU() {

        if (socketClient.isClosed()) {
            return null;
        }

        String str = "RobotMode()";
        if (!sendDataSilent(str)) {
            return null;
        }

        String responce = readRaw(15000);

        int modeInt = parseOneInt(responce);

        switch (modeInt) {
            case -1:
                return new String("Нет контроллера");
            case 0:
                return new String("Не подключен");
            case 1:
                return new String("Инициализация");
            case 2:
                return new String("Тормоз снят");
            case 3:
                return new String("Зарезервировано");
            case 4:
                return new String("Неактивен");
            case 5:
                return new String("Активен   ");
            case 6:
                return new String("Обнаружено препятствие");
            case 7:
                return new String("В движении");
            case 8:
                return new String("Запись траектории");
            case 9:
                return new String("Ошибка");
            case 10:
                return new String("Пауза");
            case 11:
                return new String("Холостой ход");
            default:
                return null;
        }
    }
}