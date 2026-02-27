package com.template.util;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {

    // Writes one CSV row safely
    public static void writeRow(BufferedWriter bw, List<String> values) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(escape(values.get(i)));
        }
        sb.append("\n");
        bw.write(sb.toString());
    }

    // Escapes values for CSV (quotes, commas, newlines)
    public static String escape(String v) {
        if (v == null) return "";
        String s = v;
        boolean mustQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        if (s.contains("\"")) s = s.replace("\"", "\"\"");
        return mustQuote ? ("\"" + s + "\"") : s;
    }

    // Parses a CSV line (supports quotes)
    public static List<String> parseLine(String line) {

        List<String> out = new ArrayList<>();
        if (line == null) return out;

        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // double quote inside quoted string
                    cur.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }

        out.add(cur.toString());
        return out;
    }
}