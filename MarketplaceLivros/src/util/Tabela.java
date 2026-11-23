package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/** Resultado de uma consulta já pronto para exibir em um JTable. */
public class Tabela {
    private final String[] colunas;
    private final List<Object[]> linhas;

    private Tabela(String[] colunas, List<Object[]> linhas) {
        this.colunas = colunas;
        this.linhas = linhas;
    }

    public static Tabela de(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int n = md.getColumnCount();
        String[] cols = new String[n];
        for (int i = 0; i < n; i++) {
            cols[i] = md.getColumnLabel(i + 1).replace('_', ' ');
        }
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        List<Object[]> linhas = new ArrayList<>();
        while (rs.next()) {
            Object[] l = new Object[n];
            for (int i = 0; i < n; i++) {
                Object v = rs.getObject(i + 1);
                if (v instanceof BigDecimal) {
                    v = ((BigDecimal) v).setScale(2, RoundingMode.HALF_UP).toPlainString();
                } else if (v instanceof Timestamp) {
                    v = fmt.format((Timestamp) v);
                } else if (v == null) {
                    v = "-";
                }
                l[i] = v;
            }
            linhas.add(l);
        }
        return new Tabela(cols, linhas);
    }

    public int tamanho() { return linhas.size(); }

    public Object valor(int linha, int coluna) { return linhas.get(linha)[coluna]; }

    public DefaultTableModel modelo() {
        DefaultTableModel m = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Object[] l : linhas) m.addRow(l);
        return m;
    }
}
