package query.application.view.bean.backing;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.view.rich.component.rich.input.RichInputText;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;


public class QueryMainBean {
    private RichInputText bindSQL;
    private RichInputText pg_SQL_INPUT;
    private RichInputText pg_SQL_OUTPUT;
    final private String sDelimiter = "|";

    public QueryMainBean() {
    }

    private void logmessages(String sDebug) {
        System.out.println(sDebug);
    }

    public Object doExecuteQuery() {

        logmessages("doExecuteQuery START");

        String sQuery = "";
        sQuery = pg_SQL_INPUT.getValue().toString();
        int rowCount = 0;

        logmessages("doExecuteQuery SQL" + sQuery);
        BindingContext context = BindingContext.getCurrent();
        BindingContainer container = context.getCurrentBindingsEntry();
        OperationBinding method_binding = container.getOperationBinding("getSQLresult");
        Map arguments = method_binding.getParamsMap();
        arguments.put("query", sQuery);
        ResultSet oSQLresult;
        oSQLresult = (ResultSet) method_binding.execute();
        if (oSQLresult != null) {
            logmessages("oSQLresult not null");
            logmessages(oSQLresult.toString());
            StringBuffer OutputBuffer = new StringBuffer();
            StringBuffer rowcount = new StringBuffer();
            ResultSetMetaData rsmd;
            try {
                rsmd = oSQLresult.getMetaData();
                logmessages("get meta data oSQLresult");
                int columnCount = rsmd.getColumnCount();
                String columnname = "";
                String temp_string = "";

                for (int i = 0; i < columnCount; i++) {
                    columnname = rsmd.getColumnName(i + 1);
                    OutputBuffer.append(columnname);
                    OutputBuffer.append(sDelimiter);
                }
                OutputBuffer.append(System.getProperty("line.separator"));

                while (oSQLresult.next()) {
                    for (int i = 0; i < columnCount; i++) {
                        temp_string = removeunwantedchar(oSQLresult.getString(i + 1));
                        OutputBuffer.append(temp_string);
                        OutputBuffer.append(sDelimiter);
                    }
                    rowCount++;
                    OutputBuffer.append(System.getProperty("line.separator"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    oSQLresult.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            rowcount.append("Row Count :" + rowCount);
            
            OutputBuffer=rowcount.append(System.getProperty("line.separator")).append(OutputBuffer);
            pg_SQL_OUTPUT.setRows(rowCount);
            pg_SQL_OUTPUT.setValue(OutputBuffer.toString());
        } 
        
        {
            FacesMessage Message = new FacesMessage("Execution Completed.");
            Message.setSeverity(FacesMessage.SEVERITY_INFO);
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, Message);
        }
        logmessages("doExecuteQuery END");
        return null;
    }

    public static String removeunwantedchar(String Input) {
        if(Input!=null)
        {
        Input = Input.replaceAll("[\\n\\t ]", "");
        Input = Input.replaceAll("|", "");
        }
        return Input;
    }

    public void setBindSQL(RichInputText bindSQL) {
        this.bindSQL = bindSQL;
    }

    public RichInputText getBindSQL() {
        return bindSQL;
    }

    public void setPg_SQL_INPUT(RichInputText pg_SQL_INPUT) {
        this.pg_SQL_INPUT = pg_SQL_INPUT;
    }

    public RichInputText getPg_SQL_INPUT() {
        return pg_SQL_INPUT;
    }

    public void setPg_SQL_OUTPUT(RichInputText pg_SQL_OUTPUT) {
        this.pg_SQL_OUTPUT = pg_SQL_OUTPUT;
    }

    public RichInputText getPg_SQL_OUTPUT() {
        return pg_SQL_OUTPUT;
    }
}
