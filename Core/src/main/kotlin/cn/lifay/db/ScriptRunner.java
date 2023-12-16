package cn.lifay.db;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptRunner {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String DEFAULT_DELIMITER = ";";
    private static final Pattern DELIMITER_PATTERN = Pattern.compile("^\\s*((--)|(//))?\\s*(//)?\\s*@DELIMITER\\s+([^\\s]+)", 2);
    private final Connection connection;
    private boolean stopOnError = true;
    private boolean throwWarning;
    private boolean autoCommit = true;
    private boolean sendFullScript = false;
    private boolean removeCRs;
    private boolean escapeProcessing = true;
    private PrintWriter logWriter;
    private PrintWriter errorLogWriter;
    private String delimiter;
    private boolean fullLineDelimiter;

    public ScriptRunner(Connection connection) {
        this.logWriter = new PrintWriter(System.out);
        this.errorLogWriter = new PrintWriter(System.err);
        this.delimiter = ";";
        this.connection = connection;
    }

    public void setStopOnError(boolean stopOnError) {
        this.stopOnError = stopOnError;
    }

    public void setThrowWarning(boolean throwWarning) {
        this.throwWarning = throwWarning;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public void setSendFullScript(boolean sendFullScript) {
        this.sendFullScript = sendFullScript;
    }

    public void setRemoveCRs(boolean removeCRs) {
        this.removeCRs = removeCRs;
    }

    public void setEscapeProcessing(boolean escapeProcessing) {
        this.escapeProcessing = escapeProcessing;
    }

    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    public void setErrorLogWriter(PrintWriter errorLogWriter) {
        this.errorLogWriter = errorLogWriter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setFullLineDelimiter(boolean fullLineDelimiter) {
        this.fullLineDelimiter = fullLineDelimiter;
    }

    public void runScript(Reader reader) throws SQLException {
        this.setAutoCommit();

        try {
            if (this.sendFullScript) {
                this.executeFullScript(reader);
            } else {
                this.executeLineByLine(reader);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.rollbackConnection();
        }

    }

    private void executeFullScript(Reader reader) throws SQLException {
        StringBuilder script = new StringBuilder();

        String line;
        try {
            BufferedReader lineReader = new BufferedReader(reader);

            while ((line = lineReader.readLine()) != null) {
                String trim = line.trim();
                if (!trim.startsWith("--") && !trim.startsWith("//")) {
                    script.append(line);
                }
                script.append(LINE_SEPARATOR);
            }

            String command = script.toString();
            this.println(command);
            this.executeStatement(command);
            this.commitConnection();
        } catch (Exception var6) {
            line = "Error executing: " + script + ".  Cause: " + var6;
            this.printlnError(line);
            throw new SQLException(line, var6);
        }
    }

    private void executeLineByLine(Reader reader) throws SQLException {
        StringBuilder command = new StringBuilder();

        String line;
        try {
            BufferedReader lineReader = new BufferedReader(reader);

            while ((line = lineReader.readLine()) != null) {
                this.handleLine(command, line);
            }

            this.commitConnection();
            this.checkForMissingLineTerminator(command);
        } catch (Exception var5) {
            line = "Error executing: " + command + ".  Cause: " + var5;
            this.printlnError(line);
            throw new SQLException(line, var5);
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void closeConnection() {
        try {
            this.connection.close();
        } catch (Exception var2) {
        }

    }

    private void setAutoCommit() throws SQLException {
        try {
            if (this.autoCommit != this.connection.getAutoCommit()) {
                this.connection.setAutoCommit(this.autoCommit);
            }

        } catch (Throwable var2) {
            throw new SQLException("Could not set AutoCommit to " + this.autoCommit + ". Cause: " + var2, var2);
        }
    }

    private void commitConnection() throws SQLException {
        try {
            if (!this.connection.getAutoCommit()) {
                this.connection.commit();
            }

        } catch (Throwable var2) {
            throw new SQLException("Could not commit transaction. Cause: " + var2, var2);
        }
    }

    private void rollbackConnection() {
        try {
            if (!this.connection.getAutoCommit()) {
                this.connection.rollback();
            }
        } catch (Throwable var2) {
        }

    }

    private void checkForMissingLineTerminator(StringBuilder command) throws SQLException {
        if (command != null && command.toString().trim().length() > 0) {
            throw new SQLException("Line missing end-of-line terminator (" + this.delimiter + ") => " + command);
        }
    }

    private void handleLine(StringBuilder command, String line) throws SQLException {
        String trimmedLine = line.trim();
        if (this.lineIsComment(trimmedLine)) {
            Matcher matcher = DELIMITER_PATTERN.matcher(trimmedLine);
            if (matcher.find()) {
                this.delimiter = matcher.group(5);
            }

            this.println(trimmedLine);
        } else if (this.commandReadyToExecute(trimmedLine)) {
            command.append(line, 0, line.lastIndexOf(this.delimiter));
            command.append(LINE_SEPARATOR);
            this.println(command);
            this.executeStatement(command.toString());
            command.setLength(0);
        } else if (trimmedLine.length() > 0) {
            command.append(line);
            command.append(LINE_SEPARATOR);
        }

    }

    private boolean lineIsComment(String trimmedLine) {
        return trimmedLine.startsWith("//") || trimmedLine.startsWith("--");
    }

    private boolean commandReadyToExecute(String trimmedLine) {
        return !this.fullLineDelimiter && trimmedLine.contains(this.delimiter) || this.fullLineDelimiter && trimmedLine.equals(this.delimiter);
    }

    private void executeStatement(String command) throws SQLException {
        Statement statement = this.connection.createStatement();
        Throwable var3 = null;

        try {
//            statement.setEscapeProcessing(this.escapeProcessing);
            String sql = command;
            if (this.removeCRs) {
                sql = command.replace("\r\n", "\n");
            }

            try {
                for (boolean hasResults = statement.execute(sql); hasResults || statement.getUpdateCount() != -1; hasResults = statement.getMoreResults()) {
                    this.checkWarnings(statement);
                    this.printResults(statement, hasResults);
                }
            } catch (SQLWarning var16) {
                throw var16;
            } catch (SQLException var17) {
                if (this.stopOnError) {
                    throw var17;
                }

                String message = "Error executing: " + command + ".  Cause: " + var17;
                this.printlnError(message);
            }
        } catch (Throwable var18) {
            var3 = var18;
            throw var18;
        } finally {
            if (statement != null) {
                if (var3 != null) {
                    try {
                        statement.close();
                    } catch (Throwable var15) {
                        var3.addSuppressed(var15);
                    }
                } else {
                    statement.close();
                }
            }

        }

    }

    private void checkWarnings(Statement statement) throws SQLException {
        if (this.throwWarning) {
            SQLWarning warning = statement.getWarnings();
            if (warning != null) {
                throw warning;
            }
        }
    }

    private void printResults(Statement statement, boolean hasResults) {
        if (hasResults) {
            try {
                ResultSet rs = statement.getResultSet();
                Throwable var4 = null;

                try {
                    ResultSetMetaData md = rs.getMetaData();
                    int cols = md.getColumnCount();

                    int i;
                    String value;
                    for (i = 0; i < cols; ++i) {
                        value = md.getColumnLabel(i + 1);
                        this.print(value + "\t");
                    }

                    this.println("");

                    while (rs.next()) {
                        for (i = 0; i < cols; ++i) {
                            value = rs.getString(i + 1);
                            this.print(value + "\t");
                        }

                        this.println("");
                    }
                } catch (Throwable var17) {
                    var4 = var17;
                    throw var17;
                } finally {
                    if (rs != null) {
                        if (var4 != null) {
                            try {
                                rs.close();
                            } catch (Throwable var16) {
                                var4.addSuppressed(var16);
                            }
                        } else {
                            rs.close();
                        }
                    }

                }
            } catch (SQLException var19) {
                this.printlnError("Error printing results: " + var19.getMessage());
            }

        }
    }

    private void print(Object o) {
        if (this.logWriter != null) {
            this.logWriter.print(o);
            this.logWriter.flush();
        }

    }

    private void println(Object o) {
        if (this.logWriter != null) {
            this.logWriter.println(o);
            this.logWriter.flush();
        }

    }

    private void printlnError(Object o) {
        if (this.errorLogWriter != null) {
            this.errorLogWriter.println(o);
            this.errorLogWriter.flush();
        }

    }
}
