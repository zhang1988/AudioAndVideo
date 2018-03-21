package com.example.commonlib.safe;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.commonlib.util.LogUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CloseUtil {

    /**
     * 关闭给定的输入流.
     *
     * @param inStream
     */
    public static void close(InputStream inStream) {
        if (inStream != null) {
            try {
                inStream.close();
            } catch (IOException e) {
                LogUtils.print("CloseUtil", e.getMessage());
            }
        }
    }

    /**
     * 关闭给定可关闭流.
     *
     * @param stream
     */
    public static void close(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Throwable e) {
                LogUtils.print("CloseUtil", e.getMessage());
            }
        }
    }

    /**
     * 关闭给定的输出流.
     *
     * @param outStream
     */
    public static void close(OutputStream outStream) {
        if (outStream != null) {
            try {
                outStream.close();
            } catch (IOException e) {
                LogUtils.print("CloseUtil", e.getMessage());
            }
        }
    }

    /**
     * 关闭给定的输出流.
     *
     * @param writer
     */
    public static void close(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                LogUtils.print("CloseUtil", e.getMessage());
            }
        }
    }

    /**
     * 关闭给定的Socket.
     *
     * @param socket 给定的Socket
     */
    public static void close(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                LogUtils.print("CloseUtil", e.getMessage());
            }
        }
    }

    /**
     * 关闭reader
     *
     * @param reader
     */
    public static void close(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                LogUtils.print("CloseUtil", e.getMessage());
            }
        }
    }

    /**
     * close connection
     *
     * @param conn
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                LogUtils.print("CloseUtil", e.getMessage());
            }
        }
    }

    /**
     * @param ps
     */
    public static void close(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (Exception e) {
                LogUtils.print("CloseUtil", e.getMessage());
            }
        }
    }

    /**
     * @param rs
     */
    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                LogUtils.print("CloseUtil", e.getMessage());
            }
        }
    }

    public static void close(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                LogUtils.print("CloseUtil", e.getMessage());
            }
        }
    }

    public static void close(Cursor c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception e) {
                LogUtils.print("CloseUtil", e.getMessage());
            }
        }
    }

    public static void close(SQLiteDatabase db) {
        if (db != null) {
            try {
                db.close();
            } catch (Exception e) {
                LogUtils.print("CloseUtil", e.getMessage());
            }
        }
    }

    public static void close(SQLiteStatement sqLiteStatement) {
        if (sqLiteStatement != null) {
            try {
                sqLiteStatement.close();
            } catch (Exception e) {
                LogUtils.print("CloseUtil", e.getMessage());
            }
        }
    }

    public static void close(HttpURLConnection conn) {
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (Exception e) {
                LogUtils.print("CloseUtil", e.getMessage());
            }
        }
    }
}
