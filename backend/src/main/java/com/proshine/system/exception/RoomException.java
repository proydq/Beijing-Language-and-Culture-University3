package com.proshine.system.exception;

/**
 * 房间管理相关异常
 * 
 * @author system
 * @date 2024-01-01
 */
public class RoomException extends RuntimeException {

    public RoomException(String message) {
        super(message);
    }

    public RoomException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 房间不存在异常
     */
    public static class RoomNotFoundException extends RoomException {
        public RoomNotFoundException(String roomId) {
            super("房间不存在，ID: " + roomId);
        }
    }

    /**
     * 房间重复异常
     */
    public static class RoomDuplicateException extends RoomException {
        public RoomDuplicateException(String roomName, String roomNo) {
            super(String.format("房屋名称[%s]和房间号码[%s]组合已存在", roomName, roomNo));
        }
    }

    /**
     * 房间数据验证异常
     */
    public static class RoomValidationException extends RoomException {
        public RoomValidationException(String message) {
            super("房间数据验证失败: " + message);
        }
    }

    /**
     * 房间导入异常
     */
    public static class RoomImportException extends RoomException {
        public RoomImportException(String message) {
            super("房间数据导入失败: " + message);
        }

        public RoomImportException(String message, Throwable cause) {
            super("房间数据导入失败: " + message, cause);
        }
    }

    /**
     * 房间导出异常
     */
    public static class RoomExportException extends RoomException {
        public RoomExportException(String message) {
            super("房间数据导出失败: " + message);
        }

        public RoomExportException(String message, Throwable cause) {
            super("房间数据导出失败: " + message, cause);
        }
    }
}