package com.aplication.assistug.helper;

import android.content.Context;
import android.util.Log;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ExcelHelper {

    public static final String Tablename = "MyTable1";
    public static final String id = "_id";// 0 integer
    public static final String Cedula = "Cedula";// 1 text(String)

    public static void insertExcelToSqlite(String uidcurso,Context context, Sheet sheet,  String nombrecurso) {

        for(Row r : sheet) {
             r.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);

             String c = r.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

            try {
                if (FirebaseHelper.insert(c, context, uidcurso, nombrecurso)) {
                    return;
                }
            } catch (Exception ex) {
                Log.d("Exception in importing", ex.getMessage().toString());
            }
            }

    }

}